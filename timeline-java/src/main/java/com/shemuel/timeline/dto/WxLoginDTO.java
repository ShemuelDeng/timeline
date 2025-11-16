// WxLoginDTO.java
package com.shemuel.timeline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class WxLoginDTO {

    /**
     * 小程序端通过 wx.login 拿到的临时 code
     */
    @NotBlank(message = "code 不能为空")
    private String code;

    /**
     * 头像地址（前端从 getUserProfile 中带过来）
     */
    private String avatar;

    /**
     * 昵称（前端从 getUserProfile 中带过来）
     */
    private String nickname;
}
