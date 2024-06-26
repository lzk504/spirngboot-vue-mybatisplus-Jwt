package com.example.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis工具
 */
@Slf4j
@Component
public class RedisUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取redis中存储的邮箱的验证码
     * @param email 邮箱
     * @return 验证码
     */
    public String getEmailVerityCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 向redis中存储验证码
     * @param email 邮箱
     * @param code 验证码
     * @param timeout 过期时间
     */
    public void storeCodeByEmail(String email, String code,int timeout) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.opsForValue()
                .set(key, String.valueOf(code), timeout, TimeUnit.MINUTES);
    }

    /**
     * 移除Redis中存储的邮件验证码
     * @param email 电邮
     */
    public void deleteEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }


}
