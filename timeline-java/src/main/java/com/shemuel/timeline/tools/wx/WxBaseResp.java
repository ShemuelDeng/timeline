package com.shemuel.timeline.tools.wx;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author zhangdong
 * @date 2022/5/6 15:20:38
 */
@NoArgsConstructor
@Data
public class WxBaseResp {

    @JSONField(name = "errcode")
    private Integer errcode;
    @JSONField(name = "errmsg")
    private String errmsg;

    public boolean hasSuccess(){
        return Objects.equals(getErrcode(), 0);
    }

    public static WxBaseResp getInstance(){
        return new WxBaseResp();
    }
}
