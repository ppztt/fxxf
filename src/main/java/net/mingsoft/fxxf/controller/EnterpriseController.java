package net.mingsoft.fxxf.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.vo.*;
import net.mingsoft.fxxf.mapper.AuditLogMapper;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.impl.EnterpriseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Api(tags = {"企业端"})
@RestController
@RequestMapping("/${ms.manager.path}/enterprise")
@Slf4j
public class EnterpriseController {

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private AuditLogMapper auditLogMapper;

    @Resource
    private ApplicantsService applicantsService;


    @GetMapping("/info/{userId}")
    @ApiOperation(value = "获取企业信息", notes = "获取企业信息")
    public BaseResult<EnterpriseInfoVo> getEnterpriseInfo(@PathVariable(value = "userId") Integer id) {
        ManagerInfoVo user = enterpriseService.getEnterpriseInfo(id);

        EnterpriseInfoVo enterpriseInfoVo = new EnterpriseInfoVo();

        BeanUtils.copyProperties(user, enterpriseInfoVo);

        return BaseResult.success(enterpriseInfoVo);
    }

    @PostMapping("/info/{userId}")
    @ApiOperation(value = "更新企业信息")
    public BaseResult updateEnterpriseInfo(@PathVariable(value = "userId") Integer id, @RequestBody EnterpriseInfoVo enterpriseInfoVo) {
        ManagerInfoVo user = enterpriseService.getEnterpriseInfo(id);

        BeanUtils.copyProperties(enterpriseInfoVo, user, "id");
        user.setManagerNickName(enterpriseInfoVo.getRealname());

        enterpriseService.updateEnterpriseInfo(user);

        return BaseResult.success("更新企业信息成功！");
    }


