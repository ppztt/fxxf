package net.mingsoft.fxxf.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.DynamicParameter;
import io.swagger.annotations.DynamicParameters;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.vo.TransportStoreNewApplyVo;
import net.mingsoft.fxxf.bean.vo.TransportUnitNewApplyVo;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.impl.CommonDataService;
import net.mingsoft.utils.ApplicantsImportUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.mingsoft.fxxf.controller.EnterpriseController.newStoreApplicants;
import static net.mingsoft.fxxf.controller.EnterpriseController.newUnitApplicants;

@Api(tags = {"数据传输入库接口"})
@RestController
@RequestMapping("/transport")
@Slf4j
public class TransportController {

    @Resource
    private ApplicantsService applicantsService;

    /**
     * 传输放心消费承诺数据到广州市的账号
     *
     * @param transportUnitNewApplyVos transportUnitNewApplyVos
     * @return {@link BaseResult}
     * @DocView.Name 传输放心消费承诺数据到广州市的账号
     */
    @PostMapping("/unit")
    @ApiOperation(value = "传输放心消费承诺数据到广州市的账号", notes = "传输放心消费承诺数据到广州市的账号")
    @DynamicParameters(
            name = "transportUnitNewApplyVos",
            properties = {
                    @DynamicParameter(name = "transportUnitNewApplyVos", value = "transportUnitNewApplyVos", dataTypeClass = TransportUnitNewApplyVo.class, required = true)
            }
    )

    public BaseResult unit(@RequestBody List<TransportUnitNewApplyVo> transportUnitNewApplyVos) {

        try {
            if (transportUnitNewApplyVos == null || transportUnitNewApplyVos.size() == 0) {
                return BaseResult.fail("数据不能为空");
            }

            // 校验数据
            List<Map<String, Object>> errorListMap = checkUnitData(transportUnitNewApplyVos);

            if (errorListMap.size() > 0) {
                return BaseResult.fail("5001", "数据校验不通过", errorListMap);
            }

            List<String> creditCodes = transportUnitNewApplyVos.stream().map(transportUnitNewApplyVo -> transportUnitNewApplyVo.getCreditCode()).collect(Collectors.toList());

            String[] array = new String[creditCodes.size()];
            array = creditCodes.toArray(array);
            List<ArrayList<String>> same = ApplicantsImportUtil.findSame(array);
            if (same.size() > 0) {
                return BaseResult.fail("当前数据中【统一社会信用代码】存在重复，行:" + same);
            }

            List<Applicants> applicants = applicantsService.findApplicantsByCreditCodes(1, creditCodes);

            if (applicants.size() > 0) {
                List<String> creditCodesTmp = applicants.stream().map(transportUnitNewApplyVo -> transportUnitNewApplyVo.getCreditCode()).collect(Collectors.toList());

                return BaseResult.fail("5001", "与数据库存在相同统一社会信用代码", creditCodesTmp);
            }

            List<Applicants> applicantsList = new ArrayList<>();

            transportUnitNewApplyVos.forEach(transportUnitNewApplyVo -> {
                Applicants app = newUnitApplicants(transportUnitNewApplyVo);
                app.setAuditRoleId(2);

                applicantsList.add(app);
            });

            applicantsService.saveBatch(applicantsList);
        } catch (Exception e) {
            e.printStackTrace();

            return BaseResult.fail("请求异常。" + e.getMessage());
        }

        return BaseResult.success();
    }

    @PostMapping("/store")
    @ApiOperation(value = "传输无理由退货承诺数据到广州市的账号", notes = "传输无理由退货承诺数据到广州市的账号")
    @DynamicParameters(
            name = "transportStoreNewApplyVos",
            properties = {
                    @DynamicParameter(name = "transportStoreNewApplyVos", value = "transportStoreNewApplyVos", dataTypeClass = TransportStoreNewApplyVo.class, required = true)
            }
    )

