package com.shemuel.timeline.service;

import cn.dev33.satoken.stp.StpUtil;
import com.shemuel.timeline.entity.UserProfile;
import com.shemuel.timeline.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * @Author: 公众号: 加瓦点灯
 * @Date: 2025-03-13-14:53
 * @Description:
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

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
