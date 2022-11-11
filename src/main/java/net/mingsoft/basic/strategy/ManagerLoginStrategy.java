/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package net.mingsoft.basic.strategy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.constant.e.SessionConstEnum;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.RedisUtil;
import net.mingsoft.basic.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

/**
 * 管理员登录列表
 *
 * @author Administrator
 * @version 创建日期：2020/11/18 18:12<br/>
 * 历史修订：<br/>
 */
public class ManagerLoginStrategy implements ILoginStrategy{


    @Autowired
    private IManagerBiz managerBiz;

    @Value("${ms.open-remember:true}")
    private boolean openRemember;

    @Value("${ms.pass-error-max-times:5}")
    private int passErrorMaxTimes;

    @Value("${ms.pass-error-lock-time:30}")
    private int passErrorLockTime;

    @Value("${ms.pass-error-keep-time:30}")
    private int passErrorKeepTime;

    @Value("${ms.session-time:30}")
    private int sessionTime;


    @Override
    public Boolean login(ManagerEntity manager) {
        managerBiz.updateCache();
        boolean rememberMe = BasicUtil.getBoolean("rememberMe");
        if(ObjectUtil.isNull(manager) || StringUtils.isEmpty(manager.getManagerName()) || StringUtils.isEmpty(manager.getManagerPassword())){
            return false;
        }
        // 根据账号获取当前管理员信息
        ManagerEntity _manager = managerBiz.getManagerByManagerName(manager.getManagerName());
        if (_manager == null ) {
            // 系统不存在此用户
            return false;
        } else {
            // 判断当前用户输入的密码是否正确
            if (SecureUtil.md5(manager.getManagerPassword()).equals(_manager.getManagerPassword())) {
//                 创建管理员session对象
                ManagerEntity managerSession = new ManagerEntity();
//                 压入管理员seesion
                BasicUtil.setSession(SessionConstEnum.MANAGER_SESSION, managerSession);
                SpringUtil.getRequest().getSession().setMaxInactiveInterval(sessionTime * 60);
                BeanUtils.copyProperties(_manager, managerSession);
                Subject subject = SecurityUtils.getSubject();
                UsernamePasswordToken upt = new UsernamePasswordToken(managerSession.getManagerName(), managerSession.getManagerPassword());
                if (openRemember) {
                    upt.setRememberMe(rememberMe);
                } else {
                    upt.setRememberMe(openRemember);
                }
                subject.login(upt);
                RedisUtil.addValue("PassErrTimes:" + manager.getManagerName(), String.valueOf(0), 1, TimeUnit.MINUTES);
                return true;
            } else {
                // 密码错误
                int times = 1;
                String passErrTimes = (String) RedisUtil.getValue("PassErrTimes:" + manager.getManagerName());
                if (StringUtils.isNotBlank(passErrTimes)) {
                    try {
                        times = Integer.parseInt(passErrTimes);
                        times++;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (times >= passErrorMaxTimes) {
                    if (!checkManagerPassErrLock(manager)) {
                        RedisUtil.addValue("PassErrLock:" + manager.getManagerName(), "Locked", passErrorLockTime, TimeUnit.MINUTES);
                        times = 0;
                    }
                }
                RedisUtil.addValue("PassErrTimes:" + manager.getManagerName(), String.valueOf(times), passErrorKeepTime, TimeUnit.MINUTES);
                return false;
            }
        }
    }

    public boolean checkManagerPassErrLock(ManagerEntity manager) {
        String checkValue = (String) RedisUtil.getValue("PassErrLock:" + manager.getManagerName());
        return StringUtils.isNotBlank(checkValue);
    }
}
