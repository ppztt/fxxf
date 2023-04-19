package net.mingsoft.fxxf.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import net.mingsoft.basic.dao.IManagerDao;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.fxxf.bean.vo.ApiResult;
import net.mingsoft.fxxf.mapper.UserMapper;
import net.mingsoft.fxxf.service.UserService;
import net.mingsoft.utils.CheckPassword;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: UserController
 * @Description 用户管理控制器
 * @Author Ligy
 * @Date 2020/1/12 21:00
 **/
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    UserService service;

    @Resource
    UserMapper userMapper;

    @Resource
    IManagerDao managerDao;

    // @RequiresPermissions("manage:user")
    @ApiOperation(value = "用户列表", notes = "用户管理/用户列表")
    @GetMapping(value = "/userList", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public ApiResult<Page<User>> userList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                          @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<User> page = new Page<>(current, size);
        List<User> users = userMapper.userList(keyword, page);
        page.setRecords(users);
        return ApiResult.success(page);
    }

    // @RequiresPermissions("manage:user")
    @ApiOperation(value = "新增用户", notes = "用户管理/新增用户")
    @PostMapping(value = "/addUser", produces = "application/json;charset=UTF-8")
    @DynamicParameters(
            name = "user",
            properties = {
                    @DynamicParameter(name = "user", value = "user", dataTypeClass = User.class, required = true)
            }
    )
    public ApiResult addUser(@Valid @RequestBody User user, BindingResult msg) {
        if (msg.hasErrors()) {
            log.info(msg.getFieldError().getDefaultMessage());
            return new ApiResult("500", msg.getFieldError().getDefaultMessage());
        } else {
            //account唯一性校验
            String account = user.getAccount();
            List<User> uList = service.list(new QueryWrapper<User>().eq("account", account));
            if (uList.size() > 0) {
                return new ApiResult("500", "帐号已存在");
            } else {
                try {
                    String pwd = user.getPassword();
                    // 密码强度校验
                    boolean isPass = CheckPassword.checkPasswordRule(pwd);
                    if(isPass){
                        user.setPassword(DigestUtils.sha256Hex(pwd));

                        LocalDateTime now = LocalDateTime.now();
                        user.setCreateTime(now);
                        user.setUpdateTime(now);
                        user.setUsertype(1);
                        service.save(user);

                        // 新增manager表记录
                        ManagerEntity manager = new ManagerEntity();
                        manager.setManagerName(user.getAccount());
                        manager.setManagerPassword(SecureUtil.md5(pwd));
                        manager.setManagerNickName(user.getRealname());
                        manager.setRoleId(user.getRoleId());
                        managerDao.insert(manager);
                        return ApiResult.success();
                    }else{
                        return ApiResult.fail("密码最小长度为8位，至少包含数字、大写字母、小写字母和特殊字符中的三种");
                    }
                } catch (Exception e) {
                    log.error("保存新增用户发生异常：{}", e);
                    return ApiResult.fail();
                }
            }
        }
    }

    // @RequiresPermissions("manage:user")
    @ApiOperation(value = "删除用户", notes = "用户管理/删除用户")
    @PostMapping(value = "/del/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true)
    })
    public ApiResult del(@PathVariable(value = "id") int id) {
        try {
            ManagerEntity managerEntity = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
            Integer uId = managerEntity.getIntegerId();
            if (uId == id) {
                return new ApiResult("500", "不允许删除当前登录用户");
            }
            service.removeById(id);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("删除用户发生异常:{}", e);
            return ApiResult.fail();
        }
    }

    // @RequiresPermissions("manage:userinfo")
    @ApiOperation(value = "修改个人资料", notes = "用户管理/修改个人资料")
    @PostMapping(value = "/updateById")
    @DynamicParameters(
            name = "user",
            properties = {
                    @DynamicParameter(name = "user", value = "user", dataTypeClass = User.class, required = true)
            }
    )
    public ApiResult updateById(@RequestBody User user) {
        try {
            //密码
            String password = user.getPassword();
            if (password != null) {
                // 密码强度校验
                boolean isPass = CheckPassword.checkPasswordRule(password);
                if(isPass){
                    password = DigestUtils.sha256Hex(password);
                    user.setPassword(password);
                }else{
                    return ApiResult.fail("密码最小长度为8位，至少包含数字、大写字母、小写字母和特殊字符中的三种");
                }
            }
            user.setUsertype(1);
            //User实体类不为NULL的属性都将被更新
            user.update(
                    new UpdateWrapper<User>().eq("id", user.getId()).set("update_time", LocalDateTime.now())
            );
            return ApiResult.success();
        } catch (Exception e) {
            log.error("更新用户信息发生异常：{}", e);
            return ApiResult.fail();
        }
    }

