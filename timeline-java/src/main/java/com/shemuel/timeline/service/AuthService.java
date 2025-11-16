package com.shemuel.timeline.service;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shemuel.timeline.dto.WxLoginDTO;
import com.shemuel.timeline.dto.WxSessionResponse;
import com.shemuel.timeline.entity.UserProfile;
import com.shemuel.timeline.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-13-14:53
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;


    @Value("${wx.appid}")
    private String wxAppId;

    @Value("${wx.secret}")
    private String wxAppSecret;

    @Resource
    private RestTemplate restTemplate;


    // 你原来的 login / checkPassword 省略...

    /**
     * 小程序微信登录
     */
    public UserProfile loginByWeixin(WxLoginDTO dto) {
        // 1. 用 code 调微信 jscode2session 换 openid
        WxSessionResponse wxSession = getSessionByCode(dto.getCode());

        if (wxSession.getErrcode() != null && wxSession.getErrcode() != 0) {
            throw new BusinessException("微信登录失败：" + wxSession.getErrmsg());
        }

        String openid = wxSession.getOpenid();
        String unionid = wxSession.getUnionid();

        if (openid == null || openid.trim().isEmpty()) {
            throw new BusinessException("微信登录失败：未获取到 openid");
        }

        // 2. 根据 openid 查找或创建用户
        UserProfile user = userService.findByWxOpenId(openid)
                .orElseGet(() -> createUserByWeixin(openid, unionid, dto));

        // 3. 可选：更新一下昵称和头像（用户修改微信头像后同步）
        boolean needUpdate = false;
        if (dto.getNickname() != null && !dto.getNickname().equals(user.getNickname())) {
            user.setNickname(dto.getNickname());
            needUpdate = true;
        }
        if (dto.getAvatar() != null && !dto.getAvatar().equals(user.getAvatarUrl())) {
            user.setAvatarUrl(dto.getAvatar());
            needUpdate = true;
        }
        if (needUpdate) {
            userService.saveOrUpdate(user);
        }

        // 不在这里调 StpUtil.login，交给 Controller 调
        return user;
    }

    /**
     * 调用微信 jscode2session 接口
     */
    private WxSessionResponse getSessionByCode(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + wxAppId
                + "&secret=" + wxAppSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        String body = restTemplate.getForObject(url, String.class);
        log.info("jscode2session 返回: " + body);

        // 用你项目里已有的 JSON 工具，下面只是一个示例（Jackson）
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, WxSessionResponse.class);
        } catch (Exception e) {
            throw new BusinessException("解析微信登录返回失败");
        }
    }

    /**
     * 首次微信登录时创建用户
     */
    private UserProfile createUserByWeixin(String openid, String unionid, WxLoginDTO dto) {
        UserProfile user = new UserProfile();
        // 你自己的 UserProfile 字段自己对一下
        user.setOpenid(openid);
        user.setUnionid(unionid);
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "微信用户");
        user.setAvatarUrl(dto.getAvatar());
        user.setUserName("wx_" + openid);   // 随便占一个唯一标识
        // 随机密码（只是为了兼容原有字段，微信登录不会用到）
        user.setPasswordHash(UUID.randomUUID().toString());

        userService.saveOrUpdate(user);
        return user;
    }


    public boolean login( String identifier, String password){
        UserProfile userProfile = checkPassword(identifier, password);

        // 登录成功
        StpUtil.login(userProfile.getId());

        return true;
    }




    public UserProfile checkPassword(String identifier,String password) {
        Optional<UserProfile> userOptional = userService.findByIdentifier(identifier);
        if (!userOptional.isPresent()) {
            throw new BusinessException("用户名或密码错误");
        }

        UserProfile user = userOptional.get();

        if (!PasswordService.checkPassword(password, user.getPasswordHash())) {
           throw new BusinessException("用户名或密码错误");
        }
        return user;
    }

    public UserProfile checkCaptcha(String identifier,String captcha) {
        Optional<UserProfile> userOptional = userService.findByIdentifier(identifier);
        if (!userOptional.isPresent()) {
            throw new BusinessException("用户不存在");
        }

        UserProfile user = userOptional.get();

        if (!Objects.equals("todo", captcha)){
            throw new BusinessException("验证码错误");
        }
        return user;
    }

    public void updatePassword(UserProfile userProfile,String newPassword){
        String newHashedPassword = PasswordService.hashPassword(newPassword);
        userProfile.setPasswordHash(newHashedPassword);
        userService.updateById(userProfile);
    }

}
