package com.shemuel.timeline.notify;

import lombok.Data;

// 可以放在 notify/dto 目录下
@Data
public class NotifyTestResult {
    /**
     * 渠道类型：bark / dingding / wecom
     */
    private String type;

    /**
     * 本次测试是否认为成功（errcode=0 / http 200 等）
     */
    private boolean success;

    /**
     * 被调用方返回的原始响应（字符串或 JSON 串）
     */
    private String rawResponse;

    /**
     * 简单的人类可读提示，例如：“发送成功”/“钉钉返回错误码 310000”
     */
    private String message;
}
