package com.shemuel.timeline.controller;

import com.alibaba.fastjson.JSON;
import com.shemuel.timeline.annotation.AccessLimit;
import com.shemuel.timeline.common.RestResult;
import com.shemuel.timeline.tools.wx.AccessTokenDTO;
import com.shemuel.timeline.tools.wx.PropertyVO;
import com.shemuel.timeline.tools.wx.WxSubscribeMsgRequestVO;
import com.shemuel.timeline.tools.wx.WxWeatherNoticeDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-11-20:26
 * @Description:
 */
@RestController
@AccessLimit
@Slf4j
public class HealthyController {

    @Value("${wx.appid}")
    private String appid;


    @Value("${wx.secret}")
    private String secret;


    @Value("${wx.templateId.notice}")
    private String noticeTemplateId;


    @Value("${wx.templateId.weather}")
    private String weatherTemplateId;


    @Value("${wx.openIds}")
    private String openIds;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/ok")
    public RestResult<String> healthy() {



        WxWeatherNoticeDataVO data = new WxWeatherNoticeDataVO();

        data.setPhrase2(PropertyVO.init("上海",null));
        data.setPhrase3(PropertyVO.init("晴转多云",null));
        data.setCharacter_string4(PropertyVO.init("1~6°",null));
        data.setCharacter_string21(PropertyVO.init("1%",null));
        data.setThing5(PropertyVO.init("适合户外出行",null));



        WxSubscribeMsgRequestVO requestVO1 = new WxSubscribeMsgRequestVO();
        requestVO1.setTouser("otZuA1_LzqCicrfYveMJU5er5wXg");
        requestVO1.setTemplate_id(weatherTemplateId);
        requestVO1.setData(data);
        String json = JSON.toJSONString(requestVO1);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity formEntity = new HttpEntity(json, headers);
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + getAccessToken().getAccess_token();
        Object result = restTemplate.postForObject(url, formEntity, Object.class);
        log.info("推送结果: {},{}", "openId", result);

        return RestResult.success("Yes, I'm ok!");
    }

    private AccessTokenDTO getAccessToken() {

        String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", appid, secret);
        return restTemplate.getForObject(url, AccessTokenDTO.class);
    }
}
