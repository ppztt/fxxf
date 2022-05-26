/**
 * The MIT License (MIT)
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */




package net.mingsoft.cms.action.web;

import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.entity.CategoryEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * 解析旧路径跳转新路径
 *
 * @author 1y
 * @date 2022.05.26
 */
@ApiIgnore
@Controller("ConvertOldPathAction")
@RequestMapping("/")
public class ConvertOldPathAction extends net.mingsoft.cms.action.BaseAction {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${ms.html-dir:html}")
    private String htmlDir;


    /**
     * 注入分类业务层
     */
    @Autowired
    private ICategoryBiz categoryBiz;


    /**
     * 解析旧路径跳转新路径
     */
    @GetMapping("/show**")
    public String index(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        CategoryEntity category = new CategoryEntity();
        String newPath = "/" + htmlDir + "/web";
        if (StringUtils.isNotBlank(requestURI)) {
            String[] split = requestURI.split("-");
            category.setId(split[1]);
            CategoryEntity cate = categoryBiz.getById(category);
            if (cate != null) {
                newPath += cate.getCategoryPath();
                newPath += "/";
                newPath += split[2];
                newPath += ".html";
            } else {
                newPath = "404";
            }
        } else {
            newPath = "404";
        }
        LOG.warn("路径跳转：" + requestURI + " >>> " + newPath);
        return newPath;
    }

}
