package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.fxxf.entity.Applicants;
import net.mingsoft.fxxf.entity.AuditLog;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.AuditLogService;
import net.mingsoft.fxxf.vo.ApplicantsStoreExcelImportVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author laijunbao
 */
@Service
public class MyApplicantsStoreService {

    @Autowired
    private ApplicantsService applicantsService;

    @Resource
    private AuditLogService auditLogService;

    /**
     * @param applicantsStoreExcelVos 导入模板数据
     * @return
     * @throws
     * @description 无理由退货实体店导入
     * @author laijunbao
     * @updateTime 2020-01-16 0016 16:58
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Applicants> templateImport(List<ApplicantsStoreExcelImportVo> applicantsStoreExcelVos) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();

        List<String> creditCodes = new ArrayList<>();
        for (int i = 0; i < applicantsStoreExcelVos.size(); i++) {
            creditCodes.add(applicantsStoreExcelVos.get(i).getCreditCode());
        }

        List<Applicants> applicantsByDbs = applicantsService.list(new QueryWrapper<Applicants>()
                .eq("type", 2)
                .in("credit_code", creditCodes));

        List<Applicants> applicantsList = new ArrayList<>();
        List<Applicants> applicantsListNeedUpdate = new ArrayList<>();
        if (applicantsStoreExcelVos.size() > 0) {
            applicantsStoreExcelVos.parallelStream().forEach(a -> {
                Applicants applicantsByDb = null;

                if (applicantsByDbs != null && applicantsByDbs.size() > 0) {
                    List<Applicants> collect = applicantsByDbs.parallelStream().filter(app -> Objects.equals(app.getCreditCode().trim(), a.getCreditCode().trim())).collect(Collectors.toList());
                    if (collect != null && collect.size() > 0) {
                        applicantsByDb = collect.get(0);
                    }
                }

                if (applicantsByDb != null && applicantsByDb.getStatus() == 4) {
                    // 待审核
                    BeanUtils.copyProperties(a, applicantsByDb);
                    applicantsByDb.setUpdateTime(LocalDateTime.now());

                } else {
                    Applicants applicants = new Applicants();
                    BeanUtils.copyProperties(a, applicants);
                    applicants.setType(2);

                    if (user.getRoleId() == 2 || user.getRoleId() == 1) {
                        applicants.setStatus(1);

                        applicants.setStartTime(LocalDate.now());
                        LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
                        applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
                        applicants.setCcDate(LocalDateTime.now());
                    } else {
                        applicants.setStatus(4);
                    }

                    applicants.setCreateTime(LocalDateTime.now());
                    applicants.setUpdateTime(LocalDateTime.now());
                    if (a.getApplicationDate() != null) {
                        applicants.setApplicationDate(LocalDateTime.ofInstant(a.getApplicationDate().toInstant(),
                                ZoneId.systemDefault()));
                    }
                    if (a.getIndustryDate() != null) {
                        applicants.setIndustryDate(LocalDateTime.ofInstant(a.getIndustryDate().toInstant(),
                                ZoneId.systemDefault()));
                    }
                    if (a.getCcDate() != null) {
                        applicants.setCcDate(LocalDateTime.ofInstant(a.getCcDate().toInstant(),
                                ZoneId.systemDefault()));
                    }

//                    // 获取登录用户
//                    User user = (User) SecurityUtils.getSubject().getPrincipal();
                    // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
                    Integer roleId = user.getRoleId();
                    applicants.setAuditRoleId(roleId);
                    applicants.setCreater(user.getId());
                    if (Objects.equals(roleId, 1)) {
                        applicants.setCreateType("省级导入");
                    } else if (Objects.equals(roleId, 2)) {
                        applicants.setCreateType("地市导入");
                    } else if (Objects.equals(roleId, 3)) {
                        applicants.setCreateType("区县导入");
                    } else if (Objects.equals(roleId, 4)) {
                        applicants.setCreateType("行业协会导入");
                    }

                    applicantsList.add(applicants);
                }
            });
            if (applicantsList.size() > 0) {

                applicantsService.saveBatch(applicantsList);

                if (user.getRoleId() == 2) {
                    saveAuditLogByCityImport(applicantsList);
                }
            }

            if (applicantsListNeedUpdate.size() > 0) {
                applicantsService.updateBatchById(applicantsListNeedUpdate);
            }
        }

        return applicantsList;
    }

    public void saveAuditLogByCityImport(List<Applicants> applicants) {
        // 获取登录用户
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        Integer roleId = user.getRoleId();

        List<AuditLog> auditLogs = new ArrayList<>();

        applicants.forEach(app -> {
            // 审核记录
            AuditLog auditLog = new AuditLog();
            auditLog.setAppId(app.getId());
            auditLog.setAuditor(user.getId());
            auditLog.setContents("市一级导入");
            auditLog.setCreateTime(LocalDateTime.now());
            auditLog.setRoleId(roleId);
            auditLog.setStatus(1);

            auditLogs.add(auditLog);
        });

        auditLogService.saveBatch(auditLogs);
    }

    public List<Applicants> findApplicantsByRegName(String creditCode) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .eq("credit_code", creditCode)
                .eq("type", 2)
                .eq("status", 1));
    }

    public List<Applicants> findApplicantsByIdRegName(Integer id, String creditCode) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .eq("credit_code", creditCode)
                .eq("type", 2)
                .eq("status", 1)
                .notIn("id", id));
    }
}
