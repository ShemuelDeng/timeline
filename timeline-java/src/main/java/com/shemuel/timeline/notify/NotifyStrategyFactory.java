package com.shemuel.timeline.notify;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotifyStrategyFactory {

    private final Map<String, NotifyStrategy> strategyMap;

    public NotifyStrategy get(String type) {
        NotifyStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("未知的通知类型: " + type);
        }
        return strategy;
    }
}
