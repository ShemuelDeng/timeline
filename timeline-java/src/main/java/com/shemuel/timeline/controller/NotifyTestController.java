package com.shemuel.timeline.controller;

import com.shemuel.timeline.common.RestResult;
import com.shemuel.timeline.dto.NotifyTestReq;
import com.shemuel.timeline.notify.NotifyStrategy;
import com.shemuel.timeline.notify.NotifyStrategyFactory;
import com.shemuel.timeline.notify.NotifyTestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
@Slf4j
public class NotifyTestController {

    private final NotifyStrategyFactory factory;

    @PostMapping("/test")
    public RestResult<Object> testChannel(@RequestBody NotifyTestReq req) {
        try {
            NotifyStrategy strategy = factory.get(req.getType());
            NotifyTestResult notifyTestResult = strategy.sendTest(req.getUrl(), req.getParamsObj());
            return RestResult.success(notifyTestResult);
        } catch (Exception e) {
            log.error("测试失败：" + e.getMessage(), e);
            NotifyTestResult failResult = new NotifyTestResult();
            failResult.setMessage("测试失败：" + e.getMessage());
            failResult.setSuccess(false);
            return RestResult.success(failResult);
        }
    }
}
