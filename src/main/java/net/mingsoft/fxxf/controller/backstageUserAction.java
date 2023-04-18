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




package net.mingsoft.fxxf.controller;

import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.cms.action.BaseAction;
import net.mingsoft.cms.biz.ICategoryBiz;
import net.mingsoft.cms.biz.IContentBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: GeneraterAction
 */
@ApiIgnore
@Controller("cmsBackstageUser")
@RequestMapping("/${ms.manager.path}/xwh/backstageUser")
@Scope("request")
public class backstageUserAction extends BaseAction {

    /*
     * log4j日志记录
     */
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * 文章管理业务层
     */
    @Autowired
    private IContentBiz contentBiz;

    /**
     * 栏目管理业务层
     */
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 模块管理业务层
     */
    @Autowired
    private IModelBiz modelBiz;

    @Value("${ms.manager.path}")
    private String managerPath;

    @Value("${ms.html-dir:html}")
    private String htmlDir;

    /**
     * /**
     * 更新主页
     *
     * @return
     */
    @GetMapping("/index")
    public String index() {
        return "/fxxf/backstageUser/index.do";
    }
}
