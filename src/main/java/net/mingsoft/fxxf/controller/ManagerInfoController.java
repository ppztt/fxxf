package net.mingsoft.fxxf.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.service.ManagerInfoService;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author luwb
 * @date 2023-04-17
 */
@RestController
@Slf4j
@Api(tags = "用户信息管理")
@RequestMapping("/user")
public class ManagerInfoController {

    @Resource
    private ManagerInfoService managerInfoService;

    @ApiOperation(value = "企业用户列表", notes = "用户管理/企业用户管理列表")
    @GetMapping(value = "/enterpriseList", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public BaseResult<Page<ManagerInfoVo>> enterpriseList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                          @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<ManagerInfoVo> page = new Page<>(current, size);
        List<ManagerInfoVo> users = managerInfoService.enterpriseList(keyword.trim(), page);
        page.setRecords(users);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "用户列表", notes = "用户管理/用户列表")
    @GetMapping(value = "/userList", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public BaseResult<Page<ManagerInfoVo>> userList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                    @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                    @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<ManagerInfoVo> page = new Page<>(current, size);
        List<ManagerInfoVo> users = managerInfoService.userList(keyword.trim(), page);
        page.setRecords(users);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "行业协会用户列表", notes = "用户管理/行业协会用户管理列表")
    @GetMapping(value = "/industryAssociationList", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "string", example = "张三", defaultValue = "张三")
            }
    )
    public BaseResult<Page<ManagerInfoVo>> industryAssociationList(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                                   @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                   @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<ManagerInfoVo> page = new Page<>(current, size);
        List<ManagerInfoVo> users = managerInfoService.industryAssociationList(keyword.trim(), page);
        page.setRecords(users);
        return BaseResult.success(page);
    }

    @ApiOperation(value = "新增用户", notes = "用户管理/新增后台用户")
    @PostMapping(value = "/addUser", produces = "application/json;charset=UTF-8")
    @DynamicParameters(
            name = "managerInfo",
            properties = {
                    @DynamicParameter(name = "user", value = "user", dataTypeClass = ManagerInfoVo.class, required = true)
            }
    )
    public BaseResult<String> addUser(@Valid @RequestBody ManagerInfoVo managerInfo, BindingResult msg) {
        if (msg.hasErrors()) {
            return BaseResult.fail(Optional.ofNullable(msg.getFieldError()).map(FieldError::getDefaultMessage).orElse("参数有误"));
        }
        managerInfo.setUsertype(1);
        return BaseResult.success(managerInfoService.addUser(managerInfo));
    }

    @ApiOperation(value = "新增行业协会用户", notes = "用户管理/新增行业协会用户")
    @PostMapping(value = "/addIndustryAssociation", produces = "application/json;charset=UTF-8")
    @DynamicParameters(
            name = "managerInfo",
            properties = {
                    @DynamicParameter(name = "user", value = "user", dataTypeClass = ManagerInfoVo.class, required = true)
            }
    )
    public BaseResult<String> addIndustryAssociation(@Valid @RequestBody ManagerInfoVo managerInfo, BindingResult msg) {
        if (msg.hasErrors()) {
            return BaseResult.fail(Optional.ofNullable(msg.getFieldError()).map(FieldError::getDefaultMessage).orElse("参数有误"));
        }
        managerInfo.setUsertype(3);
        return BaseResult.success(managerInfoService.addUser(managerInfo));
    }

    @ApiOperation(value = "修改个人资料", notes = "用户管理/修改后台用户个人资料")
    @PostMapping(value = "/updateById")
    @DynamicParameters(
            name = "managerInfo",
            properties = {
                    @DynamicParameter(name = "user", value = "user", dataTypeClass = ManagerInfoVo.class, required = true)
            }
    )
    public BaseResult<String> updateById(@RequestBody ManagerInfoVo managerInfo) {
        managerInfoService.updateUserInfo(managerInfo);
        return BaseResult.success();
    }

    @ApiOperation(value = "删除用户", notes = "用户管理/删除用户")
    @PostMapping(value = "/del/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true)
    })
    public BaseResult<String> del(@PathVariable(value = "id") String id) {
        managerInfoService.deleteUser(id);
        return BaseResult.success();
    }

    @ApiOperation(value = "修改个人密码", notes = "用户管理/修改个人密码")
    @PostMapping("/updatePwd")
    @DynamicParameters(
            name = "managerInfo",
            properties = {
                    @DynamicParameter(name = "managerInfo", value = "managerInfo", dataTypeClass = ManagerInfoVo.class, required = true)
            }
    )
    public BaseResult<String> updatePwd(@RequestBody ManagerInfoVo managerInfo) {
        if (CharSequenceUtil.hasBlank(managerInfo.getManagerPassword(), managerInfo.getNewManagerPassword())) {
            return BaseResult.fail("密码不能为空");
        }
        managerInfoService.updatePwd(managerInfo);
        return BaseResult.success();
    }

    @ApiOperation(value = "查询个人资料|企业用户|行业协会查看", notes = "用户管理/查询个人资料")
    @GetMapping("/userInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id")
    })
    public BaseResult<ManagerInfoVo> userInfo(@RequestParam(required = false) String id) {
        ManagerInfoVo managerInfo = new ManagerInfoVo();
        if (CharSequenceUtil.isNotBlank(id)) {
            managerInfo = managerInfoService.getManagerInfoById(id);
        } else {
            ManagerEntity managerEntity = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
            BeanUtil.copyProperties(managerEntity, managerInfo);
        }
        managerInfo.setManagerPassword("");
        return BaseResult.success(managerInfo);
    }

}
