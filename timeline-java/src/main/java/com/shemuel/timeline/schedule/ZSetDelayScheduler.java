package com.shemuel.timeline.schedule;

import com.shemuel.timeline.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于ZSet实现的延时任务调度器实现
 *
 * @author dengsx
 * @date 2024/05/24
 */
@Slf4j
public abstract class ZSetDelayScheduler implements Runnable, InitializingBean, DisposableBean {

    /**
     * 超时任务set key超时时间，任务的超时时间不能超过此时间，否则会造成调度不到的问题
     */
    private final static int KEY_EXPIRE_MINUTES = 150;

    private final static int MINUTES_FACTOR = 60 * 1000;

    private final static String DELAY_REDIS_KEY_PREFIX = "delay:task";

    private String zSetKey;

    private volatile boolean runningFlag = true;

    private Thread executeThread;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void afterPropertiesSet() {
        zSetKey = DELAY_REDIS_KEY_PREFIX + zSetKey();

        executeThread = new Thread(this, zSetKey);
        executeThread.start();
        log.info("延时任务调度器已启动：{}", zSetKey);
    }

    /**
     * 延时任务队列名称
     *
     * @return
     */
    protected abstract String zSetKey();

    /**
     * 任务延时时长时间的换算倍数，默认以分钟精度换算，可重写自定义
     *
     * @return
     */
    protected Integer timeFactor() {
        return 60 * 1000;
    }

    /**
     * 延时任务的redis key 过期时间 默认150分钟 ，可重写自定义
     *
     * @return
     */
    protected Integer delayKeyExpireTime() {
        return KEY_EXPIRE_MINUTES;
    }

    /**
     * 检查任务是否已存在
     *
     * @param taskPayloads
     * @return
     */
    public boolean exist(String... taskPayloads) {
        return Objects.nonNull(redisTemplate.opsForZSet().score(zSetKey, getZSetValue(taskPayloads)));
    }

    /**
     * 添加延时任务，重复任务覆盖并更新延时时间
     *
     * @param delay 任务延时时间
     * @param taskPayloads
     */
    protected void schedule(Integer delay, String... taskPayloads) {
        if (delay * timeFactor() >= (delayKeyExpireTime() * MINUTES_FACTOR - timeFactor())) {
            log.error("定时调度设置的时间太大了: {}, {}, [{}]", zSetKey, delay, String.join(", ", taskPayloads));
        }

        Long score = System.currentTimeMillis() + delay * timeFactor();
        redisTemplate.opsForZSet().add(zSetKey, getZSetValue(taskPayloads), score);
        redisTemplate.expire(zSetKey, delayKeyExpireTime(), TimeUnit.MINUTES);
        log.info("定时任务添加: {}, {}, [{}]", zSetKey, delay, String.join(", ", taskPayloads));
    }

    /**
     * 添加指定时间的调度的任务
     *
     * @param time 任务到期时间
     * @param taskPayloads
     */
    protected void schedule(Long time, String... taskPayloads) {
        redisTemplate.opsForZSet().add(zSetKey, getZSetValue(taskPayloads), time);
        redisTemplate.expire(zSetKey, delayKeyExpireTime(), TimeUnit.MINUTES);
        log.info("添加定时任务: {}, {},{}", zSetKey, String.join(", ", taskPayloads), DateUtil.fromTimestamp(time));
    }

    /**
     * 添加延时任务，重复任务忽略
     *
     * @param delay
     * @param taskPayloads
     */
    protected boolean scheduleIfAbsent(Integer delay, String... taskPayloads) {
        if (delay * timeFactor() >= (delayKeyExpireTime() * MINUTES_FACTOR - timeFactor())) {
            log.error("定时调度设置的时间太大了: {}, {}, [{}]", zSetKey, delay, String.join(", ", taskPayloads));
        }

        String zSetValue = getZSetValue(taskPayloads);

        Double existingScore = redisTemplate.opsForZSet().score(zSetKey, zSetValue);
        if(Objects.isNull(existingScore)) {
            Long score = System.currentTimeMillis() + delay * timeFactor();
            redisTemplate.opsForZSet().add(zSetKey, zSetValue, score);
            redisTemplate.expire(zSetKey, delayKeyExpireTime(), TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    /**
     * 移除延时任务
     *
     * @param taskPayloads
     */
    public void cancelSchedule(String... taskPayloads) {
        Long remove = redisTemplate.opsForZSet().remove(zSetKey, getZSetValue(taskPayloads));
        log.info("cancelSchedule {}, {}", getZSetValue(taskPayloads), remove);
    }

    private String getZSetValue(String... taskPayloads) {
        return String.join(":", taskPayloads);
    }

    @Override
    public void run() {
        while (runningFlag) {
            try {
                Long now = System.currentTimeMillis();
                List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                    @Override
                    public List<Object> execute(RedisOperations operations) throws DataAccessException {
                        operations.multi();
                        operations.opsForZSet().rangeByScore(zSetKey, 0, now);
                        operations.opsForZSet().removeRangeByScore(zSetKey, 0, now);
                        return operations.exec();
                    }
                });

                Set<String> zSetValues = (Set) results.get(0);
                if (log.isDebugEnabled()) {
                    log.debug("到期任务数-{}：{}", zSetKey, zSetValues.size());
                }
                if (CollectionUtils.isEmpty(zSetValues)) {
                    continue;
                }

                for (String zSetValue : zSetValues) {
                    log.info("任务到期：{}，{}", zSetKey, zSetValue);
                    try {
                        timeToRun(zSetValue.split(":"));
                    } catch (Exception e) {
                        log.error("执行任务时异常，{}，{}，{}", zSetKey, zSetValue, ExceptionUtils.getStackTrace(e));
                    }
                }
            } catch (Throwable e) {
                log.error("获取并执行到期任务时异常，{}", zSetKey, e);
            } finally {
                try {
                    long sleepTime = timeFactor() > 1000 ? 1000 : 500;
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                } catch (InterruptedException ignore) { }
            }
        }
    }

    /**
     * 延时任务到期执行逻辑
     *
     * @param taskPayloads
     */
    protected abstract void timeToRun(String[] taskPayloads);

    /**
     * 程序结束销毁定时器
     */
    @Override
    public void destroy() {
        runningFlag = false;
        log.info("ZSetDelayScheduler {} destroyed.", zSetKey);
    }
}
