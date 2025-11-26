package com.shemuel.timeline.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DingTalkRobotTool {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 无签名普通调用，返回钉钉响应字符串
     */
    public String sendTextByUrl(String webhookUrl, String content) {
        return doSend(webhookUrl, content);
    }
    /**
     * 有签名调用：根据 secret 生成 sign + timestamp 后再发送
     */
    public String sendWithSign(String webhookUrl, String secret, String content) {
        try {
            long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(Base64.getEncoder().encodeToString(signData), "UTF-8");

            String realUrl = webhookUrl + "&timestamp=" + timestamp + "&sign=" + sign;
            return doSend(realUrl, content);
        } catch (Exception e) {
            log.error("DingTalkRobotTool sign error", e);
            return "ERROR: sign generate failed: " + e.getMessage();
        }
    }

    private String doSend(String url, String content) {
        if (!StringUtils.hasText(url)) {
            log.warn("DingTalkRobotTool.send skip, url is blank");
            return "url is blank";
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("msgtype", "text");

            Map<String, String> text = new HashMap<>();
            text.put("content", content);
            body.put("text", text);

            String resp = restTemplate.postForObject(url, body, String.class);
            log.info("DingTalkRobotTool send success, resp={}", resp);
            return resp;
        } catch (Exception e) {
            log.error("DingTalkRobotTool send error, url={}, err={}", url, e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }
}
