package com.shemuel.timeline.service;

import com.shemuel.timeline.dto.UserRegisterDTO;
import com.shemuel.timeline.entity.UserProfile;
import com.shemuel.timeline.exception.BusinessException;
import com.shemuel.timeline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

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


}
