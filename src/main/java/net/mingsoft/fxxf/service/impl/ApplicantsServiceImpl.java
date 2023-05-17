package net.mingsoft.fxxf.service.impl;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.exception.BusinessException;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.AuditLog;
import net.mingsoft.fxxf.bean.entity.Region;
import net.mingsoft.fxxf.bean.enums.ApplicantsTypeEnum;
import net.mingsoft.fxxf.bean.enums.CreateTypeEnum;
import net.mingsoft.fxxf.bean.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatisticsRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatusUpdateRequest;
import net.mingsoft.fxxf.bean.request.EnterpriseNewApplyRequest;
import net.mingsoft.fxxf.bean.vo.*;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.AuditLogMapper;
import net.mingsoft.fxxf.mapper.RegionMapper;
import net.mingsoft.fxxf.service.ApplicantsService;
import net.mingsoft.fxxf.service.AuditLogService;
import net.mingsoft.fxxf.service.ManagerInfoService;
import net.mingsoft.fxxf.vaild.ApplicantsStoreExcelVerifyHandlerImpl;
import net.mingsoft.fxxf.vaild.ApplicantsUnitExcelVerifyHandlerImpl;
import net.mingsoft.utils.ApplicantsImportUtil;
import net.mingsoft.utils.EasyExcelUtil;
import net.mingsoft.utils.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 申报单位 服务实现类
 */
@Service
@Slf4j
public class ApplicantsServiceImpl extends ServiceImpl<ApplicantsMapper, Applicants> implements ApplicantsService {

    private final String[] unitImportTitle = new String[]{"经营者注册名称", "统一社会信用代码", "门店名称", "经营场所-所在市", "经营场所-所在区县",
            "经营场所-详细地址", "网店名称", "所属平台", "经营类别", "类别明细",
            "负责人姓名", "负责人电话", "其他承诺事项及具体内容", "企业申请日期"};

    private final String[] storeImportTitle = new String[]{"经营者注册名称", "统一社会信用代码", "门店名称", "经营场所-所在市",
            "经营场所-所在区县", "经营场所-详细地址", "经营类别", "类别明细", "负责人姓名", "负责人电话",
            "适用商品-承诺事项及内容", "退货期限（天）",
            "退货约定-承诺事项及内容", "企业申请日期"};


    /**
     * 导入模板
     */
    @Value("${unitTemplateFilePath}")
    private String unitTemplateFilePath;


    @Value("${storeTemplateFilePath}")
    private String storeTemplateFilePath;

    /**
     * 导入模板临时文件
     */
    @Value("${importFileTmp}")
    private String importFileTmp;

    @Autowired
    private ApplicantsMapper applicantsMapper;

    @Resource
    private ManagerInfoService managerInfoService;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private MyAuditLogService myAuditLogService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private EnterpriseService enterpriseService;


    @Autowired
    private ApplicantsStoreExcelVerifyHandlerImpl storeVerifyHandler;

    @Autowired
    private ApplicantsUnitExcelVerifyHandlerImpl unitVerifyHandler;

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public List<RegionVo> getGdRegion() {
        return baseMapper.getGdRegion();
    }


    @Override
    public BaseResult<BasePageResult<Applicants>> listPage(ApplicantsPageRequest applicantsPageRequest) {
        // 获取登录用户 roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return BaseResult.fail("当前登录用户为空，请重新登陆");
        }

        ManagerInfoVo extensionInfo = managerInfoService.getManagerInfoById(user.getId());
        if (extensionInfo == null) {
            return BaseResult.fail("当前登录用户扩展信息为空，请前往补充");
        }

