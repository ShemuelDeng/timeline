package com.shemuel.timeline.utils;

import com.shemuel.timeline.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class CronUtil {

    /** 最小允许间隔：60 秒 */
    private static final long MIN_INTERVAL_SECONDS = 60L;

    /**
     * 兼容 5 / 6 段 cron，统一转换为 Spring 使用的 6 段格式，
     * 并顺带做语法校验 + 最小间隔校验。
     *
     * @param rawExpr 用户输入的原始 cron 串（可能 5 段，也可能 6 段）
     * @return 经过标准化后的 6 段 cron 表达式（秒 分 时 日 月 星期）
     */
    public static String normalizeAndValidate(String rawExpr) {
        if (rawExpr == null || rawExpr.trim().isEmpty()) {
            throw new ServiceException("Cron 表达式不能为空");
        }

        // 1. 规范空白字符
        String expr = rawExpr.trim().replaceAll("\\s+", " ");
        String[] parts = expr.split(" ");

        // 2. 5 段 -> 补一个 0 到最前面（秒）
        if (parts.length == 5) {
            expr = "0 " + expr;
        } else if (parts.length == 6) {
            // 正常 Spring 风格，直接用
        } else {
            throw new ServiceException("Cron 表达式必须是 5 或 6 段，请检查");
        }

        // 3. 交给 Spring CronExpression 做语法校验
        CronExpression cron;
        try {
            cron = CronExpression.parse(expr);
        } catch (Exception e) {
            log.warn("解析 Cron 失败, raw={}, normalized={}", rawExpr, expr, e);
            throw new ServiceException("非法的 Cron 表达式，请检查语法");
        }

        // 4. 计算两次最近执行时间的间隔，确保 >= 60 秒
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime first = cron.next(now);
        if (first == null) {
            throw new ServiceException("Cron 表达式在未来不会触发，请检查配置");
        }
        LocalDateTime second = cron.next(first);
        if (second != null) {
            long seconds = Duration.between(first, second).getSeconds();
            if (seconds < MIN_INTERVAL_SECONDS) {
                throw new ServiceException("cron表达式循环间隔不能少于 60 秒");
            }
        }

        // 一切 OK，返回标准化后的表达式
        return expr;
    }
}