    public BaseResult store(@RequestBody List<TransportStoreNewApplyVo> transportStoreNewApplyVos) {

        try {
            if (transportStoreNewApplyVos == null || transportStoreNewApplyVos.size() == 0) {
                return BaseResult.fail("数据不能为空");
            }

            // 校验数据
            List<Map<String, Object>> errorListMap = checkStoreData(transportStoreNewApplyVos);

            if (errorListMap.size() > 0) {
                return BaseResult.fail("5001", "数据校验不通过", errorListMap);
            }

            List<String> creditCodes = transportStoreNewApplyVos.stream().map(transportUnitNewApplyVo -> transportUnitNewApplyVo.getCreditCode()).collect(Collectors.toList());

            String[] array = new String[creditCodes.size()];
            array = creditCodes.toArray(array);
            List<ArrayList<String>> same = ApplicantsImportUtil.findSame(array);
            if (same.size() > 0) {
                return BaseResult.fail("当前数据中【统一社会信用代码】存在重复，行:" + same);
            }

            List<Applicants> applicants = applicantsService.findApplicantsByCreditCodes(2, creditCodes);

            if (applicants.size() > 0) {
                List<String> creditCodesTmp = applicants.stream().map(transportUnitNewApplyVo -> transportUnitNewApplyVo.getCreditCode()).collect(Collectors.toList());

                return BaseResult.fail("5001", "与数据库存在相同统一社会信用代码", creditCodesTmp);
            }

            List<Applicants> applicantsList = new ArrayList<>();

            transportStoreNewApplyVos.forEach(transportStoreNewApplyVo -> {
                Applicants app = newStoreApplicants(transportStoreNewApplyVo);
                app.setAuditRoleId(2);

                applicantsList.add(app);
            });

            applicantsService.saveBatch(applicantsList);
        } catch (Exception e) {
            e.printStackTrace();

            return BaseResult.fail("请求异常。" + e.getMessage());
        }

        return BaseResult.success();
    }

