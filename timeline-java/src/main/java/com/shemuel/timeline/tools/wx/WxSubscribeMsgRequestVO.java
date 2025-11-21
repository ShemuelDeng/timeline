package com.shemuel.timeline.tools.wx;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: dengshaoxiang
 * @Date: 2025-11-18-16:23
 * @Description:
 */
@Data
@Accessors(chain = true)
public class WxSubscribeMsgRequestVO {

    /**
     * 接收者openId
     */
    private String touser;
    /**
     * 模板id
     */
    private String template_id;
    /**
     * 微信接口默认地址
     */
    private String url = "http://weixin.qq.com/download";
    /**
     * 背景颜色
     */
    private String topcolor = "#FF0000";
    /**
     * 参数对象
     */
    private Object data;
}
