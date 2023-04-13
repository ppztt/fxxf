package net.mingsoft.utils;

/**
 * @Author: yrg
 * @Description: 常量定义类
 **/
public interface CommonConstant {

    /**
     * 登陆token
     */
    String JWT_DEFAULT_TOKEN_NAME = "token";

    /**
     * base64前缀
     */
    String BASE64_PREFIX = "data:image/jpeg;base64,";

    /**
     * JWT用户名
     */
    String JWT_USERNAME = "username";

    /**
     * JWT刷新新token响应状态码
     */
    int JWT_REFRESH_TOKEN_CODE = 460;

    /**
     * JWT刷新新token响应状态码，
     * 缓存中不存在，但jwt未过期，不生成新的token，返回461状态码
     */
    int JWT_INVALID_TOKEN_CODE = 461;

}
