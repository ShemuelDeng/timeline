package com.shemuel.timeline.service;

import com.shemuel.timeline.dto.UserRegisterDTO;
import com.shemuel.timeline.dto.UtoolsBaseInfoResponse;
import com.shemuel.timeline.entity.UserProfile;
import com.shemuel.timeline.exception.BusinessException;
import com.shemuel.timeline.exception.ServiceException;
import com.shemuel.timeline.repository.UserRepository;
import com.shemuel.timeline.utils.DateUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserProfile register(UserRegisterDTO userRegisterDTOu) {
        UserProfile users = new UserProfile();
        users.setUserName(userRegisterDTOu.getUsername());
        users.setEmail(userRegisterDTOu.getEmail());
        users.setEmail(userRegisterDTOu.getPhone());
        // 密码加密
        users.setPasswordHash(PasswordService.hashPassword(userRegisterDTOu.getPassword()));
        users.setCreatedAt(LocalDateTime.now());
        users.setLastLogin(null);
        try {
            return userRepository.save(users);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("idx_email")){
                throw  new BusinessException(500, "邮箱已被注册");
            } else if (e.getMessage().contains("idx_phone")) {
                throw  new BusinessException(500, "手机号已被注册");
            }else {
                throw  new BusinessException(500, "用户名已被注册");
            }
        }catch (Exception e){
            log.error("用户注册失败", e);
            throw new BusinessException("服务异常");
        }
    }

    public Optional<UserProfile> findByIdentifier(String identifier) {
        return userRepository.findByIdentifier(identifier);
    }
    public Optional<UserProfile> findByWxOpenId(String openId) {
        return userRepository.findByWxOpenId(openId);
    }
    public Optional<UserProfile> findUserByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    public int updateById (UserProfile userProfile) {
        return userRepository.update(userProfile);
    }

    public UserProfile saveOrUpdate (UserProfile userProfile) {
        return userRepository.saveOrUpdate(userProfile);
    }



    @Resource
    private UtoolsOpenApiClient utoolsOpenApiClient;

    public UserProfile loginOrRegisterByUtoolsToken(String token) {
        UtoolsBaseInfoResponse resp = utoolsOpenApiClient.getBaseInfo(token);
        UtoolsBaseInfoResponse.Resource r = resp.getResource();
        if (r == null || r.getOpen_id() == null) {
            throw new ServiceException("从 uTools 获取用户信息失败");
        }

        String openId = r.getOpen_id();
        String nickname = "utoos-" +  r.getNickname();
        String avatar = r.getAvatar();

        UserProfile user = null;
        // 1. 查是否已经绑定
        Optional<UserProfile> userProfileOptional = userRepository.findByUtoolsId(openId);
        if (userProfileOptional.isPresent()) {
            user= userProfileOptional.get();
            // 更新下最后登录时间等
            user.setLastLogin(DateUtil.toLocalDateTime(new Date()));
            userRepository.saveOrUpdate(user);
            return user;
        }

        // 2. 首次进来，自动注册一个账号
        user = new UserProfile();
        user.setUserName(generateUserName(openId, nickname));
        user.setNickname(nickname);
        user.setAvatarUrl(avatar);
        user.setUtoolsId(openId);
        user.setSourceType("utools");
        user.setUserStatus(0);

        // 生成一个随机密码哈希（用户基本用不到这个密码）
        String randomPwd = UUID.randomUUID().toString().replace("-", "");
        String bcryptHash = passwordEncoder().encode(randomPwd);
        user.setPasswordHash(bcryptHash);

        userRepository.saveOrUpdate(user);
        return user;
    }

    private String generateUserName(String openId, String nickname) {
        if (nickname != null && !nickname.isEmpty()) {
            return nickname;
        }
        return "utools_" + openId.substring(0, 8);
    }
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
