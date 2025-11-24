package com.shemuel.timeline.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DingTalkRobotTool {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送文本消息到钉钉机器人
     *
     * @param webhookUrl 钉钉机器人 webhook
     * @param content    文本内容
     */
    public void sendTextByUrl(String webhookUrl, String content) {
        if (!StringUtils.hasText(webhookUrl)) {
            log.warn("DingTalkRobotTool.sendTextByUrl skip, webhookUrl is blank");
            return;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("msgtype", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            body.put("text", text);

            String resp = restTemplate.postForObject(webhookUrl, body, String.class);
            log.debug("DingTalkRobotTool send success, resp={}", resp);
        } catch (Exception e) {
            log.error("DingTalkRobotTool send error, url={}, err={}", webhookUrl, e.getMessage(), e);
            throw e;
        }
    }
}
