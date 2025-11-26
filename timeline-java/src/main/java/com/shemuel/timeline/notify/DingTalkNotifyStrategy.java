package com.shemuel.timeline.notify;

import com.shemuel.timeline.tools.DingTalkRobotTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component("dingding")
@RequiredArgsConstructor
public class DingTalkNotifyStrategy implements NotifyStrategy {

    private final DingTalkRobotTool dingTalkRobotTool;

    @Override
    public NotifyTestResult sendTest(String url, Map<String, Object> params) {
        String content = (String) params.getOrDefault("content", "灵犀提醒-测试通知");
        String secret = (String) params.get("secret");

        String resp;
        if (StringUtils.hasText(secret)) {
            resp = dingTalkRobotTool.sendWithSign(url, secret, content);
        } else {
            resp = dingTalkRobotTool.sendTextByUrl(url, content);
        }

        NotifyTestResult result = new NotifyTestResult();
        result.setType("dingding");
        result.setRawResponse(resp);

        // 钉钉正常返回形如 {"errcode":0,"errmsg":"ok"}
        boolean ok = resp != null && resp.contains("\"errcode\":0");
        result.setSuccess(ok);
        result.setMessage(ok ? "钉钉测试发送成功" : "钉钉测试失败");
        return result;
    }
}
