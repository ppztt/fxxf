/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.action.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.action.BaseAction;
import net.mingsoft.basic.biz.IAppBiz;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.strategy.ILoginStrategy;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.RedisUtil;
import net.mingsoft.config.MSProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @ClassName: LoginAction
 * @Description:TODO(登录的基础应用层)
 * @author: 铭飞开发团队
 * @date: 2015年1月27日 下午3:21:47
 *
 * @Copyright: 2018 www.mingsoft.net Inc. All rights reserved.
 */
@Api(tags={"前端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}")
public class LoginAction extends BaseAction {

    /**
     * 站点业务层
     */
    @Autowired
    private IAppBiz appBiz;

    @Autowired
    private ILoginStrategy loginStrategy;

    @Value("${ms.pass-change-max-day:90}")
    private int passChangeMaxDay;

    /**
     * 加载管理员登录界面
     *
     * @param request
     *            请求对象
     * @return 管理员登录界面地址
     */
    @ApiOperation(value = "加载管理员登录界面")
    @SuppressWarnings("resource")
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        String managerPath = MSProperties.manager.path;
        Subject currentSubject = SecurityUtils.getSubject();
        ManagerEntity user = (ManagerEntity) currentSubject.getPrincipal();
        if (user != null) {
            return "redirect:" + managerPath + "/index.do";
        }
        request.setAttribute("app", BasicUtil.getApp());
        return "/login";
    }

    @ApiOperation(value = "超时修改密码界面")
    @SuppressWarnings("resource")
    @GetMapping("/changepass")
    public String changepass(HttpServletRequest request) {
        request.setAttribute("app", BasicUtil.getApp());
        Subject currentSubject = SecurityUtils.getSubject();
        currentSubject.logout();
        return "/changepass";
    }

    /**
     * 验证登录
     *
     * @param manager
     *            管理员实体
     * @param request
     *            请求
     * @param response
     *            响应
     */
    @ApiOperation(value = "验证登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "managerName", value = "帐号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "managerPassword", value = "密码", required = true, paramType = "query"),
    })
    @PostMapping("/login")
    @ResponseBody
    public ResultData login(@ModelAttribute @ApiIgnore ManagerEntity manager, HttpServletRequest request, HttpServletResponse response) {
        LOG.debug("basic checkLogin");

        //验证码
        if (!(checkRandCode())) {
            return ResultData.build().error(getResString("err.error", this.getResString("rand.code")));
        }
        BasicUtil.removeSession(SessionConstEnum.CODE_SESSION);
        if (checkManagerPassErrLock(manager)) {
            // 密码错误超时锁定中
            return ResultData.build().error("密码错误超时锁定中（分钟）：" + RedisUtil.ttl("PassErrLock:" + manager.getManagerName()) / 60);
        }
        if (loginStrategy.login(manager)) {
            if (!checkManagerPassChangeTime(manager)) {
                String managerPath = MSProperties.manager.path;
                return ResultData.build().success(managerPath + "/changepass.do", passChangeMaxDay);
            }
            return ResultData.build().success();
        } else {
            return ResultData.build().error(getResString("err.error", this.getResString("manager.name.or.password")));
        }

    }

    public boolean checkManagerPassErrLock(ManagerEntity manager) {
        String checkValue = (String) RedisUtil.getValue("PassErrLock:" + manager.getManagerName());
        return StringUtils.isNotBlank(checkValue);
    }

    public boolean checkManagerPassChangeTime(ManagerEntity manager) {
        String checkValue = (String) RedisUtil.getValue("PassChangeMaxDay:" + manager.getManagerName());
        return StringUtils.isNotBlank(checkValue);
    }
}
