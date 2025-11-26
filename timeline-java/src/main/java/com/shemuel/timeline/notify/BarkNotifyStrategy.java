package com.shemuel.timeline.notify;

import com.shemuel.timeline.tools.BarkPushTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("bark")
@RequiredArgsConstructor
public class BarkNotifyStrategy implements NotifyStrategy {

    private final BarkPushTool barkPushTool;
    @Override
    public NotifyTestResult sendTest(String url, Map<String, Object> params) {
        String content = (String) params.getOrDefault("content", "灵犀提醒-测试通知");
        String resp = barkPushTool.push(url, "灵犀提醒-测试通知", content);

        NotifyTestResult result = new NotifyTestResult();
        result.setType("bark");
        result.setRawResponse(resp);

        boolean ok = resp != null && !resp.startsWith("ERROR");
        result.setSuccess(ok);
        result.setMessage(ok ? "Bark 测试发送成功" : "Bark 测试发送失败");
        return result;
    }
}