        IPage<Applicants> applicantsIPage = applicantsMapper.listPage(new Page<>(
                        applicantsPageRequest.getCurrent(), applicantsPageRequest.getSize()), applicantsPageRequest, user.getRoleId(),
                extensionInfo.getCity(), extensionInfo.getDistrict());
        setExtension(applicantsIPage);
        return BaseResult.success(new BasePageResult<>(applicantsIPage.getCurrent(), applicantsIPage.getSize(), applicantsIPage.getPages(),
                applicantsIPage.getTotal(), applicantsIPage.getRecords()));
    }

    @Override
    public ApplicantsFindVo findApplicantsByRegName(Integer id, String creditCode, String type) {
        List<Applicants> applicants = findApplicantsByIdRegName(id, creditCode, type);

        if (!CollectionUtils.isEmpty(applicants)) {
            return new ApplicantsFindVo(true);
        }

        return new ApplicantsFindVo(false);
    }


    /**
     * 经营者列表-根据id查询单位
     */
    @Override
    public ApplicantsParamsVo findApplicants(Applicants applicants) {
        ApplicantsParamsVo applicantsParamsVo = new ApplicantsParamsVo();

        BeanUtils.copyProperties(applicants, applicantsParamsVo);
        applicantsParamsVo.setManagement(applicants.getManagement());
        if (StringUtils.isNotBlank(applicants.getDetails())) {
            applicantsParamsVo.setDetails(Arrays.asList(applicants.getDetails().split(",")));
        }

        List<AuditLogVo> auditLogs = auditLogMapper.getAuditLog(applicants.getId(), null);

        applicantsParamsVo.setAuditLogs(auditLogs);

        return applicantsParamsVo;
    }

    /**
     * 经营者列表-根据单位id更新状态及原因
     */
    @Override
    public BaseResult<String> updateApplicantsStatus(ApplicantsStatusUpdateRequest applicantsStatusUpdateRequest) {
        Integer applicantsId = applicantsStatusUpdateRequest.getApplicantsId();
        Applicants applicants = getById(applicantsId);

        if (applicants != null) {
            applicants.setDelReason(applicantsStatusUpdateRequest.getDelReason());
            applicants.setDelOther(applicantsStatusUpdateRequest.getDelOther());
            applicants.setDelTime(LocalDateTime.now());
            applicants.setUpdateTime(LocalDateTime.now());
            applicants.setStatus(applicantsStatusUpdateRequest.getStatus());

            return applicants.updateById() ? BaseResult.success(String.format("经营者列表-根据单位id:%d更新状态及原因成功", applicantsId)) :
                    BaseResult.fail(String.format("经营者列表-根据单位id:%d更新状态及原因失败！", applicantsId));

        } else {
            return BaseResult.fail("单位不存在");
        }
    }

    /**
     * 经营者列表-编辑保存
     */
    @Override
    public BaseResult updateApplicants(ApplicantsParamsVo applicantsParamsVo) {
        Integer paramsVoId = applicantsParamsVo.getId();
        Applicants applicantsById = getById(paramsVoId);
        if (applicantsById == null) {
            return BaseResult.fail("承诺单位不存在");
        }

        BeanUtils.copyProperties(applicantsParamsVo, applicantsById, "id,status");

        if (StringUtils.isNotBlank(applicantsParamsVo.getStartTime())) {
            applicantsById.setStartTime(LocalDate.parse(applicantsParamsVo.getStartTime()));
        }
        if (StringUtils.isNotBlank(applicantsParamsVo.getEndTime())) {
            applicantsById.setEndTime(LocalDate.parse(applicantsParamsVo.getEndTime()));
        }

        // 类别明细
        if (!CollectionUtils.isEmpty(applicantsParamsVo.getDetails())) {
            applicantsById.setDetails(String.join(",", applicantsParamsVo.getDetails()));
        }

        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        // 不通过的数据，更改为待审核作态
        if (Objects.equals(applicantsById.getStatus(), 7)) {
            applicantsById.setStatus(4);
            applicantsById.setAuditRoleId(user.getRoleId() + 1);
        }

        //多个地址解析
        if (StringUtils.isNotBlank(applicantsParamsVo.getAddrs())) {
            JSONArray jsonArray = JSON.parseArray(applicantsParamsVo.getAddrs());

            int size = jsonArray.size();
            StringBuilder citys = new StringBuilder();
            StringBuilder districts = new StringBuilder();
            StringBuilder addressStr = new StringBuilder();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String city = jsonObject.getString("city");
                String district = jsonObject.getString("district");
                String address = jsonObject.getString("address");

                //验证多个地址是否完整
                if (StringUtils.isEmpty(city) || StringUtils.isEmpty(district) || StringUtils.isEmpty(address)) {
                    return BaseResult.fail("地址不全,请补全");
                }

                citys.append(city);
                districts.append(district);
                addressStr.append(address);

                if ((size - 1) != i) {
                    citys.append(",");
                    districts.append(",");
                    addressStr.append(",");
                }
            }

            applicantsById.setCity(citys.toString());
            applicantsById.setDistrict(districts.toString());
            applicantsById.setAddress(addressStr.toString());
        }


        return updateById(applicantsById) ? BaseResult.success(String.format("经营者列表-根据id：%d更新单位成功", paramsVoId)) :
                BaseResult.fail(String.format("经营者列表-根据id：%d更新单位失败", paramsVoId));

    }

    /**
     * 经营者列表-模板下载
     */
    @Override
    public void downTemplateFile(Integer type, HttpServletRequest request, HttpServletResponse response) {
        String bizTemplateFile = ApplicantsTypeEnum.UNIT.getCode().equals(type) ? unitTemplateFilePath : storeTemplateFilePath;
        try {
            log.info("下载模板文件路径：{}", bizTemplateFile);
            TemplateExportParams params = new TemplateExportParams(bizTemplateFile);
            // 输出全部的sheet
            params.setScanAllsheet(true);
            Workbook workbook = ExcelExportUtil.exportExcel(params, new HashMap<>());
            String fileName = ApplicantsTypeEnum.UNIT.getCode().equals(type) ? "放心消费承诺单位导入模板（备注：请启用宏）v1.xlsm" : "线下无理由退货承诺店导入模板（备注：请启用宏）v1.xlsm";
            ExcelUtil.downLoadExcel(fileName, request, response, workbook);
        } catch (Exception e) {
            log.error("经营者列表-模板下载接口异常", e);
        }
    }

    @Override
    public BaseResult templatePreImport(Integer type, MultipartFile file) {
        String yyyyMMddPattern = "yyyy-MM-dd";
        DateFormat dateformat = new SimpleDateFormat(yyyyMMddPattern);

        ArrayList<ExcelImportErrorMsgVo> errorMsgVoList = new ArrayList<>();
        try (InputStream in = file.getInputStream()) {
            ImportParams importParams = new ImportParams();
            importParams.setStartRows(1);
            importParams.setNeedVerify(true);

            ExcelImportResult<ApplicantsExcelImportVo> result = new ExcelImportResult<>();
            if (ApplicantsTypeEnum.UNIT.getCode().equals(type)) {
                importParams.setVerifyHandler(unitVerifyHandler);
                importParams.setImportFields(unitImportTitle);
                ExcelImportResult<ApplicantsUnitExcelImportVo> unitApplicantsImportRes = ExcelImportUtil.importExcelMore(
                        in, ApplicantsUnitExcelImportVo.class, importParams);

                result.setVerifyFail(unitApplicantsImportRes.isVerifyFail());
                result.setList(BeanUtil.copyToList(unitApplicantsImportRes.getList(), ApplicantsExcelImportVo.class));
                result.setFailList(BeanUtil.copyToList(unitApplicantsImportRes.getFailList(), ApplicantsExcelImportVo.class));
            }

            if (ApplicantsTypeEnum.STORE.getCode().equals(type)) {
                importParams.setVerifyHandler(storeVerifyHandler);
                importParams.setImportFields(storeImportTitle);
                ExcelImportResult<ApplicantsStoreExcelImportVo> storeApplicantsImportRes = ExcelImportUtil.importExcelMore(
                        in, ApplicantsStoreExcelImportVo.class, importParams);

                result.setVerifyFail(storeApplicantsImportRes.isVerifyFail());
                result.setList(BeanUtil.copyToList(storeApplicantsImportRes.getList(), ApplicantsExcelImportVo.class));
                result.setFailList(BeanUtil.copyToList(storeApplicantsImportRes.getFailList(), ApplicantsExcelImportVo.class));
            }


            // 是否存在校验失败
            if (result.isVerifyFail()) {
                List<ApplicantsExcelImportVo> failList = result.getFailList();
                failList.forEach(e -> {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo(e.getRowNum(), e.getErrorMsg()));
                });
                if (!errorMsgVoList.isEmpty()) {
                    return BaseResult.fail(errorMsgVoList);
                }
            }

            List<ApplicantsExcelImportVo> applicantsExcelImportVos = result.getList();
            if (!applicantsExcelImportVos.isEmpty()) {
                //检测数据是否跨区县
                // 获取登录用户
                ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
                if (user == null) {
                    return BaseResult.fail("当前登录用户为空，请重新登陆");
                }

                ManagerInfoVo userExtensionInfo = managerInfoService.getManagerInfoById(user.getId());
                if (userExtensionInfo == null) {
                    return BaseResult.fail("当前登录用户扩展信息为空，请前往补充");
                }

                // 地区数据校验
                int roleId = user.getRoleId();
                if (roleId == 3) {
                    String district = userExtensionInfo.getDistrict();
                    for (ApplicantsExcelImportVo applicantsExcelImportVo : applicantsExcelImportVos) {
                        String vo = applicantsExcelImportVo.getDistrict();
                        if (!vo.contains(district)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他地区数据，请去掉后再上传"));
                            return BaseResult.fail(errorMsgVoList);
                        }
                    }
                } else if (roleId == 2) {
                    String city = userExtensionInfo.getCity();
                    for (ApplicantsExcelImportVo applicantsExcelImportVo : applicantsExcelImportVos) {
                        String vo = applicantsExcelImportVo.getCity();
                        if (!vo.contains(city)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他市区数据，请去掉后再上传"));
                            return BaseResult.fail(errorMsgVoList);
                        }
                    }
                } else if (roleId == 4) {
                    String city = userExtensionInfo.getCity();
                    for (ApplicantsExcelImportVo applicantsExcelImportVo : applicantsExcelImportVos) {
                        String vo = applicantsExcelImportVo.getCity();
                        if (!vo.contains(city)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他市区数据，请去掉后再上传"));
                            return BaseResult.fail(errorMsgVoList);
                        }
                    }
                }

                // 日期校验
                for (ApplicantsExcelImportVo applicantsExcelImportVo : applicantsExcelImportVos) {
                    Date curApplicantDate = applicantsExcelImportVo.getApplicationDate();
                    String strdate = dateformat.format(curApplicantDate);
                    if (strdate.length() != yyyyMMddPattern.length()) {
                        errorMsgVoList.add(new ExcelImportErrorMsgVo(String.format("经营者注册名称：%s，企业申请日期格式不对，应为yyyy-MM-dd", applicantsExcelImportVo.getRegName())));
                        return BaseResult.fail(errorMsgVoList);
                    }
                }

                // 统一社会信用代码校验
                List<String> creditCodes = applicantsExcelImportVos.stream()
                        .map(ApplicantsExcelImportVo::getCreditCode).collect(Collectors.toList());

                String[] array = new String[creditCodes.size()];
                array = creditCodes.toArray(array);
                List<ArrayList<String>> same = ApplicantsImportUtil.findSame(array);
                if (!CollectionUtils.isEmpty(same)) {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】存在重复，行:" + same));
                    return BaseResult.fail(errorMsgVoList);
                }

                List<Applicants> applicantsStatus5_6 = findApplicantsByCreditCodeAndStatus5_6(creditCodes.toArray(), type);
                AtomicInteger rowNum0 = new AtomicInteger(3);
                AtomicBoolean flag0 = new AtomicBoolean(false);
                boolean isAduit = applicantsExcelImportVos.stream().anyMatch(app -> {
                    for (Applicants applicants : applicantsStatus5_6) {
                        if (Objects.equals(app.getCreditCode(), applicants.getCreditCode())) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo(rowNum0.get(), "导入的统一社会信用代码中存在与现有在审核名单相同项"));
                            flag0.set(true);
                            break;
                        }
                    }
                    if (flag0.get()) {
                        return true;
                    }

                    rowNum0.getAndIncrement();
                    return false;
                });
                if (isAduit) {
                    return BaseResult.fail(errorMsgVoList);
                }

                List<Applicants> applicantsStatus1 = findApplicantsByRegName(type, creditCodes.toArray());
                AtomicInteger rowNum1 = new AtomicInteger(3);
                AtomicBoolean flag1 = new AtomicBoolean(false);
                boolean isRepact1 = applicantsExcelImportVos.stream().anyMatch(app -> {
                    for (int i = 0; i < applicantsStatus1.size(); i++) {
                        if (Objects.equals(app.getCreditCode(), applicantsStatus1.get(i).getCreditCode())) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo(rowNum1.get(), "导入的统一社会信用代码中存在与现有在期名单相同项"));
                            flag1.set(true);
                            break;
                        }
                    }
                    if (flag1.get()) {
                        return true;
                    }

                    rowNum1.getAndIncrement();
                    return false;
                });
                if (isRepact1) {
                    return BaseResult.fail(errorMsgVoList);
                }

                List<Applicants> applicantsStatus4 = findApplicantsByRegNameAndStatus4(type, creditCodes.toArray());

                boolean isRepact = false;
                AtomicInteger rowNum = new AtomicInteger(3);
                AtomicBoolean flag = new AtomicBoolean(false);
                if (!CollectionUtils.isEmpty(applicantsStatus4)) {
                    isRepact = applicantsExcelImportVos.stream().anyMatch(app -> {
                        for (Applicants applicants : applicantsStatus4) {
                            if (Objects.equals(app.getCreditCode(), applicants.getCreditCode())) {
                                errorMsgVoList.add(new ExcelImportErrorMsgVo(rowNum.get(),
                                        "导入的统一社会信用代码中存在与现有待审核名单相同项，点击确定按钮将覆盖系统中现有数据，点击取消将不上传文件内容！"));
                                flag.set(true);
                                break;
                            }
                        }
                        if (flag.get()) {
                            return true;
                        }
                        rowNum.getAndIncrement();
                        return false;
                    });
                }
                String filename = file.getOriginalFilename();
                String suffixName = filename.substring(filename.lastIndexOf("."));
                String newFileName = System.currentTimeMillis() + suffixName;

                String path = importFileTmp;
                File filePath = new File(path);
                if (!filePath.exists()) {
                    filePath.mkdir();
                }
                File dest = new File(path + newFileName);
                file.transferTo(dest);

                if (isRepact) {
                    errorMsgVoList.get(0).setFileId(dest.getName());
                } else {
                    ExcelImportErrorMsgVo errorMsgVo = new ExcelImportErrorMsgVo();
                    errorMsgVo.setFileId(dest.getName());
                    errorMsgVoList.add(errorMsgVo);
                }

                return BaseResult.success(errorMsgVoList);
            }

            errorMsgVoList.add(new ExcelImportErrorMsgVo(null, "导入文件为空，请重新选择"));

            return BaseResult.fail(errorMsgVoList);
        } catch (ExcelImportException e) {
            log.error("经营者模板预导入templatePreImport接口ExcelImportException异常:{}", e);
            if (e.getMessage().contains("不是合法的Excel模板")) {
                errorMsgVoList.add(new ExcelImportErrorMsgVo(1, "不是合法的模板"));
                return BaseResult.fail(errorMsgVoList);
            }
        } catch (Exception e) {
            log.error("经营者模板预导入templatePreImport接口其它异常:{}", e);
        }

        return BaseResult.fail();
    }

    @Override
    public BaseResult audit(Integer id, Integer type, String notes) {

        if (StringUtils.isBlank(notes)) {
            return BaseResult.fail("原因不能为空");
        }

        String flagStr = updateApplicantsStatusByAudit(type, id, notes);

        if (flagStr.equals("success")) {
            return BaseResult.success();
        }
        return BaseResult.fail(flagStr);
    }

    @Override
    public BaseResult templateImport(Integer type, String fileId) {
        File file = null;
        try {
            String path = importFileTmp;
            file = ResourceUtils.getFile(path + fileId);

            ImportParams importParams = new ImportParams();
            importParams.setStartRows(1);

            ExcelImportResult<ApplicantsExcelImportVo> result = ExcelImportUtil.importExcelMore(
                    file, ApplicantsExcelImportVo.class, importParams);

            // 是否存在校验失败
            boolean verfiyFail = result.isVerifyFail();
            List<ApplicantsExcelImportVo> failList = result.getFailList();
            List<ApplicantsExcelImportVo> applicantsExcelVos = result.getList();

            if (verfiyFail) {
                ArrayList<ExcelImportErrorMsgVo> errorMsgVoList = new ArrayList<>();
                failList.forEach(e -> {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo(e.getRowNum(), e.getErrorMsg()));
                });
                return BaseResult.fail(errorMsgVoList);
            }

            if (file != null) {
                file.delete();
            }

            if (!CollectionUtils.isEmpty(applicantsExcelVos)) {

                // 批量更新写入
                ArrayList<Applicants> applicantsList = ((ApplicantsServiceImpl) AopContext.currentProxy()).templateSyncDbWrite(type, applicantsExcelVos);

                return BaseResult.success(applicantsList);
            }

            return BaseResult.fail("导入文件为空，请重新选择");
        } catch (Exception e) {
            log.error("经营者列表-导入接口异常", e);
        }

        return BaseResult.fail("导入失败");
    }

    @Override
    public void export(Integer type, String status, HttpServletRequest request, HttpServletResponse response) {
        // 获取登录用户 roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            throw new BusinessException("当前登录用户为空，请重新登陆");
        }

        ManagerInfoVo extensionInfo = managerInfoService.getManagerInfoById(user.getId());
        if (extensionInfo == null) {
            throw new BusinessException("当前登录用户扩展信息为空，请前往补充");
        }

        int roleId = user.getRoleId();
        String city = extensionInfo.getCity();
        List<Applicants> applicantsList = applicantsMapper.applicantsExport(type, status, roleId,
                city, extensionInfo.getDistrict());

        List<ApplicantsExcelVo> applicantsExcelVos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM");
        applicantsList.forEach(app -> {
            ApplicantsExcelVo applicantsExcelVo = new ApplicantsExcelVo();
            BeanUtils.copyProperties(app, applicantsExcelVo);
            if (app.getApplicationDate() != null) {
                applicantsExcelVo.setApplicationDate(formatter.format(app.getApplicationDate()));
            }
            if (app.getIndustryDate() != null) {
                applicantsExcelVo.setIndustryDate(formatter.format(app.getIndustryDate()));
            }
            if (app.getCcDate() != null) {
                applicantsExcelVo.setCcDate(formatter.format(app.getCcDate()));
            }
            if (app.getDelTime() != null) {
                applicantsExcelVo.setDelTime(formatter.format(app.getDelTime()));
            }
            if (app.getStartTime() != null) {
                applicantsExcelVo.setStartTime(formatterDate.format(app.getStartTime()));
            }

            applicantsExcelVo.setDelReason(app.getDelReason());
            applicantsExcelVo.setDelOther(app.getDelOther());
            applicantsExcelVos.add(applicantsExcelVo);
        });

        String fileName = ApplicantsTypeEnum.UNIT.getCode().equals(type) ? "放心消费承诺单位.xlsx" : "线下无理由退货承诺店.xlsx";
        String concatName = ApplicantsTypeEnum.UNIT.getCode().equals(type) ? " 放心消费承诺-经营者统计.xlsx" : " 线下无理由退货承诺店.xlsx";
        if (roleId == 2 && !Objects.isNull(city)) {
            fileName = city + concatName;
        } else if (roleId == 3 && !Objects.isNull(city) && !Objects.isNull(extensionInfo.getDistrict())) {
            fileName = city + "_" + extensionInfo.getDistrict() + concatName;
        }

        if (CollectionUtils.isEmpty(applicantsExcelVos)) {
            applicantsExcelVos.add(new ApplicantsExcelVo());
        }
        EasyExcelUtil.exportExcel(response, ApplicantsExcelVo.class, StringUtils.EMPTY, fileName, applicantsExcelVos);
    }

    @Override
    public BaseResult<ArrayList<OperatorStatisticsVo>> operatorStatistics(ApplicantsStatisticsRequest applicantsStatisticsRequest) {
        // 获取登录用户
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return BaseResult.fail("当前登录用户为空，请重新登陆");
        }

        ManagerInfoVo extensionInfo = managerInfoService.getManagerInfoById(user.getId());
        if (extensionInfo == null) {
            return BaseResult.fail("当前登录用户扩展信息为空，请前往补充");
        }
        ArrayList<OperatorStatisticsVo> operatorStatisticsVosList = statistic(applicantsStatisticsRequest.getType(),
                applicantsStatisticsRequest.getStartTime(),
                applicantsStatisticsRequest.getEndTime(),
                user.getRoleId(), extensionInfo.getCity());
        return BaseResult.success(operatorStatisticsVosList);
    }

    @Override
    public void operatorStatisticsExport(Integer type, String startTime, String endTime,
                                         HttpServletRequest request, HttpServletResponse response) {
        // 获取登录用户
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            throw new BusinessException("当前登录用户为空，请重新登陆");
        }

        ManagerInfoVo extensionInfo = managerInfoService.getManagerInfoById(user.getId());
        if (extensionInfo == null) {
            throw new BusinessException("当前登录用户扩展信息为空，请前往补充");
        }
        // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
        int roleId = user.getRoleId();
        String city = extensionInfo.getCity();

        String fileName = ApplicantsTypeEnum.UNIT.getCode().equals(type) ? " 放心消费承诺-经营者统计.xlsx" : " 无理由退货实体店-经营者统计.xlsx";
        if (roleId == 2 && !Objects.isNull(city)) {
            fileName = city + fileName;
        } else if (roleId == 3 && !Objects.isNull(city) && !Objects.isNull(extensionInfo.getDistrict())) {
            fileName = city + "_" + extensionInfo.getDistrict() + fileName;
        }

        ArrayList<OperatorStatisticsVo> operatorStatisticsVos = statistic(type, startTime, endTime, roleId, extensionInfo.getCity());
        // unitStatisticsVos为空时会导出报错
        if (CollectionUtils.isEmpty(operatorStatisticsVos)) {
            operatorStatisticsVos.add(new OperatorStatisticsVo());
        }
        if (ApplicantsTypeEnum.UNIT.getCode().equals(type)) {
            EasyExcelUtil.exportExcel(response, UnitOperatorStatisticsVo.class, StringUtils.EMPTY, fileName, operatorStatisticsVos);
        }
        if (ApplicantsTypeEnum.STORE.getCode().equals(type)) {
            EasyExcelUtil.exportExcel(response, StoreOperatorStatisticsVo.class, StringUtils.EMPTY, fileName, operatorStatisticsVos);
        }
    }

    @Override
    public BaseResult saveEnterpriseApplyInfo(EnterpriseNewApplyRequest enterpriseNewApplyRequest) {
        List<Applicants> applicantsByCreditCode = findApplicantsByCreditCode(enterpriseNewApplyRequest.getType(), enterpriseNewApplyRequest.getCreditCode());

        if (!CollectionUtils.isEmpty(applicantsByCreditCode)) {
            return BaseResult.fail("存在相同统一社会信用代码");
        }

        Applicants applicants = new Applicants();
        BeanUtils.copyProperties(enterpriseNewApplyRequest, applicants);

        applicants.setType(enterpriseNewApplyRequest.getType()).setStatus(4).setContCommitment("否").setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now()).setCreateType("企业提交");

        if (ApplicantsTypeEnum.UNIT.getCode().equals(enterpriseNewApplyRequest.getType())) {
            applicants.setContents1("不提供假冒伪劣商品，不提供“三无”产品，不提供不合格商品，不提供来源不明商品，不提供过期商品，不提供缺陷商品，不提供侵犯知识产权商品。");
            applicants.setContents2("不作虚假宣传，不搞低价诱导；恪守服务承诺，履行合同约定；明码实价，明白消费；守法经营，诚信待客。");
            applicants.setContents3("履行保护消费者权益第一责任，提供便捷售后服务，高效处理消费纠纷，承担先行赔付和首问责任。");
        }

        if (StringUtils.isNotBlank(enterpriseNewApplyRequest.getContents4())) {
            applicants.setAddContents4Cnt(1);
        }

        // 获取登录用户
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return BaseResult.fail("当前登录用户为空，请重新登陆");
        }

        applicants.setCreater(Integer.parseInt(user.getId()));

        if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
            applicants.setAuditRoleId(4);
        } else if (StringUtils.isNotBlank(applicants.getCity())) {
            applicants.setAuditRoleId(3);
        }

        if (!CollectionUtils.isEmpty(enterpriseNewApplyRequest.getDetails())) {
            applicants.setDetails(String.join(",", enterpriseNewApplyRequest.getDetails()));
        }

        //判断录入类型
        CreateTypeEnum matchCreateTypeEnum = CreateTypeEnum.matchByCode(user.getRoleId());
        if (matchCreateTypeEnum != null) {
            applicants.setCreateType(matchCreateTypeEnum.getName());
        }

        //多个地址解析
        if (StringUtils.isNotBlank(enterpriseNewApplyRequest.getAddrs())) {
            JSONArray jsonArray = JSON.parseArray(enterpriseNewApplyRequest.getAddrs());

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
    }

    /**
     * 放心消费单位写入
     */
    @Transactional(rollbackFor = Exception.class)
    public ArrayList<Applicants> templateSyncDbWrite(Integer type, List<ApplicantsExcelImportVo> applicantsExcelVos) {

        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();

        List<String> creditCodes = applicantsExcelVos.stream().map(ApplicantsExcelImportVo::getCreditCode).collect(Collectors.toList());
        List<Applicants> applicantsByDbs = list(new QueryWrapper<Applicants>().eq("type", type).in("credit_code", creditCodes));

        ArrayList<Applicants> applicantsList = new ArrayList<>();
        List<Applicants> applicantsListNeedUpdate = new ArrayList<>();

        if (!applicantsExcelVos.isEmpty()) {
            applicantsExcelVos.forEach(a -> {
                // 第一次导入该单位时，默认连续承诺为“否”；
                // 当第二次导入时，如果在期现在是替换数据，不改变上面连续承诺的两个字段（仍为否）；
                // 如果第二次导入时第一次的有效期过期了，且导入时间在过期后一年以内，则新建这条承诺，且连续承诺字段修改为“是”，连续承诺次数记为“2”，之后导入递增；
                // 当第二次导入时，之前的承诺申请被主动摘牌，或者是过期时间超过了一年，那连续承诺记为“否”

                if (!CollectionUtils.isEmpty(applicantsByDbs)) {
                    List<Applicants> collect = applicantsByDbs.parallelStream()
                            .filter(app -> Objects.equals(app.getCreditCode().trim(), a.getCreditCode().trim())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(collect)) {
                        Applicants applicantsByDb = collect.get(0);
                        if (StringUtils.isNotBlank(a.getContents4())) {
                            applicantsByDb.setAddContents4Cnt(1);
                        }

                        if (user.getRoleId() == 1 || user.getRoleId() == 2) {
                            //市级用户和省级用户导入，直接在期
                            Applicants applicants = newApplicants(a, type);
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
                                        Applicants applicants = newApplicants(a, type);
                                        applicants.setContCommitment("是");
                                        applicants.setCommNum(commNum == 0 || commNum == null ? 2 : commNum + 1);
                                        applicantsByDb.setUpdateTime(LocalDateTime.now());
                                        applicantsList.add(applicants);
                                    } else {
                                        Applicants applicants = newApplicants(a, type);
                                        applicantsByDb.setUpdateTime(LocalDateTime.now());
                                        applicantsList.add(applicants);
                                    }
                                }
                            } else if (applicantsByDb.getStatus() == 2) {
                                Applicants applicants = newApplicants(a, type);
                                applicantsByDb.setUpdateTime(LocalDateTime.now());
                                applicantsList.add(applicants);
                            }
                        }

                        applicantsListNeedUpdate.add(applicantsByDb);
                    } else {
                        applicantsList.add(newApplicants(a, type));
                    }
                } else {
                    Applicants applicants = newApplicants(a, type);

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
                saveBatch(applicantsList);

                if (user.getRoleId() == 2) {
                    saveAuditLogByCityImport(applicantsList);
                }
            }

            if (applicantsListNeedUpdate.size() > 0) {
                updateBatchById(applicantsListNeedUpdate);
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

    private Applicants newApplicants(ApplicantsExcelImportVo a, Integer type) {
        Applicants applicants = new Applicants();
        BeanUtils.copyProperties(a, applicants);
        applicants.setType(type);
        applicants.setStatus(4);
        applicants.setContCommitment("否");
        applicants.setCreateTime(LocalDateTime.now());
        applicants.setUpdateTime(LocalDateTime.now());
        if (a.getApplicationDate() != null) {
            applicants.setApplicationDate(LocalDateTime.ofInstant(a.getApplicationDate().toInstant(), ZoneId.systemDefault()));
        }
        if (a.getIndustryDate() != null) {
            applicants.setIndustryDate(LocalDateTime.ofInstant(a.getIndustryDate().toInstant(), ZoneId.systemDefault()));
        }
        if (a.getCcDate() != null) {
            applicants.setCcDate(LocalDateTime.ofInstant(a.getCcDate().toInstant(), ZoneId.systemDefault()));
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

    /*
     * 审核 状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:(行业协会审核通过)市级审核通过; 7:审核不通过 8:行业协会审核不通过)
     * return 0：正常流程；8：已被审核；2：无权审核
     * */
    private String updateApplicantsStatusByAudit(Integer type, Integer id, String notes) {
        // 获取登录用户
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();


        Integer roleId = user.getRoleId();

        Applicants applicants = getById(id);

        if (Objects.isNull(applicants.getAuditRoleId())) {
            return "待审核的角色不能为空";
        }

        // 状态(1:在期； 0:摘牌；2:过期; 4:待审核; 5:县级审核通过; 6:(行业协会审核通过)市级审核通过; 7:审核不通过 8:行业协会审核不通过)
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
                    setStartAndEndTime(applicants);
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

                                setStartAndEndTime(applicants);
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
                        if (Objects.equals(roleId, applicants.getAuditRoleId()) && (Objects.equals(applicants.getStatus(), 5) ||
                                Objects.equals(applicants.getStatus(), 6))) {
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

                                setStartAndEndTime(applicants);
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

                updateById(applicants);

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

    public List<Applicants> findApplicantsByCreditCode(Integer type, String creditCode) {
        return list(new QueryWrapper<Applicants>().eq("credit_code", creditCode).eq("type", type).notIn("status", 0, 2, 7));
    }

    public List<Applicants> findApplicantsByCreditCodes(Integer type, List<String> creditCode) {
        return list(new QueryWrapper<Applicants>().in("credit_code", creditCode).eq("type", type).notIn("status", 0, 2, 7));
    }

    public List<Applicants> findApplicantsByCreditCode(Integer id, Integer type, String creditCode) {
        return list(new QueryWrapper<Applicants>().eq("credit_code", creditCode).eq("type", type).notIn("status", 0, 2, 7).notIn("id", id));
    }

    @Override
    public void extensionDate(Integer id) {
        ManagerEntity user = (ManagerEntity) SecurityUtils.getSubject().getPrincipal();
        int roleId = user.getRoleId();
        Applicants applicants = applicantsMapper.selectById(id);
        if (applicants == null) {
            throw new BusinessException("数据不存在");
        }
        if (!canExtension(applicants)) {
            throw new BusinessException("当前数据不允许续期");
        }
        LambdaUpdateWrapper<Applicants> updateWrapper = new UpdateWrapper<Applicants>().lambda();
        updateWrapper
                .eq(Applicants::getId, id)
                .set(Applicants::getContCommitment, "是")
                .set(Applicants::getCommNum, applicants.getCommNum() + 1)
                .set(Applicants::getUpdateTime, LocalDateTime.now());
        // 省级、地市工作人员续期不用审核
        if (roleId == 1 || roleId == 2) {
            updateWrapper
                    .set(Applicants::getStatus, 1)
                    .set(Applicants::getEndTime, applicants.getEndTime().plusYears(3));
        } else {
            // 需要审核
            updateWrapper.set(Applicants::getStatus, 4)
                    .set(Applicants::getAuditRoleId, 3);
        }
        applicantsMapper.update(null, updateWrapper);
    }

    public List<Applicants> findApplicantsByRegName(Integer type, Object[] creditCodes) {
        return list(new QueryWrapper<Applicants>().in("credit_code", creditCodes).eq("type", type).eq("status", 1));
    }

    public List<Applicants> findApplicantsByRegNameAndStatus4(Integer type, Object[] creditCodes) {
        return list(new QueryWrapper<Applicants>().in("credit_code", creditCodes).eq("type", type).eq("status", 4));
    }

    public List<Applicants> findApplicantsByCreditCodeAndStatus5_6(Object[] creditCodes, Integer type) {
        return list(new QueryWrapper<Applicants>().in("credit_code", creditCodes).eq("type", type).in("status", 5, 6));
    }

    public List<Applicants> findApplicantsByIdRegName(Integer id, String creditCode, String type) {
        return list(new QueryWrapper<Applicants>().eq("credit_code", creditCode).eq("type", type).eq("status", 1).notIn("id", id));
    }

    private ArrayList<OperatorStatisticsVo> statistic(int statisticType, String startTime, String endTime, int roleId, String city) {
        // 根据角色获取按地区统计的字段: city/district
        String areaField = roleId == 1 ? "city" : "district";
        // 按地区统计承诺单位总数、增加其他承诺的单位数量、连续承诺单位数量
        List<OperatorStatisticsVo> operatorStatisticsVosList = statisticApplicantsCount(areaField, city, statisticType,
                startTime, endTime);
        // 按地区统计‘过渡期’单位数量
        List<OperatorStatisticsVo> transitionPeriodList = applicantsMapper.statisticTransitionPeriodCount(areaField, city, statisticType,
                startTime, endTime);
        // 按单位统计处理结果为摘牌、监督告诫、其他的次数
        List<HandleResultStatisticsVo> resultStatisticList = applicantsMapper.statisticResultCount(areaField, city, statisticType,
                startTime, endTime);
        calc(operatorStatisticsVosList, transitionPeriodList, resultStatisticList);
        return new ArrayList<>(operatorStatisticsVosList);
    }

    private List<OperatorStatisticsVo> statisticApplicantsCount(String areaField, String city, int statisticType, String startTime, String endTime) {
        List<OperatorStatisticsVo> result = new ArrayList<>();
        List<OperatorStatisticsVo> operatorStatisticsVos = applicantsMapper.statisticApplicantsCount(areaField, city, statisticType, startTime, endTime);
        String area = "city".equals(areaField) ? "广东省" : city;
        List<Region> regions = regionMapper.underRegListInfoByCurName(area);
        regions.sort(Comparator.comparing(Region::getSort));
        for (Region region : regions) {
            OperatorStatisticsVo statisticsVo = new OperatorStatisticsVo();
            int applicantsCnt = 0;
            int addContents1Cnt = 0;
            int commitmentCnt = 0;
            int contents2Cnt = 0;
            for (OperatorStatisticsVo operatorStatisticsVo : operatorStatisticsVos) {
                if (CharSequenceUtil.contains(operatorStatisticsVo.getArea(), region.getName())) {
                    applicantsCnt += operatorStatisticsVo.getApplicantsCnt();
                    if (ApplicantsTypeEnum.UNIT.getCode().equals(statisticType)) {
                        addContents1Cnt += operatorStatisticsVo.getAddContents1Cnt();
                        commitmentCnt += operatorStatisticsVo.getCommitmentCnt();
                    }
                    if (ApplicantsTypeEnum.STORE.getCode().equals(statisticType)) {
                        contents2Cnt += operatorStatisticsVo.getContents2Cnt();
                    }
                }
            }
            statisticsVo.setArea(region.getName());
            statisticsVo.setApplicantsCnt(applicantsCnt);
            statisticsVo.setAddContents1Cnt(addContents1Cnt);
            statisticsVo.setCommitmentCnt(commitmentCnt);
            statisticsVo.setContents2Cnt(contents2Cnt);
            result.add(statisticsVo);
        }
        return result;
    }

    private void calc(List<OperatorStatisticsVo> operatorStatisticsVosList, List<OperatorStatisticsVo> transitionPeriodList, List<HandleResultStatisticsVo> resultStatisticList) {
        for (OperatorStatisticsVo operatorStatisticsVo : operatorStatisticsVosList) {
            String area = operatorStatisticsVo.getArea();
            int transitionPeriodCnt = 0;
            int beSupervisedCnt = 0;
            int delCnt = 0;
            int nullCnt = operatorStatisticsVo.getApplicantsCnt();

            for (OperatorStatisticsVo transitionPeriodVo : transitionPeriodList) {
                String voArea = transitionPeriodVo.getArea();
                if (CharSequenceUtil.contains(voArea, area)) {
                    transitionPeriodCnt += transitionPeriodVo.getTransitionPeriodCnt();
                }
            }

            for (HandleResultStatisticsVo handleResultStatisticsVo : resultStatisticList) {
                String voArea = handleResultStatisticsVo.getArea();
                if (CharSequenceUtil.contains(voArea, area)) {
                    if (handleResultStatisticsVo.getBeSupervisedCnt() > 0) {
                        // 即使getBeSupervisedCnt的数量大于1，也只统计1次
                        beSupervisedCnt += 1;
                    }
                    if (handleResultStatisticsVo.getDelCnt() > 0) {
                        delCnt += 1;
                    }
                    // 零有效投诉单位数量 = 承诺单位总数 - 被监督告诫单位数量 - 被摘牌单位数量 - 处理结果为其他的单位数量
                    if (handleResultStatisticsVo.getBeSupervisedCnt() > 0 || handleResultStatisticsVo.getOtherCnt() > 0 ||
                            handleResultStatisticsVo.getDelCnt() > 0) {
                        nullCnt -= 1;
                    }
                }
            }
            operatorStatisticsVo.setTransitionPeriodCnt(transitionPeriodCnt);
            operatorStatisticsVo.setBeSupervisedCnt(beSupervisedCnt);
            operatorStatisticsVo.setDelCnt(delCnt);
            operatorStatisticsVo.setNullCnt(nullCnt);
        }
    }

    /**
     * 设置是否需要续期
     */
    private void setExtension(IPage<Applicants> page) {
        List<Applicants> records = page.getRecords();
        for (Applicants applicant : records) {
            applicant.setIsExtension(canExtension(applicant));
        }
    }

    private boolean canExtension(Applicants applicant) {
        // 状态为续期
        if (applicant.getStatus() == 3) {
            return true;
        }
        if (applicant.getEndTime() == null) {
            return false;
        }
        // 有效期到期前一个月的提供续期按钮
        LocalDate currentDate = LocalDate.now();
        // 当前时间的下一个月日期
        LocalDate oneMonthAfter = currentDate.plus(Period.ofMonths(1));
        return applicant.getEndTime().isBefore(oneMonthAfter);
    }

    private static void setStartAndEndTime(Applicants applicants) {
        LocalDate startTime = applicants.getStartTime();
        LocalDate newStartTime = startTime == null ? LocalDate.now() : startTime;
        applicants.setStartTime(newStartTime);
        LocalDate endTime = applicants.getEndTime();
        if (endTime == null) {
            endTime = applicants.getStartTime().plusYears(3).minusMonths(1);
            endTime = LocalDate.of(endTime.getYear(), endTime.getMonthValue(), 1);
        } else {
            endTime = endTime.plusYears(3);
        }
        applicants.setEndTime(endTime);
    }

}
