package com.shemuel.timeline.tools.wx;

import com.shemuel.timeline.utils.WxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeComRobotTool {

    @Value("${wx.corp.group.robot-key}")
    private String robotKey;

    @Tool(name = "sendGroupTool", description = "发送消息到企微群组")
    public WxBaseResp sendGroupMessage(@ToolParam(description = "The message to send") String message) {
        // 使用 webhookUrl 调用微信群机器人的 HTTP 接口
        log.info("sendGroupMessage invoked {}",  message);
        WebhookSendMsgReq webhookSendMsgReq = WebhookSendMsgReq.markdown(message);
        return WxUtils.sendWebhookMsg(robotKey,webhookSendMsgReq);
    }
}
