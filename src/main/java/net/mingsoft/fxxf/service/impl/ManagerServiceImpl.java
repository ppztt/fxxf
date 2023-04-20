package net.mingsoft.fxxf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.fxxf.bean.entity.ManagerInfo;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import net.mingsoft.fxxf.mapper.ManagerMapper;
import net.mingsoft.fxxf.service.ManagerInfoService;
import net.mingsoft.fxxf.service.ManagerService;
import net.mingsoft.utils.CheckPassword;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author luwb
 * @date 2023-04-20
 */
@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, ManagerEntity> implements ManagerService {

    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private ManagerInfoService managerInfoService;
    @Lazy
    @Autowired
    private ManagerService managerService;

    @Override
    public List<ManagerInfoVo> userList(Integer current, Integer size, String keyword) {
        Page<ManagerInfoVo> page = new Page<>(current, size);
        List<ManagerInfoVo> userList = managerMapper.userList(keyword, page);
        page.setRecords(userList);
        return userList;
    }

    @Override
    public List<String> existsAccount(String account) {
        return managerMapper.existsAccount(account);
    }

    @Override
    public List<ManagerInfoVo> enterpriseList(String keyword, Page<ManagerInfoVo> page) {
        return managerMapper.enterpriseList(keyword, page);
    }

    @Override
    public List<ManagerInfoVo> userList(String keyword, Page<ManagerInfoVo> page) {
        return managerMapper.userList(keyword, page);
    }

    @Override
    public List<ManagerInfoVo> industryAssociationList(String keyword, Page<ManagerInfoVo> page) {
        List<ManagerInfoVo> managerInfoVos = managerMapper.industryAssociationList(keyword, page);
        managerInfoVos.forEach(item -> item.setManagerPassword(""));
        return managerInfoVos;
    }

    @Override
    public String addUser(ManagerInfoVo user) {
        String pwd = user.getManagerPassword();
        List<String> accounts = existsAccount(user.getManagerName());
        checkUserInfo(pwd, accounts);
        user.setManagerPassword(DigestUtils.sha256Hex(pwd));
        // 使用managerService调用，使事务生效
        managerService.insertManagerInfo(user);
        return user.getId();
    }

    @Override
    public void updateUserInfo(ManagerInfoVo user) {
        ManagerInfoVo dbUser = checkManagerInfoId(user.getId());
        String password = user.getManagerPassword();
        if (CharSequenceUtil.isNotBlank(password)) {
            checkUserInfo(password, null);
        }
        user.setUsertype(dbUser.getUsertype());
        // 不为NULL的属性都将被更新
        // 使用managerService调用，使事务生效
        managerService.updateManagerInfoById(user);
    }

    @Override
    public void deleteUser(String id) {
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        String uId = user.getId();
        if (Objects.equals(id, uId)) {
            throw new BusinessException("不允许删除当前登录用户");
        }
        managerService.removeManagerInfoById(id);
    }

    @Override
    public void updatePwd(ManagerInfoVo user) {
        ManagerEntity dbUser = checkManagerId(user.getId());
        String dbPwd = dbUser.getManagerPassword();
        String paramPwd = user.getManagerPassword();
        checkUserInfo(paramPwd, null);
        if (!dbPwd.equals(DigestUtils.sha256Hex(paramPwd))) {
            throw new BusinessException("旧密码不正确");
        }
        String newPwd = DigestUtils.sha256Hex(user.getNewManagerPassword());
        if (dbPwd.equals(newPwd)) {
            throw new BusinessException("新密码与旧密码相同");
        }
        LambdaUpdateWrapper<ManagerEntity> updateWrapper = new UpdateWrapper<ManagerEntity>()
                .lambda().eq(ManagerEntity::getId, user.getId()).set(ManagerEntity::getManagerPassword, newPwd);
        update(updateWrapper);
    }

    @Override
    public ManagerInfoVo getManagerInfoById(String userId) {
        ManagerInfoVo managerInfoVo = new ManagerInfoVo();
        ManagerEntity managerEntity = getById(userId);
        ManagerInfo managerInfo = managerInfoService.getById(userId);
        BeanUtil.copyProperties(managerEntity, managerInfoVo);
        BeanUtil.copyProperties(managerInfo, managerInfoVo);
        return managerInfoVo;
    }

    /**
     * 保存用户信息：包含manger、manager_info表
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertManagerInfo(ManagerInfoVo user) {
        ManagerEntity loginUser = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        ManagerEntity managerEntity = new ManagerEntity();
        ManagerInfo managerInfo = new ManagerInfo();
        BeanUtil.copyProperties(user, managerEntity, "id");
        BeanUtil.copyProperties(user, managerInfo, "id");
        managerEntity.setCreateDate(new Date());
        managerEntity.setUpdateDate(new Date());
        managerEntity.setCreateBy(loginUser.getId());
        managerEntity.setUpdateBy(loginUser.getId());
        boolean save = this.save(managerEntity);
        if (save) {
            managerInfo.setId(managerEntity.getId());
            managerInfo.setCreateTime(new Date());
            managerInfo.setUpdateTime(new Date());
            managerInfoService.save(managerInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateManagerInfoById(ManagerInfoVo user) {
        if (Objects.isNull(user.getId())) {
            throw new BusinessException("更新失败：id为空");
        }
        ManagerEntity loginUser = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        ManagerEntity managerEntity = new ManagerEntity();
        ManagerInfo managerInfo = new ManagerInfo();
        BeanUtil.copyProperties(user, managerEntity);
        BeanUtil.copyProperties(user, managerInfo);
        managerEntity.setUpdateDate(new Date());
        managerEntity.setUpdateBy(loginUser.getId());
        managerInfo.setUpdateTime(new Date());
        saveOrUpdate(managerEntity);
        managerInfoService.saveOrUpdate(managerInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeManagerInfoById(String id) {
        removeById(id);
        managerInfoService.removeById(id);
    }

    /**
     * 校验账号唯一性及密码强度
     */
    private void checkUserInfo(String pwd, List<String> accounts) {
        //account唯一性校验
        if (CollUtil.isNotEmpty(accounts)) {
            throw new BusinessException("账号已存在");
        }
        // 密码强度校验
        boolean isPass = CheckPassword.checkPasswordRule(pwd);
        if (!isPass) {
            throw new BusinessException("密码最小长度为8位，至少包含数字、大写字母、小写字母和特殊字符中的三种");
        }
    }

    private ManagerEntity checkManagerId(String managerId) {
        ManagerEntity dbUser = getById(managerId);
        if (dbUser == null) {
            throw new BusinessException("用户信息不存在");
        }
        return dbUser;
    }

    private ManagerInfoVo checkManagerInfoId(String managerInfoId) {
        ManagerInfoVo dbUser = getManagerInfoById(managerInfoId);
        if (dbUser == null) {
            throw new BusinessException("用户信息不存在");
        }
        return dbUser;
    }
}