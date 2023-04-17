package net.mingsoft.shiro.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.utils.JwtTokenUtil;
import net.mingsoft.utils.ResponseBean;
import net.mingsoft.utils.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: yrg
 * @Description: 认证过滤器
 **/
@Slf4j
public class JwtAuthenticationFilter extends AuthenticatingFilter {

    private TokenUtil tokenUtil;
    private Cache<String, User> tokenCache;

    public JwtAuthenticationFilter(TokenUtil tokenUtil, Cache<String, User> tokenCache) {
        this.tokenUtil = tokenUtil;
        this.tokenCache = tokenCache;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = JwtTokenUtil.getToken(httpRequest);
        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("token不能为空");
        }
        if (tokenUtil.isExpired(token)) {
            throw new AuthenticationException("token已过期,token:" + token);
        }
        User user = tokenCache.getIfPresent(token);
        if (user == null) {
            throw new AuthenticationException("token不存在,token:" + token);
        }
        String username = tokenUtil.getUsernameFromToken(token);
        return JwtToken.builder()
                .token(token)
                .principal(username)
                .build();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("鉴权成功,token:{},url:{}", token, url);
        // 刷新token
        JwtToken jwtToken = (JwtToken) token;
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        tokenUtil.refreshToken(jwtToken, httpServletResponse);
        return true;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        try {
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json;charset=UTF-8");
            servletResponse.setHeader("Access-Control-Allow-Origin", "*");
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(new ResponseBean(500, "登录失败，无权访问")));
        } catch (IOException e) {
        }
        return false;
    }

}
