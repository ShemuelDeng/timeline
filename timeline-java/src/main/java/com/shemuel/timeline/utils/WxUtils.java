package com.shemuel.timeline.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shemuel.timeline.tools.wx.WebhookSendMsgReq;
import com.shemuel.timeline.tools.wx.WxBaseResp;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangdong
 * @date 2025/2/21 10:41
 */
@Slf4j
public class WxUtils {

    public static WxBaseResp sendWebhookMsg(String hookKey, WebhookSendMsgReq webhookSendMsgReq) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + hookKey;
        WxBaseResp wxBaseResp = JSON.parseObject(HttpUtil.post(url, JSONObject.toJSONString(webhookSendMsgReq)), WxBaseResp.class);
        if(!wxBaseResp.hasSuccess()){
            log.warn("sendWebhookMsgError|{}|{}|{}", hookKey, JSONObject.toJSONString(webhookSendMsgReq), wxBaseResp.getErrmsg());
        }
        return wxBaseResp;
    }


    public static WxBaseResp sendWebhookMsgByUrl(String url, WebhookSendMsgReq webhookSendMsgReq) {

        WxBaseResp wxBaseResp = JSON.parseObject(HttpUtil.post(url, JSONObject.toJSONString(webhookSendMsgReq)), WxBaseResp.class);
        if(!wxBaseResp.hasSuccess()){
            log.warn("sendWebhookMsgError|{}|{}|{}", url, JSONObject.toJSONString(webhookSendMsgReq), wxBaseResp.getErrmsg());
        }
        return wxBaseResp;
    }
}
