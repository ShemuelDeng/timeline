package com.shemuel.timeline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AiQuotaService {

    private static final int DAILY_LIMIT = 3;
    private static final String KEY_PREFIX = "ai:remind:quota:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 尝试消耗一次 AI 创建额度
     *
     * @param userId 用户ID
     * @return true = 本次可以使用（额度已成功占用1次）
     *         false = 当日已无可用次数
     */
    public boolean tryUseOnce(Long userId) {
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        LocalDate today = LocalDate.now(zone);
        String dateStr = today.format(DateTimeFormatter.BASIC_ISO_DATE); // 20251130

        String key = KEY_PREFIX + userId + ":" + dateStr;

        // 自增一次，Redis 原子操作
        Long used = redisTemplate.opsForValue().increment(key);
        if (used == null) {
            // 理论上不会为 null，防御一下
            return false;
        }

        // 如果是今天第一次使用，设置过期时间为当天 24 点
        if (used == 1L) {
            LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
            long seconds = Duration.between(LocalDateTime.now(zone), tomorrowStart).getSeconds();
            if (seconds > 0) {
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
        }

        // 超出当日限额
        if (used > DAILY_LIMIT) {
            return false;
        }

        return true;
    }

    /**
     * 查询剩余额度（可选）
     */
    public long getRemaining(Long userId) {
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        LocalDate today = LocalDate.now(zone);
        String dateStr = today.format(DateTimeFormatter.BASIC_ISO_DATE);

        String key = KEY_PREFIX + userId + ":" + dateStr;

        Object val = redisTemplate.opsForValue().get(key);
        long used = 0;
        if (val instanceof Number) {
            used = ((Number) val).longValue();
        }
        long remaining = DAILY_LIMIT - used;
        return Math.max(remaining, 0);
    }
}
