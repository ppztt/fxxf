package net.mingsoft.config;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.mingsoft.fxxf.bean.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: yrg
 * @Description: 缓存工厂配置类，通过配置自定义方式灵活获取
 **/
@Configuration
public class CacheConfig {

    /**
     * 验证码失效时间
     */
    @Value("${pub.vcode.expire}")
    private Long vcodeExpire;

    /**
     * 邮箱验证码失效时间
     */
    @Value("${pub.ecode.expire}")
    private Long ecodeExpire;

    /**
     * token失效时间
     */
    @Value("${jwt.token.expiration}")
    private Long expiration;

    /**
     * 只用于缓存验证码
     *
     * @return Cache<String, String>
     */
    @Bean("vcodeLocalCache")
    public Cache<String, String> vcodeLocalCache() {
        return Caffeine.newBuilder().expireAfterWrite(vcodeExpire, TimeUnit.MINUTES).build();
    }

    /**
     * 只用于缓存邮箱验证码
     *
     * @return Cache<String, String>
     */
    @Bean("ecodeLocalCache")
    public Cache<String, String> ecodeLocalCache() {
        return Caffeine.newBuilder().expireAfterWrite(ecodeExpire, TimeUnit.MINUTES).build();
    }

    /**
     * 只用于缓存token作验证
     *
     * @return Cache<String, String>
     */
    @Bean("tokenCache")
    public Cache<String, User> tokenCache() {
        return Caffeine.newBuilder().expireAfterWrite(expiration, TimeUnit.SECONDS).build();
    }

    /**
     * 只用于记录登录失败次数
     *
     * @return
     */
    @Bean("loginFailedCache")
    public Cache<String, Integer> loginFailedCache() {
        return Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();
    }

}
