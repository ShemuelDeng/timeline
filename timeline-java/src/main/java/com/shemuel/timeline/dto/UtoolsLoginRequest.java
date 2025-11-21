package com.shemuel.timeline.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// 前端给后端的请求体
@Data
public class UtoolsLoginRequest {
    @Schema(description = "utools.fetchUserServerTemporaryToken() 获取的 token")
    private String token;
}
