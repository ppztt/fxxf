package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.vo.ManagerInfoVo;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.ManagerInfoService;
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
    private ManagerInfoService managerInfoService;

    public ManagerInfoVo getEnterpriseInfo(Integer id) {
        return managerInfoService.getManagerInfoById(String.valueOf(id));
    }

    public void updateEnterpriseInfo(ManagerInfoVo user) {
        managerInfoService.updateManagerInfoById(user);
    }

    public BasePageResult<Applicants> getEnterpriseApplyInfo(Integer current, Integer sise, Integer userId) {
        Page<Applicants> enterpriseApplyPage = applicantsService.page(
                new Page<>(current, sise),
                new QueryWrapper<Applicants>().eq("creater", userId).eq("create_type", "企业提交")
        );
        return new BasePageResult<>(enterpriseApplyPage.getCurrent(), enterpriseApplyPage.getSize(),
                enterpriseApplyPage.getPages(), enterpriseApplyPage.getTotal(), enterpriseApplyPage.getRecords());
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