    @GetMapping("/apply")
    @ApiOperation(value = "获取当前企业所有申请信息")
    public BaseResult<BasePageResult<Applicants>> getEnterpriseApplyInfo(
            @RequestParam(name = "current", defaultValue = "1") Integer current,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "userId") Integer userId) {

        BasePageResult<Applicants> applyInfo = enterpriseService.getEnterpriseApplyInfo(current, size, userId);

        return BaseResult.success(applyInfo);

    }

    @GetMapping("/apply/{id}")
    @ApiOperation(value = "根据id获取申请信息", notes = "根据id获取申请信息")
    public BaseResult<ApplicantsParamsVo> getEnterpriseApplyInfoById(@PathVariable(value = "id") Integer id) {
        Applicants applicants = enterpriseService.getEnterpriseApplyInfoById(id);

        ApplicantsParamsVo applicantsParamsVo = new ApplicantsParamsVo();
        if (applicants != null) {
            BeanUtils.copyProperties(applicants, applicantsParamsVo);
            applicantsParamsVo.setManagement(applicants.getManagement());
            if (applicants.getDetails() != null && StringUtils.isNotBlank(applicants.getDetails())) {
                applicantsParamsVo.setDetails(Arrays.asList(applicants.getDetails().split(",")));
            }

            List<AuditLogVo> auditLogs = auditLogMapper.getAuditLog(id, 2);
            if (!Objects.equals(applicants.getStatus(), 1) || !Objects.equals(applicants.getStatus(), 7)) {
                if (!CollectionUtils.isEmpty(auditLogs)) {
                    auditLogs.get(0).setContents("");
                }
            }

            applicantsParamsVo.setAuditLogs(auditLogs);
        }

        return BaseResult.success(applicantsParamsVo);
    }

    @PostMapping("/apply/1")
    @ApiOperation(value = "申请信息-放心消费承诺新申请")

    public BaseResult<EnterpriseUnitNewApplyVo> saveEnterpriseUnitApplyInfo(@RequestParam(value = "userId") Integer id,
                                                                            @RequestBody EnterpriseUnitNewApplyVo enterpriseUnitNewApplyVo) {
        try {

            List<Applicants> applicantsByCreditCode = applicantsService.findApplicantsByCreditCode(1, enterpriseUnitNewApplyVo.getCreditCode());

            if (!CollectionUtils.isEmpty(applicantsByCreditCode)) {
                return BaseResult.fail("存在相同统一社会信用代码");
            }

            Applicants applicants = newUnitApplicants(enterpriseUnitNewApplyVo);
            applicants.setCreater(id);

            if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
                applicants.setAuditRoleId(4);
            } else if (StringUtils.isNotBlank(applicants.getCity())) {
                applicants.setAuditRoleId(3);
            }

            if (enterpriseUnitNewApplyVo.getDetails() != null && enterpriseUnitNewApplyVo.getDetails().size() > 0) {
                applicants.setDetails(String.join(",", enterpriseUnitNewApplyVo.getDetails()));
            }

            //多个地址解析
            if (StringUtils.isNotBlank(enterpriseUnitNewApplyVo.getAddrs())) {
                JSONArray jsonArray = JSON.parseArray(enterpriseUnitNewApplyVo.getAddrs());

                int size = jsonArray.size();
                String citys = "";
                String districts = "";
                String addressStr = "";
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String city = jsonObject.getString("city");
                    String district = jsonObject.getString("district");
                    String address = jsonObject.getString("address");
                    citys += city;
                    districts += district;
                    addressStr += address;

                    //验证多个地址是否完整
                    if (StringUtils.isEmpty(city) || StringUtils.isEmpty(district) || StringUtils.isEmpty(address)) {

                        return BaseResult.fail("地址不全,请补全");
                    }

                    if ((size - 1) != i) {
                        citys += ",";
                        districts += ",";
                        addressStr += ",";
                    }
                }
                applicants.setCity(citys);
                applicants.setDistrict(districts);
                applicants.setAddress(addressStr);
            }

            enterpriseService.saveEnterpriseApplyInfo(applicants);

            return BaseResult.success();
        } catch (Exception e) {
            log.error("申请信息-放心消费承诺新申请enterprise/apply/1接口异常", e);
            return BaseResult.fail(e.getMessage());
        }
    }

    @PostMapping("/apply/2")
    @ApiOperation(value = "申请信息-无理由退货承诺新申请", notes = "申请信息-无理由退货承诺新申请")
    public BaseResult<EnterpriseStoreNewApplyVo> saveEnterpriseApplyInfo(@RequestParam(value = "userId") Integer id,
                                                                         @RequestBody EnterpriseStoreNewApplyVo enterpriseStoreNewApplyVo) {
        try {
            List<Applicants> applicantsByCreditCode = applicantsService.findApplicantsByCreditCode(2, enterpriseStoreNewApplyVo.getCreditCode());

            if (!CollectionUtils.isEmpty(applicantsByCreditCode)) {
                return BaseResult.fail("存在相同统一社会信用代码");
            }

            Applicants applicants = newStoreApplicants(enterpriseStoreNewApplyVo);
            applicants.setCreater(id);

            if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
                applicants.setAuditRoleId(4);
            } else if (StringUtils.isNotBlank(applicants.getCity())) {
                applicants.setAuditRoleId(3);
            }

            if (!CollectionUtils.isEmpty(enterpriseStoreNewApplyVo.getDetails())) {
                applicants.setDetails(String.join(",", enterpriseStoreNewApplyVo.getDetails()));
            }


            //多个地址解析
            if (StringUtils.isNotBlank(enterpriseStoreNewApplyVo.getAddrs())) {
                JSONArray jsonArray = JSON.parseArray(enterpriseStoreNewApplyVo.getAddrs());

                int size = jsonArray.size();
                String citys = "";
                String districts = "";
                String addressStr = "";
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String city = jsonObject.getString("city");
                    String district = jsonObject.getString("district");
                    String address = jsonObject.getString("address");
                    citys += city;
                    districts += district;
                    addressStr += address;

                    //验证多个地址是否完整
                    if (StringUtils.isEmpty(city) || StringUtils.isEmpty(district) || StringUtils.isEmpty(address)) {

                        return BaseResult.fail("地址不全,请补全");
                    }
                    if ((size - 1) != i) {
                        citys += ",";
                        districts += ",";
                        addressStr += ",";
                    }
                }
                applicants.setCity(citys);
                applicants.setDistrict(districts);
                applicants.setAddress(addressStr);
            }

            enterpriseService.saveEnterpriseApplyInfo(applicants);

            return BaseResult.success();
        } catch (Exception e) {
            log.error("申请信息-无理由退货承诺新申请enterprise/apply/2接口异常", e);
            return BaseResult.fail(e.getMessage());
        }
    }

    @PostMapping("/apply/u/1")
    @ApiOperation(value = "申请信息-更新放心消费承诺新申请", notes = "申请信息-更新放心消费承诺新申请")
    public BaseResult<EnterpriseUnitNewApplyVo> updateEnterpriseUnitApplyInfo(@RequestParam(value = "id") Integer id, @RequestBody EnterpriseUnitNewApplyVo enterpriseUnitNewApplyVo) {
        try {

            List<Applicants> applicantsByCreditCode = applicantsService.findApplicantsByCreditCode(id, 1, enterpriseUnitNewApplyVo.getCreditCode());

            if (applicantsByCreditCode != null && applicantsByCreditCode.size() > 0) {
                return BaseResult.fail("存在相同统一社会信用代码");
            }

            Applicants applicants = enterpriseService.getEnterpriseApplyInfoById(id);

            BeanUtils.copyProperties(enterpriseUnitNewApplyVo, applicants, "id");

            applicants.setUpdateTime(LocalDateTime.now());

            if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
                applicants.setAuditRoleId(4);
            } else if (StringUtils.isNotBlank(applicants.getCity())) {
                applicants.setAuditRoleId(3);
            }

            if (enterpriseUnitNewApplyVo.getDetails() != null && enterpriseUnitNewApplyVo.getDetails().size() > 0) {
                applicants.setDetails(String.join(",", enterpriseUnitNewApplyVo.getDetails()));
            }


            //多个地址解析
            if (StringUtils.isNotBlank(enterpriseUnitNewApplyVo.getAddrs())) {
                JSONArray jsonArray = JSON.parseArray(enterpriseUnitNewApplyVo.getAddrs());

                int size = jsonArray.size();
                String citys = "";
                String districts = "";
                String addressStr = "";
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String city = jsonObject.getString("city");
                    String district = jsonObject.getString("district");
                    String address = jsonObject.getString("address");
                    citys += city;
                    districts += district;
                    addressStr += address;

                    //验证多个地址是否完整
                    if (StringUtils.isEmpty(city) || StringUtils.isEmpty(district) || StringUtils.isEmpty(address)) {

                        return BaseResult.fail("地址不全,请补全");
                    }
                    if ((size - 1) != i) {
                        citys += ",";
                        districts += ",";
                        addressStr += ",";
                    }
                }
                applicants.setCity(citys);
                applicants.setDistrict(districts);
                applicants.setAddress(addressStr);
            }
            enterpriseService.updateEnterpriseApplyInfo(applicants);

            return BaseResult.success();
        } catch (Exception e) {
            log.error("申请信息-更新放心消费承诺新申请/apply/u/1接口异常", e);
            return BaseResult.fail(e.getMessage());
        }
    }

    @PostMapping("/apply/u/2")
    @ApiOperation(value = "申请信息-更新无理由退货承诺新申请", notes = "申请信息-更新无理由退货承诺新申请")
    public BaseResult<EnterpriseStoreNewApplyVo> updateEnterpriseApplyInfo(@RequestParam(value = "id") Integer id, @RequestBody EnterpriseStoreNewApplyVo enterpriseStoreNewApplyVo) {
        try {
            List<Applicants> applicantsByCreditCode = applicantsService.findApplicantsByCreditCode(id, 2, enterpriseStoreNewApplyVo.getCreditCode());

            if (applicantsByCreditCode != null && applicantsByCreditCode.size() > 0) {
                return BaseResult.fail("存在相同统一社会信用代码");
            }

            Applicants applicants = enterpriseService.getEnterpriseApplyInfoById(id);

            BeanUtils.copyProperties(enterpriseStoreNewApplyVo, applicants, "id");

            applicants.setUpdateTime(LocalDateTime.now());

            if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
                applicants.setAuditRoleId(4);
            } else if (StringUtils.isNotBlank(applicants.getCity())) {
                applicants.setAuditRoleId(3);
            }

            if (enterpriseStoreNewApplyVo.getDetails() != null && enterpriseStoreNewApplyVo.getDetails().size() > 0) {
                applicants.setDetails(String.join(",", enterpriseStoreNewApplyVo.getDetails()));
            }


            //多个地址解析
            if (StringUtils.isNotBlank(enterpriseStoreNewApplyVo.getAddrs())) {
                JSONArray jsonArray = JSON.parseArray(enterpriseStoreNewApplyVo.getAddrs());

                int size = jsonArray.size();
                String citys = "";
                String districts = "";
                String addressStr = "";
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String city = jsonObject.getString("city");
                    String district = jsonObject.getString("district");
                    String address = jsonObject.getString("address");
                    citys += city;
                    districts += district;
                    addressStr += address;

                    //验证多个地址是否完整
                    if (StringUtils.isEmpty(city) || StringUtils.isEmpty(district) || StringUtils.isEmpty(address)) {

                        return BaseResult.fail("地址不全,请补全");
                    }
                    if ((size - 1) != i) {
                        citys += ",";
                        districts += ",";
                        addressStr += ",";
                    }
                }
                applicants.setCity(citys);
                applicants.setDistrict(districts);
                applicants.setAddress(addressStr);
            }

            enterpriseService.updateEnterpriseApplyInfo(applicants);

            return BaseResult.success();
        } catch (Exception e) {
            log.error("申请信息-更新无理由退货承诺新申请/enterprise/apply/u/2接口异常", e);
            return BaseResult.fail(e.getMessage());
        }
    }

    public static Applicants newUnitApplicants(EnterpriseUnitNewApplyVo a) {
        Applicants applicants = new Applicants();

        BeanUtils.copyProperties(a, applicants);

        applicants.setType(1);
        applicants.setStatus(4);
        applicants.setContCommitment("否");
        applicants.setCreateTime(LocalDateTime.now());
        applicants.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isNotBlank(a.getContents4())) {
            applicants.setAddContents4Cnt(1);
        }
        applicants.setContents1("不提供假冒伪劣商品，不提供“三无”产品，不提供不合格商品，不提供来源不明商品，不提供过期商品，不提供缺陷商品，不提供侵犯知识产权商品。");
        applicants.setContents2("不作虚假宣传，不搞低价诱导；恪守服务承诺，履行合同约定；明码实价，明白消费；守法经营，诚信待客。");
        applicants.setContents3("履行保护消费者权益第一责任，提供便捷售后服务，高效处理消费纠纷，承担先行赔付和首问责任。");


        applicants.setCreateType("企业提交");

        return applicants;
    }

    public static Applicants newStoreApplicants(EnterpriseStoreNewApplyVo a) {
        Applicants applicants = new Applicants();

        BeanUtils.copyProperties(a, applicants);

        applicants.setType(2);
        applicants.setStatus(4);
        applicants.setCreateTime(LocalDateTime.now());
        applicants.setPubTime(LocalDateTime.now());
        applicants.setUpdateTime(LocalDateTime.now());

        applicants.setCreateType("企业提交");

        return applicants;
    }


    public static Applicants newUnitApplicants(TransportUnitNewApplyVo a) {
        Applicants applicants = new Applicants();

        BeanUtils.copyProperties(a, applicants);

        applicants.setType(1);
        applicants.setContCommitment("否");
        applicants.setCreateTime(LocalDateTime.now());
        applicants.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isNotBlank(a.getContents4())) {
            applicants.setAddContents4Cnt(1);
        }

        //市级用户和省级用户导入，直接在期
        applicants.setStatus(1);
        applicants.setStartTime(LocalDate.now());
        LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
        applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
        applicants.setCcDate(LocalDateTime.now());

        applicants.setContents1("不提供假冒伪劣商品，不提供“三无”产品，不提供不合格商品，不提供来源不明商品，不提供过期商品，不提供缺陷商品，不提供侵犯知识产权商品。");
        applicants.setContents2("不作虚假宣传，不搞低价诱导；恪守服务承诺，履行合同约定；明码实价，明白消费；守法经营，诚信待客。");
        applicants.setContents3("履行保护消费者权益第一责任，提供便捷售后服务，高效处理消费纠纷，承担先行赔付和首问责任。");


        applicants.setCreateType("市级录入");

        return applicants;
    }

    public static Applicants newStoreApplicants(TransportStoreNewApplyVo a) {
        Applicants applicants = new Applicants();

        BeanUtils.copyProperties(a, applicants);

        applicants.setType(2);
        applicants.setCreateTime(LocalDateTime.now());
        applicants.setPubTime(LocalDateTime.now());
        applicants.setUpdateTime(LocalDateTime.now());

        applicants.setStatus(1);
        applicants.setStartTime(LocalDate.now());
        LocalDate localDate3After = applicants.getStartTime().plusYears(3).minusMonths(1);
        applicants.setEndTime(LocalDate.of(localDate3After.getYear(), localDate3After.getMonthValue(), 01));
        applicants.setCcDate(LocalDateTime.now());

        applicants.setCreateType("市级录入");

        return applicants;
    }

}
