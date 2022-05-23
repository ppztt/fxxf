package net.mingsoft.basic.filter;

import org.springframework.util.AntPathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SelfXSSEscapeFilter extends XSSEscapeFilter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (handleExcludeURL(req,resp)){//如果排除路径匹配
            filterChain.doFilter(request, response);
            return;
        }else if (!handleIncludeURL(req, resp)) {
            filterChain.doFilter(request, response);
            return;
        }
        SelfXssHttpServletRequestWrapper xssRequest = new SelfXssHttpServletRequestWrapper((HttpServletRequest) request);
        filterChain.doFilter(xssRequest, response);
    }

    /**
     * 处理拦截路径
     * @param request
     * @param response
     * @return
     */
    private boolean handleIncludeURL(HttpServletRequest request, HttpServletResponse response) {
        if (includes == null || includes.isEmpty()) {
            return false;
        }
        String url = request.getServletPath();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String pattern : includes) {
            if(antPathMatcher.match(pattern,url)){
                return true;
            }
        }
        return false;
    }

    /**
     * 处理排除路径
     * @param request
     * @param response
     * @return 是否匹配成功
     */
    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        String url = request.getServletPath();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String pattern : excludes) {
            if(antPathMatcher.match(pattern,url)){
                return true;
            }
        }
        return false;
    }
}
