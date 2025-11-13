package com.shemuel.timeline.aop;

import com.shemuel.timeline.annotation.AccessLimit;
import com.shemuel.timeline.common.RestResult;
import cn.dev33.satoken.stp.StpUtil;
import com.shemuel.timeline.service.IRateLimiter;
import com.shemuel.timeline.utils.RateLimiterFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class AccessLimitAspect {

    @Autowired
    private RateLimiterFactory rateLimiterFactory;

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 环绕通知 — 延后执行 SpEL 解析（确保 @RequestBody 已反序列化）
     */
    @Around("within(@com.shemuel.timeline.annotation.AccessLimit *) && within(com.shemuel.timeline.controller..*) || " +
            "@annotation(com.shemuel.timeline.annotation.AccessLimit) && within(com.shemuel.timeline.controller..*)")
    public Object limitAccess(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = AopUtils.getMostSpecificMethod(signature.getMethod(), joinPoint.getTarget().getClass());
        Object target = joinPoint.getTarget();

        // 获取注解（方法优先）
        AccessLimit methodAnnotation = AnnotationUtils.findAnnotation(method, AccessLimit.class);
        AccessLimit classAnnotation = AnnotationUtils.findAnnotation(target.getClass(), AccessLimit.class);
        AccessLimit annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        if (annotation == null) {
            return joinPoint.proceed();
        }

        // ✅ 再解析 SpEL 表达式
        String keyValue = parseKey(annotation.key(), joinPoint, method);

        String accessGroup = getAccessGroup(annotation.group(),
                target.getClass().getName(), method.getName());
        log.info("限流检测 => group: {}, key: {}", accessGroup, keyValue);

        IRateLimiter rateLimiter = rateLimiterFactory.getRateLimiter(accessGroup);
        if (rateLimiter != null && !rateLimiter.isGranted(keyValue)) {
            log.warn("限流触发 => group: {}, key: {}", accessGroup, keyValue);
            return RestResult.error(annotation.msg());
        }

        return
                 joinPoint.proceed();
    }

    /**
     * 解析 SpEL 表达式（在参数已反序列化后执行）
     */
    private String parseKey(String keyRegex, ProceedingJoinPoint joinPoint, Method method) {
        if (StringUtils.isNotEmpty(keyRegex)) {
            Object target = joinPoint.getTarget();
            Object[] args = joinPoint.getArgs();

            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                    target, method, args, new DefaultParameterNameDiscoverer());

            try {
                Expression expression = PARSER.parseExpression(keyRegex);
                String value = expression.getValue(context, String.class);
                if (StringUtils.isNotEmpty(value)) {
                    return value;
                }
            } catch (Exception e) {
                log.error("SpEL 表达式解析失败: {}, error: {}", keyRegex, e.getMessage());
            }

            // 若 SpEL 解析失败，则直接使用原字符串
            return keyRegex;
        }

        // 无 key 情况：已登录按用户维度限流
        if (StpUtil.isLogin()) {
            return StpUtil.getLoginIdAsString();
        }

        // 未登录时，按类 + 方法维度限流
        return joinPoint.getTarget().getClass().getName() + "#" + method.getName();
    }

    private String getAccessGroup(String group, String className, String methodName) {
        return StringUtils.isEmpty(group) ? className + "#" + methodName : group;
    }
}