    /**
     * <p> 校验数据
     *
     * @param enterpriseUnitNewApplyVos {List<TransportUnitNewApplyVo>}
     * @return List<Map < String, Object>>
     * @author laijunbao 2022-11-25 14:37:00
     */
    public List<Map<String, Object>> checkUnitData(List<TransportUnitNewApplyVo> enterpriseUnitNewApplyVos) {
        List<Map<String, Object>> errorListMap = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        AtomicInteger num = new AtomicInteger(1);
        enterpriseUnitNewApplyVos.forEach(enterpriseUnitNewApplyVo -> {
            List<String> list = new ArrayList<>();

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getRegName())) {
                list.add("经营者注册名称不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getCreditCode())) {
                list.add("统一社会信用代码不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getOnlineName())) {
                list.add("网店名称不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getPlatform())) {
                list.add("所属平台不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getStoreName())) {
                list.add("门店名称不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getCity())) {
                list.add("经营场所-所在市不能为空");
            } else if (!Objects.equals(enterpriseUnitNewApplyVo.getCity(), "广州市")) {
                list.add("经营场所-所在市只能为广州市。当前为：" + enterpriseUnitNewApplyVo.getCity());
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getDistrict())) {
                list.add("经营场所-所在区县不能为空");
            } else if (!CommonDataService.isAccessArea(enterpriseUnitNewApplyVo.getDistrict())) {
                list.add(String.format("经营场所-所在区县列数据不规范。当前填写的是：%s，需要从%s选择填写", enterpriseUnitNewApplyVo.getDistrict(), CommonDataService.getAreas()));
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getAddress())) {
                list.add("经营场所-详细地址不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getManagement())) {
                list.add("经营类别不能为空");
            } else if (!CommonDataService.isAccessManagements(enterpriseUnitNewApplyVo.getManagement())) {
                list.add(String.format("经营类别列数据不规范。当前填写的是：%s，需要从%s选择填写", enterpriseUnitNewApplyVo.getManagement(), CommonDataService.getManagements()));
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getDetails())) {
                list.add("类别明细不能为空");
            } else if (!CommonDataService.isAccessDetails(enterpriseUnitNewApplyVo.getDetails())) {
                list.add(String.format("类别明细列数据不规范。当前填写的是：%s，需要从%s选择填写", enterpriseUnitNewApplyVo.getDetails(), CommonDataService.getDetailss()));
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getPrincipal())) {
                list.add("负责人姓名不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getPrincipalTel())) {
                list.add("负责人电话不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getContents4())) {
                list.add("其他承诺事项及具体内容不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getApplicationDateStr())) {
                list.add("企业申请日期不能为空");
            } else if (!isDate(enterpriseUnitNewApplyVo.getApplicationDateStr())) {
                list.add(String.format("企业申请日期不规范，当前填写的是：%s，正确格式例子：2022-10-21", enterpriseUnitNewApplyVo.getApplicationDateStr()));
            } else {
                LocalDateTime localDateTime = LocalDateTime.parse(enterpriseUnitNewApplyVo.getApplicationDateStr() + " 00:00:00", fmt);
                enterpriseUnitNewApplyVo.setApplicationDate(localDateTime);
            }

            if (list.size() > 0) {
                Map<String, Object> errMap = new HashMap<>(2);
                errMap.put("rowNum", num.getAndIncrement());
                errMap.put("errMsg", list);
                errorListMap.add(errMap);
            }
        });

        return errorListMap;
    }

    /**
     * <p> 校验数据
     *
     * @param transportStoreNewApplyVos {List<TransportUnitNewApplyVo>}
     * @return List<Map < String, Object>>
     * @author laijunbao 2022-11-25 14:37:00
     */
    public List<Map<String, Object>> checkStoreData(List<TransportStoreNewApplyVo> transportStoreNewApplyVos) {
        List<Map<String, Object>> errorListMap = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        AtomicInteger num = new AtomicInteger(1);
        transportStoreNewApplyVos.forEach(enterpriseUnitNewApplyVo -> {
            List<String> list = new ArrayList<>();

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getRegName())) {
                list.add("经营者注册名称不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getCreditCode())) {
                list.add("统一社会信用代码不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getStoreName())) {
                list.add("门店名称不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getCity())) {
                list.add("经营场所-所在市不能为空");
            } else if (!Objects.equals(enterpriseUnitNewApplyVo.getCity(), "广州市")) {
                list.add("经营场所-所在市只能为广州市。当前为：" + enterpriseUnitNewApplyVo.getCity());
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getDistrict())) {
                list.add("经营场所-所在区县不能为空");
            } else if (!CommonDataService.isAccessArea(enterpriseUnitNewApplyVo.getDistrict())) {
                list.add(String.format("经营场所-所在区县列数据不规范。当前填写的是：%s，需要从%s选择填写", enterpriseUnitNewApplyVo.getDistrict(), CommonDataService.getAreas()));
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getAddress())) {
                list.add("经营场所-详细地址不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getManagement())) {
                list.add("经营类别不能为空");
            } else if (!CommonDataService.isAccessManagements(enterpriseUnitNewApplyVo.getManagement())) {
                list.add(String.format("经营类别列数据不规范。当前填写的是：%s，需要从%s选择填写", enterpriseUnitNewApplyVo.getManagement(), CommonDataService.getManagements()));
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getDetails())) {
                list.add("类别明细不能为空");
            } else if (!CommonDataService.isAccessDetails(enterpriseUnitNewApplyVo.getDetails())) {
                list.add(String.format("类别明细列数据不规范。当前填写的是：%s，需要从%s选择填写", enterpriseUnitNewApplyVo.getDetails(), CommonDataService.getDetailss()));
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getPrincipal())) {
                list.add("负责人姓名不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getPrincipalTel())) {
                list.add("负责人电话不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getApplicationDateStr())) {
                list.add("企业申请日期不能为空");
            } else if (!isDate(enterpriseUnitNewApplyVo.getApplicationDateStr())) {
                list.add(String.format("企业申请日期不规范，当前填写的是：%s，正确格式例子：2022-10-21", enterpriseUnitNewApplyVo.getApplicationDateStr()));
            } else {
                ;
                LocalDateTime localDateTime = LocalDateTime.parse(enterpriseUnitNewApplyVo.getApplicationDateStr() + " 00:00:00", fmt);
                enterpriseUnitNewApplyVo.setApplicationDate(localDateTime);
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getContents1())) {
                list.add("适用商品不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getContents2())) {
                list.add("退货期限不能为空");
            }

            if (StringUtils.isBlank(enterpriseUnitNewApplyVo.getContents3())) {
                list.add("退货约定不能为空");
            }

            if (list.size() > 0) {
                Map<String, Object> errMap = new HashMap<>(2);
                errMap.put("rowNum", num.getAndIncrement());
                errMap.put("errMsg", list);
                errorListMap.add(errMap);
            }
        });

        return errorListMap;
    }

    public boolean isDate(String str) {
        boolean isTrue = true;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate localDate = LocalDate.parse(str, dtf);
        } catch (Exception e) {
            isTrue = false;
        }

        return isTrue;
    }
}
