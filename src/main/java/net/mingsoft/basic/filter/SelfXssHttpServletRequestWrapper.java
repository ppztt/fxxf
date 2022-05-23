package net.mingsoft.basic.filter;

import net.mingsoft.basic.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;

public class SelfXssHttpServletRequestWrapper extends XssHttpServletRequestWrapper{

    public SelfXssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String clean(String name, String content) {
        try {
            super.clean(name, content);
        } catch (BusinessException e) {
            throw new BusinessException("参数异常!");
        }
        return content;
    }
}