//    @ApiOperation(value = "修改个人密码", notes = "用户管理/修改个人密码")
//    @PostMapping("/updatePwd")
//    @DynamicParameters(
//            name = "user",
//            properties = {
//                    @DynamicParameter(name = "user", value = "user", dataTypeClass = User.class, required = true)
//            }
//    )
//    public ApiResult updatePwd(@RequestBody User user) {
//        try {
//            //密码校验
//            User u = service.getById(user.getId());
//            String dbPwd = u.getPassword();
//
//            String fontPwd = user.getPassword();
//
//            // 密码强度校验
//            boolean isPass = CheckPassword.checkPasswordRule(fontPwd);
//            if(isPass){
//                fontPwd = DigestUtils.sha256Hex(fontPwd);
//                if (dbPwd.equals(fontPwd)) {
//                    String newPwd = DigestUtils.sha256Hex(user.getNewPassword());
//                    if (dbPwd.equals(newPwd)) {
//                        return new ApiResult("500", "旧密码与新密码相同");
//                    }
//                    service.update(new UpdateWrapper<User>()
//                            .set("password", newPwd)
//                            .eq("id", user.getId())
//                    );
//                    return ApiResult.success();
//                } else {
//                    return new ApiResult("500", "旧密码不正确");
//                }
//            }else{
//                return ApiResult.fail("密码最小长度为8位，至少包含数字、大写字母、小写字母和特殊字符中的三种");
//            }
//        } catch (Exception e) {
//            log.error("更新用户密码发生异常：{}", e);
//            return ApiResult.fail();
//        }
//    }

    // @RequiresPermissions({"manage:userinfo", "manage:enterprise","manage:industryAssociation"})
    @ApiOperation(value = "查询个人资料|企业用户|行业协会查看", notes = "用户管理/查询个人资料")
    @GetMapping("/userInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = false)
    })
    public ApiResult<User> userInfo(@RequestParam(required = false) String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                User user = service.getById(id);
                return ApiResult.success(user);
            } else {
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                return ApiResult.success(user);
            }
        } catch (Exception e) {
            log.error("查询个人资料发生异常：{}", e);
            return ApiResult.fail();
        }
    }

    /*************************企业用户***************************/
    // @RequiresPermissions("manage:enterprise")
    @ApiOperation(value = "企业用户列表", notes = "用户管理/企业用户管理列表")
    @GetMapping(value = "/enterpriseList", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public ApiResult<Page<User>> enterpriseList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                          @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<User> page = new Page<>(current, size);
        List<User> users = userMapper.enterpriseList(keyword, page);
        page.setRecords(users);
        return ApiResult.success(page);
    }


    /*************************行业协会用户***************************/
    // @RequiresPermissions("manage:industryAssociation")
    @ApiOperation(value = "行业协会用户列表", notes = "用户管理/行业协会用户管理列表")
    @GetMapping(value = "/industryAssociationList", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public ApiResult<Page<User>> industryAssociationList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                          @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<User> page = new Page<>(current, size);
        List<User> users = userMapper.industryAssociationList(keyword, page);
        page.setRecords(users);
        return ApiResult.success(page);
    }


    // @RequiresPermissions("manage:industryAssociation")
    @ApiOperation(value = "新增行业协会用户", notes = "用户管理/新增行业协会用户")
    @PostMapping(value = "/addIndustryAssociation", produces = "application/json;charset=UTF-8")
    @DynamicParameters(
            name = "user",
            properties = {
                    @DynamicParameter(name = "user", value = "user", dataTypeClass = User.class, required = true)
            }
    )
    public ApiResult addIndustryAssociation(@Valid @RequestBody User user, BindingResult msg) {
        if (msg.hasErrors()) {
            log.info(msg.getFieldError().getDefaultMessage());
            return new ApiResult("500", msg.getFieldError().getDefaultMessage());
        } else {
            //account唯一性校验
            String account = user.getAccount();
            List<User> uList = service.list(new QueryWrapper<User>().eq("account", account));
            if (uList.size() > 0) {
                return new ApiResult("500", "帐号已存在");
            } else {
                String pwd = user.getPassword();

                try {
                    // 密码强度校验
                    boolean isPass = CheckPassword.checkPasswordRule(pwd);
                    if(isPass){
                        user.setPassword(DigestUtils.sha256Hex(pwd));

                        LocalDateTime now = LocalDateTime.now();
                        user.setCreateTime(now);
                        user.setUpdateTime(now);
                        user.setUsertype(3);
                        user.setRoleId(4);
                        service.save(user);
                        return ApiResult.success();
                    }else{
                        return ApiResult.fail("密码最小长度为8位，至少包含数字、大写字母、小写字母和特殊字符中的三种");
                    }
                } catch (Exception e) {
                    log.error("保存新增行业协会用户发生异常：{}", e);
                    return ApiResult.fail();
                }
            }
        }
    }

}
