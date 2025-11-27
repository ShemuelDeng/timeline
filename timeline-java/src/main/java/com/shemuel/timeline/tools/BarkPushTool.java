package com.shemuel.timeline.tools;

import com.shemuel.timeline.notify.NotifyTestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class BarkPushTool {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过 Bark 推送，使用 GET 方式
     *
     * @param baseUrl Bark 基础地址，例如 https://api.day.app/xxxxx 或末尾带 /
     * @param title   标题
     * @param body    内容
     */
    public String push(String baseUrl, String title, String body) {
        if (!StringUtils.hasText(baseUrl)) {
            log.warn("BarkPushTool.push skip, baseUrl is blank");
            return "baseUrl is blank";
        }
        try {
            String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            String url = cleanBase + "/" + title + "/" + body;

            String resp = restTemplate.getForObject(url, String.class);
            log.info("BarkPushTool push success, url={}, resp={}", url, resp);
            return resp;
        } catch (Exception e) {
            log.error("BarkPushTool push error, baseUrl={}, err={}", baseUrl, e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }

    private String urlEncode(String s) throws UnsupportedEncodingException {
        if (s == null) {
            return "";
        }
        return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
    }
}
