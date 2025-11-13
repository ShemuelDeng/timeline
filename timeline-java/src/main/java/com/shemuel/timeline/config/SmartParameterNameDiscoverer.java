package com.shemuel.timeline.config;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartParameterNameDiscoverer implements ParameterNameDiscoverer {

    // Spring 内置的反射解析器（如果有 -parameters，会直接返回真实参数名）
    private final StandardReflectionParameterNameDiscoverer reflectionDiscoverer =
            new StandardReflectionParameterNameDiscoverer();

    // 缓存结果
    private final Map<Method, String[]> cache = new ConcurrentHashMap<>();

    @Override
    public String[] getParameterNames(Method method) {
        // 先查缓存
        return cache.computeIfAbsent(method, this::resolveParameterNames);
    }

    private String[] resolveParameterNames(Method method) {
        // 尝试用反射获取真实参数名
        String[] names = reflectionDiscoverer.getParameterNames(method);
        if (names != null && Arrays.stream(names).noneMatch(n -> n.startsWith("arg"))) {
            return names;
        }

        // 没有真实参数名，就从 Method 对象的 Parameter 里获取
        Parameter[] parameters = method.getParameters();
        String[] result = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            // 如果有 @RequestParam 或 @PathVariable 之类注解，也可以在这里提取
            if (p.isNamePresent()) {
                result[i] = p.getName();
            } else {
                // 默认名
                result[i] = "param" + i;
            }
        }
        return result;
    }

    @Override
    public String[] getParameterNames(java.lang.reflect.Constructor<?> ctor) {
        return reflectionDiscoverer.getParameterNames(ctor);
    }
}
