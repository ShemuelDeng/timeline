package com.shemuel.timeline.notify;

import com.shemuel.timeline.tools.wx.WeComRobotTool;
import com.shemuel.timeline.tools.wx.WxBaseResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("wecom")
@RequiredArgsConstructor
public class WeComNotifyStrategy implements NotifyStrategy {

    private final WeComRobotTool weComRobotTool;

    @Override
    public NotifyTestResult sendTest(String url, Map<String, Object> params) {
        String content = (String) params.getOrDefault("content", "灵犀提醒-测试通知");
        WxBaseResp resp = weComRobotTool.sendGroupMessageByUrl(url, content);

        NotifyTestResult result = new NotifyTestResult();
        result.setType("wecom");

        // 假设 WxBaseResp 有 errcode / errmsg 字段
        boolean ok = resp != null && resp.getErrcode() == 0;
        result.setSuccess(ok);
        result.setRawResponse(resp != null ? resp.toString() : "null");
        result.setMessage(ok ? "企业微信测试发送成功" : "企业微信测试失败, errcode=" + (resp != null ? resp.getErrcode() : "null"));
        return result;
    }
}
