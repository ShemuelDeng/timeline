package com.shemuel.timeline.dto;

import lombok.Data;

@Data
public class UtoolsBaseInfoResponse {

    @Data
    public static class Resource {
        private String avatar;
        private Integer member;
        private String nickname;
        private String open_id;
        private Long timestamp;
    }

    private Resource resource;
    private String sign; // utools 返回的签名，可选校验
}
