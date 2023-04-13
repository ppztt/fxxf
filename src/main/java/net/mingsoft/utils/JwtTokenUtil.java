package net.mingsoft.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yrg
 * @Date: 2020-01-12 16:38
 * @Description: TODO
 **/
public class JwtTokenUtil {

    /**
     * 从请求头或者请求参数中
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }
        // 先从Header里面获取
        String token = request.getHeader(CommonConstant.JWT_DEFAULT_TOKEN_NAME);
        if(StringUtils.isEmpty(token)){
            // 获取不到再从Parameter中拿
            token = request.getParameter(CommonConstant.JWT_DEFAULT_TOKEN_NAME);
            // 还是获取不到再从Cookie中拿
            if(StringUtils.isEmpty(token)){
                Cookie[] cookies = request.getCookies();
                if(cookies != null){
                    for (Cookie cookie : cookies) {
                        if(CommonConstant.JWT_DEFAULT_TOKEN_NAME.equals(cookie.getName())){
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }
        return token;
    }

}
