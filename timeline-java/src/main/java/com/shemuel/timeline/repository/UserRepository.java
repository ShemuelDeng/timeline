package com.shemuel.timeline.repository;

import com.shemuel.timeline.entity.UserProfile;

import java.util.Optional;


public interface UserRepository {

    UserProfile save(UserProfile userProfile);
    UserProfile saveOrUpdate(UserProfile userProfile);
    Optional<UserProfile> findByIdentifier(String identifier);
    Optional<UserProfile> findByPhone(String phone);
    Optional<UserProfile> findByWxOpenId(String openid);
    Optional<UserProfile> findByUsername(String username);
     int update(UserProfile userProfile);
    void delete(String userId);

}
