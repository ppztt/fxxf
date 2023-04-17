package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 14762
 * @title: EnterpriseService
 * @description:
 * @date 2020-09-16-001610:09
 */
@Service
public class EnterpriseService {

    @Resource
    @Lazy
    private ApplicantsService applicantsService;

    @Resource
    private UserService userService;

    public User getEnterpriseInfo(Integer id) {
        return userService.getById(id);
    }

    public void updateEnterpriseInfo(User user) {
        userService.updateById(user);
    }

    public IPage<Applicants> getEnterpriseApplyInfo(Integer current, Integer sise, Integer userId) {
        return applicantsService.page(
                new Page<>(current, sise),
                new QueryWrapper<Applicants>().eq("creater", userId).eq("create_type", "企业提交")
        );
    }

    public Applicants getEnterpriseApplyInfoById(Integer id) {
        return applicantsService.getOne(new QueryWrapper<Applicants>().eq("id", id));
    }

    public void saveEnterpriseApplyInfo(Applicants applicants) {
        applicantsService.save(applicants);
    }

    public void updateEnterpriseApplyInfo(Applicants applicants) {
        applicantsService.updateById(applicants);
    }
}
