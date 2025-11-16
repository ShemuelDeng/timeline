package com.shemuel.timeline.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.PhoneUtil;
import com.shemuel.timeline.annotation.AccessLimit;
import com.shemuel.timeline.common.RestResult;
import com.shemuel.timeline.dto.UserLoginDTO;
import com.shemuel.timeline.dto.UserPasswordResetDTO;
import com.shemuel.timeline.dto.UserRegisterDTO;
import com.shemuel.timeline.dto.WxLoginDTO;
import com.shemuel.timeline.entity.UserProfile;
import com.shemuel.timeline.exception.BusinessException;
import com.shemuel.timeline.service.AuthService;
import com.shemuel.timeline.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-11-20:35
 * @Description:
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    @AccessLimit(key = "#userLoginDTO.identifier", interval = 5)
    public RestResult<SaTokenInfo> login(@RequestBody @Validated UserLoginDTO userLoginDTO) {

        if (!authService.login(userLoginDTO.getIdentifier(), userLoginDTO.getPassword())){
            return RestResult.error("用户名或密码错误");
        }

        // 登录成功
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return RestResult.success(tokenInfo);
    }

    // AuthController.java
    @PostMapping("/loginByWeixin")
    public RestResult<SaTokenInfo> loginByWeixin(@RequestBody @Validated WxLoginDTO wxLoginDTO) {
        try {
            // 交给 service 处理业务，返回 UserProfile
            UserProfile user = authService.loginByWeixin(wxLoginDTO);

            // 用 Sa-Token 登录
            StpUtil.login(user.getId());

            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return RestResult.success(tokenInfo);
        } catch (BusinessException e) {
            return RestResult.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return RestResult.error("微信登录失败，请稍后重试");
        }
    }


    @PostMapping("/register")
    public SaResult register(@RequestBody UserRegisterDTO registerDTO) {

        if (StringUtils.isNotEmpty(registerDTO.getPhone()) && !PhoneUtil.isPhone(registerDTO.getPhone())){
            throw new BusinessException(500, "手机号格式错误");
        }

        if (StringUtils.isNotEmpty(registerDTO.getEmail()) && !registerDTO.getEmail().contains("@")){
            throw new BusinessException(500, "邮箱格式错误");
        }
        userService.register(registerDTO);
        // 这里需要添加数据库保存逻辑
        return SaResult.data("注册成功");
    }




    @PostMapping("/password/reset")
    public SaResult resetPassword(@RequestBody @Validated UserPasswordResetDTO userLoginDTO) {
        UserProfile userProfile = authService.checkCaptcha(userLoginDTO.getIdentifier(), userLoginDTO.getCaptcha());
        authService.updatePassword(userProfile, userLoginDTO.getNewPassword());
        return SaResult.data("ok");
    }
}
