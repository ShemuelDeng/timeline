package com.shemuel.timeline.dto;

import lombok.Data;

import java.util.Map;

@Data
public class NotifyTestReq {
    private String type;            // bark / dingding / wecom
    private String url;             // webhook 或 bark url
    private Map<String, Object> paramsObj;  // 内容、secret 等
}
