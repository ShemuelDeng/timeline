// WxSessionResponse.java
package com.shemuel.timeline.dto;

import lombok.Data;

@Data
public class WxSessionResponse {

    private String openid;
    private String session_key;
    private String unionid;

    private Integer errcode;
    private String errmsg;
}
