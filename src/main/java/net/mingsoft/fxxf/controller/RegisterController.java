package net.mingsoft.fxxf.controller;

import com.github.benmanes.caffeine.cache.Cache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.fxxf.bean.vo.ApiResult;
import net.mingsoft.fxxf.bean.vo.RegisterUserVo;
import net.mingsoft.fxxf.service.MailService;
import net.mingsoft.fxxf.service.UserService;
import net.mingsoft.utils.BeanUtil;
import net.mingsoft.utils.CheckPassword;
import net.mingsoft.utils.VerifyCodeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: yrg
 * @Date: 2020-09-20 10:11
 * @Description: TODO
 **/
@Slf4j
@Api(tags = "注册")
@RestController
@RequestMapping("/register")
@Validated
public class RegisterController {

    @Autowired
    private MailService mailService;

    @Autowired
    @Qualifier("ecodeLocalCache")
    private Cache<String, String> ecodeLocalCache;

    @Resource
    private UserService service;

    @GetMapping("/sendmail")
    @ApiOperation(value = "发送邮箱验证码", notes = "发送邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱账号", required = true),
    })
    public ApiResult sendmail(@Email(message = "电子邮件格式不正确") String email) {
        String code = VerifyCodeUtil.generateVerifyCode(6);
        try {
             mailService.sendHtmlMail(email, "注册验证码", "欢迎注册，您的验证码为：<b>" + code +"</b>");
            log.info("企业端注册邮箱验证码发送成功：code=" + code);
            ecodeLocalCache.put(email, code);
            return ApiResult.success(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * 注册
     * @return
     */
    @PostMapping("/save")
    @ApiOperation(value = "提交注册", notes = "提交注册")
    public ApiResult save(@Valid @RequestBody RegisterUserVo userVo) {
        log.info(userVo.toString());
        String ecode = ecodeLocalCache.getIfPresent(userVo.getEmail());
        if (StringUtils.isBlank(ecode)) {
            return ApiResult.fail("验证码已过期");
        } else if (ecode.equals(userVo.getEcode())) {
            // 检查用户名是否存在
            List<String> accounts = service.existsAccount(userVo.getAccount());
            if (BeanUtil.isBlank(accounts)) {
                // 密码强度校验
                boolean isPass = CheckPassword.checkPasswordRule(userVo.getPassword());
                if(isPass){
                    User user = new User();
                    user.setPassword(DigestUtils.sha256Hex(userVo.getPassword()));
                    user.setUsertype(2);
                    user.setAccount(userVo.getAccount());
                    user.setEmail(userVo.getEmail());
                    LocalDateTime now = LocalDateTime.now();
                    user.setCreateTime(now);
                    user.setUpdateTime(now);
                    service.save(user);
                    return ApiResult.success();
                }else{
                    return ApiResult.fail("密码最小长度为8位，至少包含数字、大写字母、小写字母和特殊字符中的三种");
                }
            } else {
                return ApiResult.fail("账号已存在!");
            }
        } else {
            return ApiResult.fail("验证码错误");
        }
    }

}
