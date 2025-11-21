package com.shemuel.timeline.service;

import com.shemuel.timeline.config.UtoolsProperties;
import com.shemuel.timeline.dto.UtoolsBaseInfoResponse;
import com.shemuel.timeline.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UtoolsOpenApiClient {

    private final UtoolsProperties props;
    private final RestTemplate restTemplate;

    public UtoolsOpenApiClient(UtoolsProperties props) {
        this.props = props;
        this.restTemplate = new RestTemplate();
    }

    public UtoolsBaseInfoResponse getBaseInfo(String accessToken) {
        long ts = System.currentTimeMillis() / 1000;

        // 1. 按文档要求的三个参数，放进有序 Map（key 升序）
        SortedMap<String, String> signParams = new TreeMap<>();
        signParams.put("access_token", accessToken);
        signParams.put("plugin_id", props.getPluginId());
        signParams.put("timestamp", String.valueOf(ts));

        // 2. 构造签名用字符串（等价 http_build_query）
        String signBase = buildQueryPhpStyle(signParams);

        // 3. 计算 sign（等价 hash_hmac）
        String sign = hmacSha256Hex(signBase, props.getSecret());

        // 4. 把 sign 也加进请求参数里
        SortedMap<String, String> allParams = new TreeMap<>(signParams);
        allParams.put("sign", sign);

        // 5. 用我们自己的方式拼 URL（不要再用 UriComponentsBuilder 来参与签名）
        String query = allParams.entrySet().stream()
                .map(e -> e.getKey() + "=" + urlEncodePhpStyle(e.getValue()))
                .collect(Collectors.joining("&"));

        String url = "https://open.u-tools.cn/baseinfo" + "?" + query;

        // 打日志便于你本地对照
        log.info("uTools baseinfo url = {}", url);
        log.info("uTools signBase = {}", signBase);
        log.info("uTools sign = {}", sign);

        ResponseEntity<UtoolsBaseInfoResponse> resp =
                restTemplate.getForEntity(url, UtoolsBaseInfoResponse.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new ServiceException("调用 uTools baseinfo 失败, httpStatus=" + resp.getStatusCode());
        }

        return resp.getBody();
    }





    private String urlEncodePhpStyle(String value) {
        // PHP http_build_query 默认用 urlencode：
        // - 空格 -> +
        // - 安全字符不编码，其他转 %XX
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new RuntimeException("URL 编码失败", e);
        }
    }

    /**
     * 等价于 PHP: hash_hmac("sha256", $str, $secret)
     */
    private String hmacSha256Hex(String data, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);
            byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));   // 小写十六进制
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算 HMAC-SHA256 失败", e);
        }
    }

    /**
     * 等价于 PHP: ksort + http_build_query
     */
    private String buildQueryPhpStyle(SortedMap<String, String> params) {
        // TreeMap 已经是按 key 升序
        return params.entrySet().stream()
                .map(e -> e.getKey() + "=" + urlEncodePhpStyle(e.getValue()))
                .collect(Collectors.joining("&"));
    }

}
