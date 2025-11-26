package com.shemuel.timeline.notify;

import java.util.Map;

public interface NotifyStrategy {
    NotifyTestResult  sendTest(String url, Map<String,Object> params);
}
