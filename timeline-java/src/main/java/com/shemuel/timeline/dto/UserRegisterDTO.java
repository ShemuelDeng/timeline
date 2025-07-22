package com.shemuel.timeline.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-12-11:10
 * @Description:
 */
@Data
public class UserRegisterDTO {

    @Schema(description = "用户昵称", example = "shemuel")
    private String username;

    /**
     * sha256加密后的密码
     */
    @Schema(description = "sha256加密后的密码", example = "xxx")
    private String password;

    @NotNull
    @NotEmpty
    @Schema(description = "邮箱", example = "xxx@qq.com")
    private String email;

    @Schema(description = "手机号", example = "12345678901")
    private String phone;
}
