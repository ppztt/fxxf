package net.mingsoft.fxxf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.AuditLog;
import net.mingsoft.fxxf.bean.vo.ApplicantsUnitExcelImportVo;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.AuditLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author laijunbao
 */
@Service
public class MyApplicantsUnitService {

    @Resource
    private ApplicantsService applicantsService;

    @Resource
    private MyAuditLogService myAuditLogService;

    @Resource
    private AuditLogService auditLogService;

    /**
     * @param applicantsUnitExcelVos 导入模板数据
     * @return
     * @throws
     * @description 放心消费单位导入
     * @author laijunbao
     * @updateTime 2020-01-16 0016 16:58
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Applicants> templateImport(List<ApplicantsUnitExcelImportVo> applicantsUnitExcelVos) {

        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();

        List<String> creditCodes = new ArrayList<>();
        for (int i = 0; i < applicantsUnitExcelVos.size(); i++) {
            creditCodes.add(applicantsUnitExcelVos.get(i).getCreditCode());
        }

        List<Applicants> applicantsByDbs = applicantsService.list(new QueryWrapper<Applicants>()
                .eq("type", 1)
                .in("credit_code", creditCodes));

        List<Applicants> applicantsList = new ArrayList<>();
        List<Applicants> applicantsListNeedUpdate = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (applicantsUnitExcelVos.size() > 0) {
            applicantsUnitExcelVos.stream().forEach(a -> {
                // 第一次导入该单位时，默认连续承诺为“否”；
                // 当第二次导入时，如果在期现在是替换数据，不改变上面连续承诺的两个字段（仍为否）；
                // 如果第二次导入时第一次的有效期过期了，且导入时间在过期后一年以内，则新建这条承诺，且连续承诺字段修改为“是”，连续承诺次数记为“2”，之后导入递增；
                // 当第二次导入时，之前的承诺申请被主动摘牌，或者是过期时间超过了一年，那连续承诺记为“否”

                if (applicantsByDbs != null && applicantsByDbs.size() > 0) {
                    List<Applicants> collect = applicantsByDbs.parallelStream().filter(app -> Objects.equals(app.getCreditCode().trim(), a.getCreditCode().trim())).collect(Collectors.toList());
                    if (collect != null && collect.size() > 0) {
                        Applicants applicantsByDb = collect.get(0);
                        if (StringUtils.isNotBlank(a.getContents4())) {
                            applicantsByDb.setAddContents4Cnt(1);
                        }

                        if (user.getRoleId() == 1 || user.getRoleId() == 2) {
                            //市级用户和省级用户导入，直接在期
                            Applicants applicants = newApplicants(a);
                            applicants.setStatus(1);
                            applicants.setStartTime(LocalDate.now());
                            LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
                            applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
                            applicants.setCcDate(LocalDateTime.now());

                            applicantsByDb.setUpdateTime(LocalDateTime.now());
                            applicantsList.add(applicants);
                        } else {

                            if (applicantsByDb.getStatus() == 4) {
                                // 待审核
                                BeanUtils.copyProperties(a, applicantsByDb);
                                applicantsByDb.setContCommitment("否");
                                applicantsByDb.setUpdateTime(LocalDateTime.now());
                            } else if (applicantsByDb.getStatus() == 7) {
                                // 审核不通过的重新导入，状态变为待审核
                                BeanUtils.copyProperties(a, applicantsByDb);
                                applicantsByDb.setStatus(4);
                                applicantsByDb.setAuditRoleId(user.getRoleId());
                                applicantsByDb.setCreater(Integer.parseInt(user.getId()));
                                applicantsByDb.setUpdateTime(LocalDateTime.now());
                            } else if (applicantsByDb.getStatus() == 0) {
                                if (applicantsByDb.getDelTime() != null) {
                                    int months = Period.between(applicantsByDb.getDelTime().toLocalDate(), LocalDateTime.now().toLocalDate()).getMonths();
                                    // 摘牌时间不超过3个月，更新为连续承诺，连续承诺次数加1
                                    if (months <= 3) {
                                        Integer commNum = applicantsByDb.getCommNum();
                                        Applicants applicants = newApplicants(a);
                                        applicants.setContCommitment("是");
                                        applicants.setCommNum(commNum == 0 || commNum == null ? 2 : commNum + 1);
                                        applicantsByDb.setUpdateTime(LocalDateTime.now());
                                        applicantsList.add(applicants);
                                    } else {
                                        Applicants applicants = newApplicants(a);
                                        applicantsByDb.setUpdateTime(LocalDateTime.now());
                                        applicantsList.add(applicants);
                                    }
                                }
                            } else if (applicantsByDb.getStatus() == 2) {
                                Applicants applicants = newApplicants(a);
                                applicantsByDb.setUpdateTime(LocalDateTime.now());
                                applicantsList.add(applicants);
                            }
                        }

                        applicantsListNeedUpdate.add(applicantsByDb);
                    } else {
                        applicantsList.add(newApplicants(a));
                    }
                } else {
                    Applicants applicants = newApplicants(a);

                    if (user.getRoleId() == 1 || user.getRoleId() == 2) {
                        //市级用户和省级用户导入，直接在期
                        applicants.setStatus(1);
                        applicants.setStartTime(LocalDate.now());
                        LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
                        applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
                        applicants.setCcDate(LocalDateTime.now());
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
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();

        Integer roleId = user.getRoleId();

        List<AuditLog> auditLogs = new ArrayList<>();

        applicants.forEach(app -> {
            // 审核记录
            AuditLog auditLog = new AuditLog();
            auditLog.setAppId(app.getId());
            auditLog.setAuditor(Integer.parseInt(user.getId()));
            auditLog.setContents("市一级导入");
            auditLog.setCreateTime(LocalDateTime.now());
            auditLog.setRoleId(roleId);
            auditLog.setStatus(1);

            auditLogs.add(auditLog);
        });

        auditLogService.saveBatch(auditLogs);


    }

    private Applicants newApplicants(ApplicantsUnitExcelImportVo a) {
        Applicants applicants = new Applicants();
        BeanUtils.copyProperties(a, applicants);
        applicants.setType(1);
        applicants.setStatus(4);
        applicants.setContCommitment("否");
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
        if (StringUtils.isNotBlank(a.getContents4())) {
            applicants.setAddContents4Cnt(1);
        }

        // 获取登录用户
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
        Integer roleId = user.getRoleId();

        if (Objects.equals(roleId, 1)) {
            applicants.setAuditRoleId(roleId + 1);
        } else {
            applicants.setAuditRoleId(roleId);
        }

        applicants.setCreater(Integer.parseInt(user.getId()));
        if (Objects.equals(roleId, 1)) {
            applicants.setCreateType("省级导入");
        } else if (Objects.equals(roleId, 2)) {
            applicants.setCreateType("地市导入");
        } else if (Objects.equals(roleId, 3)) {
            applicants.setCreateType("区县导入");
        } else if (Objects.equals(roleId, 4)) {
            applicants.setCreateType("行业协会导入");
        }

        applicants.setContents1("不提供假冒伪劣商品，不提供“三无”产品，不提供不合格商品，不提供来源不明商品，不提供过期商品，不提供缺陷商品，不提供侵犯知识产权商品。");
        applicants.setContents2("不作虚假宣传，不搞低价诱导；恪守服务承诺，履行合同约定；明码实价，明白消费；守法经营，诚信待客。");
        applicants.setContents3("履行保护消费者权益第一责任，提供便捷售后服务，高效处理消费纠纷，承担先行赔付和首问责任。");

        return applicants;
    }

    public List<Applicants> findApplicantsByRegName(Integer type, Object[] creditCodes) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .in("credit_code", creditCodes)
                .eq("type", type)
                .eq("status", 1));
    }

    public List<Applicants> findApplicantsByRegNameAndStatus4(Integer type, Object[] creditCodes) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .in("credit_code", creditCodes)
                .eq("type", type)
                .eq("status", 4));
    }

    public List<Applicants> findApplicantsByCreditCodeAndStatus5_6(Object[] creditCodes, Integer type) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .in("credit_code", creditCodes)
                .eq("type", type)
                .in("status", 5, 6));
    }

    public List<Applicants> findApplicantsByCreditCodes(int type, Object[] creditCodes) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .in("credit_code", creditCodes)
                .eq("type", type)
                .eq("status", 1));
    }

    public List<Applicants> findApplicantsByIdRegName(Integer id, String creditCode) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .eq("credit_code", creditCode)
                .eq("type", 1)
                .eq("status", 1)
                .notIn("id", id));
    }

    public List<Applicants> findApplicantsByCreditCode(Integer type, String creditCode) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .eq("credit_code", creditCode)
                .eq("type", type)
                .notIn("status", 0, 2, 7));
    }

    public List<Applicants> findApplicantsByCreditCodes(Integer type, List<String> creditCode) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .in("credit_code", creditCode)
                .eq("type", type)
                .notIn("status", 0, 2, 7));
    }

    public List<Applicants> findApplicantsByCreditCode(Integer id, Integer type, String creditCode) {
        return applicantsService.list(new QueryWrapper<Applicants>()
                .eq("credit_code", creditCode)
                .eq("type", type)
                .notIn("status", 0, 2, 7)
                .notIn("id", id));
    }

    /*
     * 审核
     * return 0：正常流程；8：已被审核；2：无权审核
     * */
    public String updateApplicantsStatusByAudit(Integer type, Integer id, String notes) {
        // 获取登录用户
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();


        Integer roleId = user.getRoleId();

        Applicants applicants = applicantsService.getById(id);

        if (Objects.isNull(applicants.getAuditRoleId())) {
            return "待审核的角色不能为空";
        }

            /*
            状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:行业协会审核通过; 7:审核不通过 )
            * */
        if (Objects.equals(applicants.getStatus(), 1)) {
            // 已通过审核
            return "已被审核通过";
        } else if (Objects.equals(applicants.getStatus(), 0)) {
            // 已摘牌
            return "已摘牌";
        } else if (Objects.equals(applicants.getStatus(), 2)) {
            // 过期
            return "已过期";
        } else if (roleId == 3 && applicants.getStatus() == 5) {//roleId == 1 &&
            return "已被县级审核通过";
        } else if (roleId == 4 && applicants.getStatus() == 6) {//roleId == 1 &&
            return "已被行业协会审核通过";
        } else if (applicants.getStatus() == 7) {//roleId == 1 &&
            return "已被审核不通过";
        } else if (applicants.getStatus() == 8) {//roleId == 1 &&
            return "已被行业协会审核不通过";
        } else {
            boolean access = false;


            if (roleId == 1) {
                // 省级审核
                if (type == 1) {
                    applicants.setStatus(1);

                    applicants.setStartTime(LocalDate.now());
                    LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
                    applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
                    applicants.setCcDate(LocalDateTime.now());
                } else if (type == 2) {
                    applicants.setStatus(7);
                }
                applicants.setAuditRoleId(roleId);

                access = true;
            } else if (roleId == 2) {
                if (Objects.equals(applicants.getCreateType(), "企业提交")) {
                    if (roleId == 2) {
                        // 市级审核
                        // 排除已经审核通过的
                        if (!Objects.equals(applicants.getStatus(), 1)) {
                            if (type == 1) {
                                //applicants.setStatus(6);

                                //市级审核通过
                                applicants.setStatus(1);

                                applicants.setStartTime(LocalDate.now());
                                LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
                                applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
                                applicants.setCcDate(LocalDateTime.now());

                                applicants.setAuditRoleId(1);
                            } else if (type == 2) {
                                applicants.setStatus(7);
                                applicants.setAuditRoleId(2);
                            }

                            access = true;
                        } else {
                            return "已被审核";
                        }
                    } else if (roleId == 3 && roleId < applicants.getAuditRoleId()) {
                        // 区县级审核
                        // 排除已经审核通过的或已经地市审核的
                        if (!Objects.equals(applicants.getStatus(), 1) || !Objects.equals(applicants.getStatus(), 6)) {
                            if (type == 1) {
                                applicants.setStatus(5);
                            } else if (type == 2) {
                                applicants.setStatus(7);
                            }
                            applicants.setAuditRoleId(3);
                            access = true;
                        } else {
                            return "已被审核";
                        }
                    } else {
                        // 更新审核状态
                        // 排除已经审核通过的
                        if (Objects.equals(roleId, applicants.getAuditRoleId()) && (Objects.equals(applicants.getStatus(), 5) || Objects.equals(applicants.getStatus(), 6))) {
                            if (type == 1) {
                                if (roleId == 2) {
                                    //applicants.setStatus(6);
                                    //20210125需求修改
                                    applicants.setStatus(1);
                                } else if (roleId == 3) {
                                    applicants.setStatus(5);
                                }
                            } else if (type == 2) {
                                applicants.setStatus(7);
                            }
                            access = true;
                        }
                    }
                } else {
                    if (roleId == 2) {
                        // 市级审核
                        // 排除已经审核通过的
                        if (!Objects.equals(applicants.getStatus(), 1)) {
                            if (type == 1) {
                                //applicants.setStatus(6);

                                //市级审核通过
                                applicants.setStatus(1);

                                applicants.setStartTime(LocalDate.now());
                                LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
                                applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
                                applicants.setCcDate(LocalDateTime.now());

                                applicants.setAuditRoleId(1);
                            } else if (type == 2) {
                                applicants.setStatus(7);
                                applicants.setAuditRoleId(roleId);
                            }
                            access = true;

                        } else {
                            return "已被审核";
                        }
                    } else if (roleId == 3 && roleId < applicants.getAuditRoleId()) {
                        // 区县级审核
                        // 排除已经审核通过的或已经地市审核的
                        if (!Objects.equals(applicants.getStatus(), 1) || !Objects.equals(applicants.getStatus(), 6)) {
                            if (type == 1) {
                                applicants.setStatus(5);
                            } else if (type == 2) {
                                applicants.setStatus(7);
                            }
                            access = true;
                            applicants.setAuditRoleId(roleId);
                        } else {
                            return "已被审核";
                        }
                    } else {
                        // 更新审核状态
                        // 排除已经审核通过的
                        if (Objects.equals(roleId, applicants.getAuditRoleId()) && (Objects.equals(applicants.getStatus(), 5) || Objects.equals(applicants.getStatus(), 6))) {
                            if (type == 1) {
                                if (roleId == 2) {
                                    //applicants.setStatus(6);
                                    //20210125需求修改
                                    applicants.setStatus(1);
                                } else if (roleId == 3) {
                                    applicants.setStatus(5);
                                }
                            } else if (type == 2) {
                                applicants.setStatus(7);
                            }
                            access = true;
                        }
                    }
                }

            } else if (roleId == 3) {
                //行业协会审核（区县用户审核）

                //applicants.getAuditRoleId() == 6||
                if (applicants.getAuditRoleId() == 4 && applicants.getCreateType().equals("企业提交")) {
                    // 区县级审核
                    // 排除已经审核通过的或已经地市审核的
                    if (!Objects.equals(applicants.getStatus(), 1) || !Objects.equals(applicants.getStatus(), 6)) {
                        if (type == 1) {
                            applicants.setStatus(5);
                        } else if (type == 2) {
                            applicants.setStatus(7);
                        }
                        applicants.setAuditRoleId(3);
                        access = true;
                    } else {
                        return "已被审核";
                    }
                }
            } else if (roleId == 4) {
                // 区县级审核
                // 排除已经审核通过的或已经行业协会审核的
                if (!Objects.equals(applicants.getStatus(), 6)) {
                    if (type == 1) {
                        applicants.setStatus(6);
                    } else if (type == 2) {
                        applicants.setStatus(8);
                    }
                    applicants.setAuditRoleId(6);
                    access = true;
                } else {
                    return "已被审核";
                }
            } else {
                return "未知用户类型";
            }


            if (access) {
                applicants.setUpdateTime(LocalDateTime.now());

                applicantsService.updateById(applicants);

                // 审核记录
                AuditLog auditLog = new AuditLog();
                auditLog.setAppId(applicants.getId());
                auditLog.setAuditor(Integer.parseInt(user.getId()));
                auditLog.setContents(notes);
                auditLog.setCreateTime(LocalDateTime.now());
                auditLog.setRoleId(roleId);

                if (type == 1) {
                    auditLog.setStatus(1);
                } else {
                    auditLog.setStatus(0);
                }

                AuditLog auditLogByAppId = auditLog.selectOne(new QueryWrapper<AuditLog>().eq("app_id", applicants.getId()).eq("role_id", roleId));

                if (auditLogByAppId != null) {
                    auditLogByAppId.setAuditor(auditLog.getAuditor());
                    auditLogByAppId.setContents(auditLog.getContents());
                    auditLogByAppId.setCreateTime(auditLog.getCreateTime());
                    auditLogByAppId.setRoleId(auditLog.getRoleId());
                    auditLogByAppId.setStatus(auditLog.getStatus());

                    myAuditLogService.updateAuditLog(auditLogByAppId);
                } else {
                    myAuditLogService.saveAuditLog(auditLog);
                }
            } else {
                // 无权审核
                return "抱歉您没有权限审核！！！";
            }
        }

        return "success";
    }


    public void saveBatch(List<Applicants> applicantsList) {
        applicantsService.saveBatch(applicantsList);
    }
}
