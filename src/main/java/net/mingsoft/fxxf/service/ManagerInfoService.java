package net.mingsoft.fxxf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.bean.entity.ManagerInfo;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;

import java.util.List;

/**
 * @author luwb
 * @date 2023-04-18
 */
public interface ManagerInfoService extends IService<ManagerInfo> {

    List<ManagerInfoVo> userList(Integer current, Integer size, String keyword);

    List<String> existsAccount(String account);

    List<ManagerInfoVo> enterpriseList(String keyword, Page<ManagerInfoVo> page);

    List<ManagerInfoVo> userList(String keyword, Page<ManagerInfoVo> page);

    List<ManagerInfoVo> industryAssociationList(String keyword, Page<ManagerInfoVo> page);

    /**
     * 新增后台用户
     *
     * @param managerInfo 后台用户信息
     * @return 新增的id
     */
    String addUser(ManagerInfoVo managerInfo);

    /**
     * 更新后台用户个人资料
     *
     * @param managerInfo 后台用户信息
     */
    void updateUserInfo(ManagerInfoVo managerInfo);

    /**
     * 删除用户
     *
     * @param id 用户id
     */
    void deleteUser(String id);

    /**
     * 更新密码
     *
     * @param managerInfo 密码信息
     */
    void updatePwd(ManagerInfoVo managerInfo);

    /**
     * 根据id查询用户信息：包含manager、manager_info信息
     */
    ManagerInfoVo getManagerInfoById(String managerId);

    /**
     * 根据id更新用户信息：包含manager、manager_info信息
     */
    void updateManagerInfoById(ManagerInfoVo managerInfo);

    /**
     * 新增用户信息：包含manager、manager_info信息
     */
    void insertManagerInfo(ManagerInfoVo managerInfo);

    /**
     * 删除用户信息：包含manager、manager_info信息
     */
    void removeManagerInfoById(String id);

}
