package net.mingsoft.fxxf.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.entity.Record;
import net.mingsoft.fxxf.entity.*;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.AuditLogMapper;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.service.*;
import net.mingsoft.fxxf.service.impl.EnterpriseService;
import net.mingsoft.fxxf.service.impl.MyApplicantsUnitService;
import net.mingsoft.fxxf.service.impl.MyAuditLogService;
import net.mingsoft.fxxf.service.impl.MyFeedbackService;
import net.mingsoft.fxxf.vo.*;
import net.mingsoft.utils.ApplicantsImportUtil;
import net.mingsoft.utils.ApplicantsUnitExcelVerifyHandlerImpl;
import net.mingsoft.utils.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static net.mingsoft.fxxf.controller.EnterpriseController.newUnitApplicants;

/**
 * <p>
 * 申报单位 前端控制器
 * 放心消费承诺单位
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@Api(tags = {"放心消费承诺单位"})
@RestController
@RequestMapping("/applicants/unit")
@Slf4j
public class ApplicantsUnitController {

    private final String[] title = new String[]{"经营者注册名称", "统一社会信用代码", "门店名称", "经营场所-所在市", "经营场所-所在区县",
            "经营场所-详细地址", "网店名称", "所属平台", "经营类别", "类别明细",
            "负责人姓名", "负责人电话", "其他承诺事项及具体内容", "企业申请日期"};


    /**
     * 放心消费承诺单位导入模板
     */
    @Value("${unitTemplateFilePath}")
    private String unitTemplateFilePath;

    /**
     * 导入模板临时文件
     */
    @Value("${importFileTmp}")
    private String importFileTmp;

    @Autowired
    private ApplicantsService applicantsService;

    @Autowired
    private MyApplicantsUnitService myApplicantsUnitService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private MyFeedbackService myFeedbackService;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Resource
    private ApplicantsMapper applicantsMapper;

    @Resource
    FeedbackStatService statService;

    @Resource
    UserService userService;

    @Resource
    private MyAuditLogService myAuditLogService;

    @Resource
    private AuditLogMapper auditLogMapper;

    @Resource
    private EnterpriseService enterpriseService;

    /**
     * @param current  当前页
     * @param size     每页条数
     * @param city     地市
     * @param district 区县
     * @param town     镇
     * @param status   状态(1:在期； 0:摘牌；2:过期)
     * @param search   搜索条件
     * @return ApiResult
     * @throws
     * @description 获取放心消费承诺单位
     * @author laijunbao
     * @updateTime 2020-01-09-0009 14:07
     */
    @RequiresPermissions("fxxfcn:list")
    @GetMapping("/list")
//    @ApiOperation(value = "表格数据", notes = "放心消费承诺单位")
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "查询经营者列表")
    @ApiOperation(value = "经营者列表-列表", notes = "经营者列表-列表")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "city", value = "地市", dataType = "string", example = "广州"),
                    @ApiImplicitParam(name = "district", value = "区县", dataType = "string", example = "天河"),
                    @ApiImplicitParam(name = "town", value = "镇", dataType = "string"),
                    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌；2:过期)"),
                    @ApiImplicitParam(name = "startTime", value = "创建时间（格式：2020-10-01）"),
                    @ApiImplicitParam(name = "endTime", value = "创建时间（格式：2020-10-01）"),
                    @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "string"),
                    @ApiImplicitParam(name = "management", value = "经营类别", dataType = "string"),
                    @ApiImplicitParam(name = "details", value = "类别明细", dataType = "string")
            }
    )
    public ApiResult<PageResultLocal<Applicants>> list(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                       @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                       @RequestParam(name = "city", required = false) String city,
                                                       @RequestParam(name = "district", required = false) String district,
                                                       @RequestParam(name = "town", required = false) String town,
                                                       @RequestParam(name = "status", required = false) String status,
                                                       @RequestParam(name = "startTime", required = false) String startTime,
                                                       @RequestParam(name = "endTime", required = false) String endTime,
                                                       @RequestParam(name = "search", required = false) String search,
                                                       @RequestParam(name = "management", required = false) String management,
                                                       @RequestParam(name = "details", required = false) String details) {

        try {
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
            Integer roleId = user.getRoleId();
            IPage<Applicants> page = applicantsMapper.applicantsList(
                    new Page<>(current, size),
                    1, city, district, town, status, startTime, endTime, roleId, user.getCity(), user.getDistrict(), search, management, details);
            return ApiResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @RequiresPermissions("fxxfcn:list")
    @PostMapping("/list/applicants/remove/{id}")
    @ApiOperation(value = "经营者列表-根据 id 删除承诺单位", notes = "经营者列表-根据 id 删除承诺单位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "承诺单位id", required = true),
    })
    @OperatorLogAnno(operType = "删除", operModul = "放心消费承诺单位", operDesc = "经营者列表-根据 id 删除承诺单位")
    public ApiResult delApplicantsById(@PathVariable(value = "id") Integer id) {
        try {
            applicantsService.removeById(id);
            return ApiResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @RequiresPermissions("fxxfcn:list")
    @PostMapping("/list/applicants/remove")
    @ApiOperation(value = "经营者列表-根据 ids 删除承诺单位", notes = "经营者列表-根据 ids 删除承诺单位")
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "ids", value = "承诺单位ids", required = true, dataTypeClass = List.class)
    }))
    @OperatorLogAnno(operType = "删除", operModul = "放心消费承诺单位", operDesc = "经营者列表-根据 ids 删除承诺单位")
    public ApiResult delApplicantsById(@RequestBody Map map) {
        try {
            List<String> ids = (List<String>) map.get("ids");
            if (ids.size() > 0) {
                applicantsService.removeByIds(ids);
                return ApiResult.success();
            }
            return ApiResult.fail("ids为空", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }


    /**
     * @param id 承诺单位id
     * @return
     * @throws
     * @description 根据 id 查询承诺单位
     * @author laijunbao
     * @updateTime 2020-01-09-0009 16:06
     */
    @RequiresPermissions("fxxfcn:list")
    @GetMapping("/list/{id}")
    @ApiOperation(value = "经营者列表-根据 id 查询承诺单位", notes = "经营者列表-根据 id 查询承诺单位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "承诺单位id", required = true),
    })
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "经营者列表-根据 id 查询承诺单位")
    public ApiResult<ApplicantsUnitParamsVo> findApplicantsById(@PathVariable(value = "id") Integer id) {
        try {
            Applicants applicants = applicantsService.getById(id);
            ApplicantsUnitParamsVo applicantsUnitParamsVo = new ApplicantsUnitParamsVo();
            if (applicants != null) {
                BeanUtils.copyProperties(applicants, applicantsUnitParamsVo);
                applicantsUnitParamsVo.setManagement(applicants.getManagement());
                if (applicants.getDetails() != null && StringUtils.isNotBlank(applicants.getDetails())) {
                    applicantsUnitParamsVo.setDetails(Arrays.asList(applicants.getDetails().split(",")));
                }

                List<AuditLogVo> auditLogs = auditLogMapper.getAuditLog(id, null);

                applicantsUnitParamsVo.setAuditLogs(auditLogs);

                return ApiResult.success(applicantsUnitParamsVo);
            } else {
                return ApiResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @RequiresPermissions("fxxfcn:list")
    @GetMapping("/find")
    @ApiOperation(value = "经营者列表-根据id和经营者注册名称查询是否重复", notes = "经营者列表-根据id和经营者注册名称查询是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true),
            @ApiImplicitParam(name = "creditCode", value = "统一社会信用代码", required = true),
    })
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "经营者列表-根据id和经营者注册名称查询是否重复")
    public ApiResult<ApplicantsFindVo> findApplicantsByRegName(@RequestParam(value = "id") Integer id,
                                                               @RequestParam(value = "creditCode") String creditCode) {
        try {
            List<Applicants> applicants = myApplicantsUnitService.findApplicantsByIdRegName(id, creditCode);

            if (applicants.size() > 0) {
                return ApiResult.success(new ApplicantsFindVo(true));
            }
            return ApiResult.success(new ApplicantsFindVo(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * @param id 承诺单位id
     * @return
     * @throws
     * @description 根据 id 更新承诺单位
     * @author laijunbao
     * @updateTime 2020-01-09-0009 16:06
     */
    @RequiresPermissions("fxxfcn:list")
    @PostMapping("/list/{id}")
    @ApiOperation(value = "经营者列表-编辑保存", notes = "经营者列表-根据 id 更新承诺单位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "承诺单位id", required = true),
    })
    @DynamicParameters(
            name = "applicants",
            properties = {
                    @DynamicParameter(name = "applicants", value = "applicants", dataTypeClass = ApplicantsUnitParamsVo.class, required = true)
            }
    )
    @OperatorLogAnno(operType = "更新", operModul = "放心消费承诺单位", operDesc = "经营者列表-根据 id 更新承诺单位")
    public ApiResult updateApplicants(@PathVariable(value = "id") Integer id, @RequestBody ApplicantsUnitParamsVo2 applicants) {
        try {
            Applicants applicantsById = applicantsService.getById(id);
            if (applicantsById != null) {
                BeanUtils.copyProperties(applicants, applicantsById, "id,status");
                if (StringUtils.isNotBlank(applicants.getStartTime()) && applicants.getStartTime().split("-").length == 2) {
                    applicants.setStartTime(applicants.getStartTime() + "-01");
                    applicantsById.setStartTime(LocalDate.parse(applicants.getStartTime()));
                } else {
                    if (StringUtils.isNotBlank(applicants.getStartTime())) {
                        applicantsById.setStartTime(LocalDate.parse(applicants.getStartTime()));
                    }
                }
                if (StringUtils.isNotBlank(applicants.getEndTime()) && applicants.getEndTime().split("-").length == 2) {
                    applicants.setEndTime(applicants.getEndTime() + "-01");
                    applicantsById.setEndTime(LocalDate.parse(applicants.getEndTime()));
                } else {
                    if (StringUtils.isNotBlank(applicants.getEndTime())) {
                        applicantsById.setEndTime(LocalDate.parse(applicants.getEndTime()));
                    }
                }
                applicantsById.setManagement(applicants.getManagement());
                if (applicants.getDetails() != null && applicants.getDetails().size() > 0) {
                    applicantsById.setDetails(String.join(",", applicants.getDetails()));
                }

                User user = (User) SecurityUtils.getSubject().getPrincipal();

                // 不通过的数据，更改为待审核作态
                if (Objects.equals(applicantsById.getStatus(), 7)) {
                    applicantsById.setStatus(4);
                    applicantsById.setAuditRoleId(user.getRoleId() + 1);
                }
                /*
                // 后台用户导入的、不通过的数据，更改为待审核作态
                if (Objects.equals(user.getUsertype(), 1) && Objects.equals(applicantsById.getStatus(), 7)) {
                    applicantsById.setStatus(4);
                    applicantsById.setAuditRoleId(user.getRoleId() + 1);
                }*/

                //多个地址解析
                if (StringUtils.isNotBlank(applicants.getAddrs())) {
                    JSONArray jsonArray = JSON.parseArray(applicants.getAddrs());

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

                            return ApiResult.fail("地址不全,请补全");
                        }
                        if ((size - 1) != i) {
                            citys += ",";
                            districts += ",";
                            addressStr += ",";
                        }
                    }
                    applicantsById.setCity(citys);
                    applicantsById.setDistrict(districts);
                    applicantsById.setAddress(addressStr);
                }

                applicantsService.updateById(applicantsById);
                return ApiResult.success();
            } else {
                return ApiResult.fail("承诺单位不存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * @param id  承诺单位id
     * @param map 摘牌提交信息
     * @return
     * @throws
     * @description 根据承诺单位 id 摘牌
     * @author laijunbao
     * @updateTime 2020-01-09-0009 16:06
     */
    @RequiresPermissions("fxxfcn:list")
    @PostMapping("/list/del/{id}")
    @ApiOperation(value = "经营者列表-摘牌提交", notes = "经营者列表-根据承诺单位 id 摘牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "承诺单位id", required = true)
    })
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "delReason", value = "摘牌具体原因", required = true),
            @DynamicParameter(name = "delOther", value = "摘牌其它必要信息", required = true)
    }))
    @OperatorLogAnno(operType = "更新", operModul = "放心消费承诺单位", operDesc = "经营者列表-根据承诺单位 id 摘牌")
    public ApiResult delApplicants(@PathVariable(value = "id") Integer id, @RequestBody Map<String, String> map) {
        try {
            Applicants applicants = applicantsService.getById(id);

            if (applicants != null) {
                applicants.setDelReason(map.get("delReason"));
                applicants.setDelOther(map.get("delOther"));
                applicants.setDelTime(LocalDateTime.now());
                applicants.setUpdateTime(LocalDateTime.now());
                applicants.setStatus(0);

                applicants.updateById();

                return ApiResult.success();
            } else {
                return ApiResult.fail("承诺单位不存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @RequiresPermissions("fxxfcn:list")
    @GetMapping(value = "/downTemplateFile")
    @ApiOperation(value = "经营者列表-模板下载", notes = "经营者列表-模板下载")
    @OperatorLogAnno(operType = "下载", operModul = "放心消费承诺单位", operDesc = "经营者列表-模板下载")
    public void downTemplateFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            TemplateExportParams params = new TemplateExportParams(ResourceUtils.getFile(unitTemplateFilePath).getPath());
            // 输出全部的sheet
            params.setScanAllsheet(true);
            Workbook workbook = ExcelExportUtil.exportExcel(params, new HashMap<>());
            String fileName = "放心消费承诺单位导入模板（备注：请启用宏）.xlsm";
            ExcelUtil.downLoadExcel(fileName, request, response, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @RequiresPermissions("fxxfcn:upload")
    @PostMapping(value = "/preImport")
    @ApiOperation(value = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项", notes = "经营者列表-检查导入的在期单位名称中是否存在与现有在期名单相同项")
    @ApiImplicitParam(name = "file", value = "导入文件", required = true, dataType = "__File")
    @OperatorLogAnno(operType = "导入", operModul = "放心消费承诺单位", operDesc = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项")
    public ApiResult<List<ExcelImportErrorMsgVo>> templatePreImport(@RequestParam("file") MultipartFile file) {

        if (!Objects.isNull(file) && !file.getOriginalFilename().endsWith(".xlsm")) {
            return ApiResult.fail("上传失败，请上传xlsm格式文件");
        }

        InputStream in = null;
        List<ExcelImportErrorMsgVo> errorMsgVoList = new ArrayList<>();
        try {
            in = file.getInputStream();

            ImportParams importParams = new ImportParams();
            importParams.setStartRows(1);
            importParams.setNeedVerify(true);
            importParams.setVerifyHandler(new ApplicantsUnitExcelVerifyHandlerImpl());
            importParams.setImportFields(title);

            ExcelImportResult<ApplicantsUnitExcelImportVo> result = ExcelImportUtil.importExcelMore(
                    in, ApplicantsUnitExcelImportVo.class, importParams);

            // 是否存在校验失败
            boolean verfiyFail = result.isVerifyFail();
            List<ApplicantsUnitExcelImportVo> failList = result.getFailList();
            List<ApplicantsUnitExcelImportVo> applicantsUnitExcelVos = result.getList();

            if (verfiyFail) {
                failList.stream().forEach(e -> {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo(e.getRowNum(), e.getErrorMsg()));
                });
                return ApiResult.fail(errorMsgVoList);
            }
            if (applicantsUnitExcelVos.size() > 0) {

                //检测区县用户导入数据是否跨区县
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                int roleId = user.getRoleId();

                if (roleId == 3) {

                    String district = user.getDistrict();
                    for (int i = 0; i < applicantsUnitExcelVos.size(); i++) {
                        String vo = applicantsUnitExcelVos.get(i).getDistrict();
                        if (!vo.contains(district)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他地区数据，请去掉后再上传"));
                            return ApiResult.fail(errorMsgVoList);
                        }
                    }
                } else if (roleId == 2) {

                    String city = user.getCity();
                    for (int i = 0; i < applicantsUnitExcelVos.size(); i++) {
                        String vo = applicantsUnitExcelVos.get(i).getCity();
                        if (!vo.contains(city)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他市区数据，请去掉后再上传"));
                            return ApiResult.fail(errorMsgVoList);
                        }
                    }

                } else if (roleId == 4) {
                    String city = user.getCity();
                    for (int i = 0; i < applicantsUnitExcelVos.size(); i++) {
                        String vo = applicantsUnitExcelVos.get(i).getCity();
                        if (!vo.contains(city)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他市区数据，请去掉后再上传"));
                            return ApiResult.fail(errorMsgVoList);
                        }
                    }
                }


                List<String> creditCodes = new ArrayList<>();

                for (int i = 0; i < applicantsUnitExcelVos.size(); i++) {
                    creditCodes.add(applicantsUnitExcelVos.get(i).getCreditCode());
                }
                String[] array = new String[creditCodes.size()];
                array = creditCodes.toArray(array);
                List<ArrayList<String>> same = ApplicantsImportUtil.findSame(array);
                if (same.size() > 0) {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】存在重复，行:" + same));
                    return ApiResult.fail(errorMsgVoList);
                }

                List<Applicants> applicantsStatus5_6 = myApplicantsUnitService.findApplicantsByCreditCodeAndStatus5_6(creditCodes.toArray(), 1);
                AtomicInteger rowNum0 = new AtomicInteger(3);
                AtomicBoolean flag0 = new AtomicBoolean(false);
                boolean isAduit = applicantsUnitExcelVos.stream().sequential().anyMatch(app -> {
                    for (int i = 0; i < applicantsStatus5_6.size(); i++) {
                        if (Objects.equals(app.getCreditCode(), applicantsStatus5_6.get(i).getCreditCode())) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo(rowNum0.get(),
                                    "导入的统一社会信用代码中存在与现有在审核名单相同项"));
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
                    return ApiResult.fail(errorMsgVoList);
                }


                List<Applicants> applicantsStatus1 = myApplicantsUnitService.findApplicantsByRegName(1, creditCodes.toArray());
                AtomicInteger rowNum1 = new AtomicInteger(3);
                AtomicBoolean flag1 = new AtomicBoolean(false);
                boolean isRepact1 = applicantsUnitExcelVos.stream().sequential().anyMatch(app -> {
                    for (int i = 0; i < applicantsStatus1.size(); i++) {
                        if (Objects.equals(app.getCreditCode(), applicantsStatus1.get(i).getCreditCode())) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo(rowNum1.get(),
                                    "导入的统一社会信用代码中存在与现有在期名单相同项"));
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
                    return ApiResult.fail(errorMsgVoList);
                }

                List<Applicants> applicantsStatus4 = myApplicantsUnitService.findApplicantsByRegNameAndStatus4(1, creditCodes.toArray());

                boolean isRepact = false;
                AtomicInteger rowNum = new AtomicInteger(3);
                AtomicBoolean flag = new AtomicBoolean(false);
                if (applicantsStatus4 != null && applicantsStatus4.size() > 0) {
                    isRepact = applicantsUnitExcelVos.stream().sequential().anyMatch(app -> {
                        for (int i = 0; i < applicantsStatus4.size(); i++) {
                            if (Objects.equals(app.getCreditCode(), applicantsStatus4.get(i).getCreditCode())) {
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

                String path = ResourceUtils.getURL("classpath:").getPath() + importFileTmp;
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
                return ApiResult.success(errorMsgVoList);
            }
            errorMsgVoList.add(new ExcelImportErrorMsgVo(null, "导入文件为空，请重新选择"));
            return ApiResult.fail(errorMsgVoList);
        } catch (ExcelImportException e) {
            e.printStackTrace();
            if (e.getMessage().contains("不是合法的Excel模板")) {
                errorMsgVoList.add(new ExcelImportErrorMsgVo(1, "不是合法的模板"));
                return ApiResult.fail(errorMsgVoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ApiResult.fail();
    }

    @RequiresPermissions("fxxfcn:upload")
    @PostMapping(value = "/import")
    @ApiOperation(value = "经营者列表-导入", notes = "经营者列表-导入")
//    @ApiImplicitParam(name = "map", value = "参数map", required = true)
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "fileId", value = "文件id", required = true)
    }))
    @OperatorLogAnno(operType = "导入", operModul = "放心消费承诺单位", operDesc = "经营者列表-导入")
    public ApiResult<List<Applicants>> templateImport(@RequestBody Map map) {

        File file = null;
        try {
            String path = ResourceUtils.getURL("classpath:").getPath() + importFileTmp;
            file = ResourceUtils.getFile(path + map.get("fileId"));

            ImportParams importParams = new ImportParams();
            importParams.setStartRows(1);
            // importParams.setNeedVerify(true);
            // importParams.setVerifyHandler(new ApplicantsUnitExcelVerifyHandlerImpl());

            ExcelImportResult<ApplicantsUnitExcelImportVo> result = ExcelImportUtil.importExcelMore(
                    file, ApplicantsUnitExcelImportVo.class, importParams);

            // 是否存在校验失败
            boolean verfiyFail = result.isVerifyFail();
            List<ApplicantsUnitExcelImportVo> failList = result.getFailList();
            List<ApplicantsUnitExcelImportVo> applicantsUnitExcelVos = result.getList();

            if (verfiyFail) {
                List<ExcelImportErrorMsgVo> errorMsgVoList = new ArrayList<>();
                failList.stream().forEach(e -> {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo(e.getRowNum(), e.getErrorMsg()));
                });
                return ApiResult.fail(errorMsgVoList);
            }

            if (file != null) {
                file.delete();
            }
            if (applicantsUnitExcelVos.size() > 0) {

//                //检测数据是否跨区县
//                User user = (User) SecurityUtils.getSubject().getPrincipal();
//                int roleId = user.getRoleId();
//
//                if (roleId==3){
//
//                    String district = user.getDistrict();
//                    for (int i = 0; i < applicantsUnitExcelVos.size(); i++) {
//                        String vo=applicantsUnitExcelVos.get(i).getDistrict();
//                        if (!vo.contains(district)){
//                            return ApiResult.fail("地市用户,暂不支持导入");
//                        }
//                    }
//                }else if (roleId ==2){
//                    return ApiResult.fail("地市用户,暂不支持导入");
//                }

                List<Applicants> applicantsList = myApplicantsUnitService.templateImport(applicantsUnitExcelVos);
                return ApiResult.success(applicantsList);
            }
            return ApiResult.fail("导入文件为空，请重新选择");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return ApiResult.fail("导入失败");
    }

    @RequiresPermissions("fxxfcn:list")
    @GetMapping(value = "/export")
    @ApiOperation(value = "经营者列表-导出", notes = "经营者列表-导出")
    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌；2:过期)，为空则导出全部", example = "1")
    @OperatorLogAnno(operType = "导出", operModul = "放心消费承诺单位", operDesc = "经营者列表-导出")
    public void export(@RequestParam(value = "status", required = false) String status, HttpServletRequest request, HttpServletResponse response) {

        // 获取登录用户
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
        Integer roleId = user.getRoleId();
        String city = user.getCity();

        List<Applicants> applicantsList = applicantsMapper.applicantsExport(1, status, roleId, user.getCity(), user.getDistrict());

        /*List<Applicants> applicantsList = applicantsService.list(
                new QueryWrapper<Applicants>()
                        .eq("type", 1)
                        .eq(StringUtils.isNotBlank(status), "status", status)
                        .eq(roleId != 1, "city", user.getCity()));*/

        List<ApplicantsUnitExcelVo> applicantsUnitVos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM");
        applicantsList.stream().forEach(app -> {
            ApplicantsUnitExcelVo applicantsUnitVo = new ApplicantsUnitExcelVo();
            BeanUtils.copyProperties(app, applicantsUnitVo);
            if (app.getApplicationDate() != null) {
                applicantsUnitVo.setApplicationDate(formatter.format(app.getApplicationDate()));
            }
            if (app.getIndustryDate() != null) {
                applicantsUnitVo.setIndustryDate(formatter.format(app.getIndustryDate()));
            }
            if (app.getCcDate() != null) {
                applicantsUnitVo.setCcDate(formatter.format(app.getCcDate()));
            }
            if (app.getDelTime() != null) {
                applicantsUnitVo.setDelTime(formatter.format(app.getDelTime()));
            }
            if (app.getStartTime() != null) {
                applicantsUnitVo.setStartTime(formatterDate.format(app.getStartTime()));
            }
            applicantsUnitVo.setDelReason(app.getDelReason());
            applicantsUnitVo.setDelOther(app.getDelOther());
            applicantsUnitVos.add(applicantsUnitVo);
        });
        String fileName = "放心消费承诺单位.xlsx";
        if (roleId == 2 && !Objects.isNull(city)) {
            fileName = city + " 放心消费承诺-经营者统计.xlsx";
        } else if (roleId == 3 && !Objects.isNull(city) && !Objects.isNull(user.getDistrict())) {
            fileName = city + "_" + user.getDistrict() + " 放心消费承诺-经营者统计.xlsx";
        }
        ExcelUtil.exportExcel(applicantsUnitVos, "", "", ApplicantsUnitExcelVo.class, fileName, request, response);
    }

    /**
     * @param current 当前页
     * @param size    每页条数
     * @param search  搜索条件
     * @return ApiResult
     * @throws
     * @description 监督投诉列表
     * @author laijunbao
     * @updateTime 2020-01-09-0009 14:07
     */
    @RequiresPermissions("fxxfcn:jdts")
    @GetMapping("/complaint")
    @ApiOperation(value = "监督投诉-列表", notes = "监督投诉-列表")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "string")
            }
    )
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "监督投诉-列表")
    public ApiResult<PageResultLocal<FeedbackComplaintVo>> complaint(@RequestParam(name = "current", defaultValue = "1") Integer current,
                                                                     @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                     @RequestParam(name = "search", required = false) String search) {

        try {
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
            Integer roleId = user.getRoleId();

            Map<String, Object> map = new HashMap<>();
            map.put("roleId", roleId);
            map.put("city", user.getCity());
            map.put("district", user.getDistrict());
            map.put("type", 1);
            map.put("search", search);
            IPage<FeedbackComplaintVo> feedbackIPage = feedbackMapper.feedbackList(new Page<>(current, size), map);
            return ApiResult.success(feedbackIPage);
        } catch (Exception e) {
            log.error("获取放心消费承诺单位表格数据失败，{}", e);
            return ApiResult.fail();
        }
    }

    /**
     * @param applicantsId
     * @return ApiResult
     * @throws
     * @description 监督投诉-企业详情
     * @author laijunbao
     * @updateTime 2020-01-09-0009 14:07
     */
    @RequiresPermissions("fxxfcn:jdts")
    @GetMapping("/complaint/companyDetails")
    @ApiOperation(value = "监督投诉-企业详情", notes = "监督投诉-企业详情")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "applicantsId", value = "applicantsId", required = true),
                    @ApiImplicitParam(name = "startTime", value = "开始时间"),
                    @ApiImplicitParam(name = "endTime", value = "结束时间"),
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
            }
    )
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "监督投诉-企业详情")
    public ApiResult<PageResultLocal<FeedbackCompanyDetailsVo>> feedbackCompanyDetails(@RequestParam(value = "applicantsId") String applicantsId,
                                                                                       @RequestParam(name = "startTime", required = false) String startTime,
                                                                                       @RequestParam(name = "endTime", required = false) String endTime,
                                                                                       @RequestParam(name = "current", defaultValue = "1") Integer current,
                                                                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {

        try {

            IPage<Feedback> feedbacks = feedbackService.page(
                    new Page<>(current, size),
                    new QueryWrapper<Feedback>()
                            .eq("applicants_id", applicantsId)
                            .ge(StringUtils.isNotBlank(startTime), "create_time", startTime)
                            .le(StringUtils.isNotBlank(endTime), "create_time", endTime)
                            .orderByDesc("is_new")
                            .orderByDesc("create_time"));

            List<FeedbackCompanyDetailsVo> feedbackCompanyDetails = new ArrayList<>();
            feedbacks.getRecords().stream().forEach(feedback -> {
                FeedbackCompanyDetailsVo feedbackCompanyDetailsVo = new FeedbackCompanyDetailsVo();
                BeanUtils.copyProperties(feedback, feedbackCompanyDetailsVo);
                feedbackCompanyDetails.add(feedbackCompanyDetailsVo);
                feedbackCompanyDetailsVo = null;
            });
            IPage<FeedbackCompanyDetailsVo> iPage = new Page();
            BeanUtils.copyProperties(feedbacks, iPage);
            iPage.setRecords(feedbackCompanyDetails);

            return ApiResult.success(iPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }

    }


    /**
     * @param id
     * @return ApiResult
     * @throws
     * @description 根据 id 查询监督投诉
     * @author laijunbao
     * @updateTime 2020-01-09-0009 14:07
     */
    @RequiresPermissions("fxxfcn:jdts")
    @GetMapping("/complaint/company/{id}")
    @ApiOperation(value = "监督投诉-企业详情处理", notes = "监督投诉-企业详情处理")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", value = "id", required = true),
            }
    )
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "监督投诉-企业详情处理")
    public ApiResult<FeedbackVo> getFeedbackById(@PathVariable(value = "id") String id) {

        try {
            // 根据 id 查询留言反馈
            Feedback feedback = feedbackService.getById(id);
            if (feedback == null) {
                return ApiResult.fail("留言不存在");
            }

            if (feedback.getIsNew() == 1) {
                feedback.setIsNew(0);
                feedbackService.updateById(feedback);
            }

            // 根据 applicants_id 查询归属地市
            Applicants applicants = applicantsService.getById(feedback.getApplicantsId());

            // 设置留言反馈归属地市
            if (applicants != null) {
                feedback.setCity(applicants.getCity());
            }

            // 根据 feedback_id 查询留言反馈操作记录
            List<Record> records = recordService.list(new QueryWrapper<Record>().eq("feedback_id", id).orderByDesc("create_time"));

            // 留言反馈操作记录设置操作用户
            records.stream().forEach(record -> {
                User user = userService.getById(record.getUserid());
                record.setUserName(user.getAccount());
            });
            feedback.setType("1".equals(feedback.getType()) ? "放心消费承诺单位" : "线下无理由退货承诺店");
            // 返回有操作记录的留言反馈对象
            FeedbackVo feedbackVo = new FeedbackVo(feedback, records, applicants.getStatus());

            return ApiResult.success(feedbackVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    /**
     * @param id 留言反馈 id
     * @return ApiResult
     * @throws
     * @description 处理留言反馈并保存操作记录, 监督投诉-处理投诉
     * @author laijunbao
     * @updateTime 2020-01-09-0009 14:07
     */
    @RequiresPermissions("fxxfcn:jdts:process")
    @PostMapping("/complaint/company/{id}")
    @ApiOperation(value = "监督投诉-处理投诉", notes = "监督投诉-处理投诉")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", value = "id", required = true)
            }
    )
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "result", value = "处理结果", required = true),
            @DynamicParameter(name = "processingSituation", value = "调查处理情况", required = true)
    }))
    @OperatorLogAnno(operType = "更新", operModul = "放心消费承诺单位", operDesc = "监督投诉-处理投诉")
    public ApiResult updateFeedback(@PathVariable(value = "id") String id,
                                    @RequestBody Map map) {

        try {
            String result = (String) map.get("result");
            String processingSituation = (String) map.get("processingSituation");
            if (StringUtils.isBlank(result)) {
                return ApiResult.fail("处理结果不能为空");
            }
            if (StringUtils.isBlank(processingSituation)) {
                return ApiResult.fail("调查处理情况不能为空");
            }
            // 根据留言反馈id处理留言反馈并保存操作记录
            myFeedbackService.updateFeedback(Integer.parseInt(id), result, processingSituation);
            return ApiResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    @RequiresPermissions("fxxfcn:jdtstj")
    @PostMapping("/operatorStatistics/list")
    @ApiOperation(value = "经营者统计-列表", notes = "经营者统计-列表")
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "current", value = "当前页", example = "1"),
            @DynamicParameter(name = "size", value = "每页条数", example = "10"),
            @DynamicParameter(name = "startTime", value = "开始时间", required = false),
            @DynamicParameter(name = "endTime", value = "结束时间", required = false),
    }))
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "经营者统计-列表")
    public ApiResult<PageResultLocal<OperatorStatisticsVo>> operatorStatistics(@RequestBody Map map) {
        try {
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
            Integer roleId = user.getRoleId();
            String city = user.getCity();

            List<OperatorStatisticsVo> operatorStatistics =
                    applicantsMapper.unitOperatorStatistics(
                            map.get("startTime").toString(),
                            map.get("endTime").toString(), roleId, city, user.getDistrict());

            return ApiResult.success(operatorStatistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    @RequiresPermissions("fxxfcn:jdtstj")
    @GetMapping("/operatorStatistics/export")
    @ApiOperation(value = "经营者统计-导出", notes = "经营者统计-导出")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
                    @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
            }
    )
    @OperatorLogAnno(operType = "导出", operModul = "放心消费承诺单位", operDesc = "经营者统计-列表导出")
    public void operatorStatisticsExport(@RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        try {
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
            Integer roleId = user.getRoleId();
            String city = user.getCity();

            List<OperatorStatisticsVo> operatorStatistics = applicantsMapper.unitOperatorStatistics(startTime, endTime, roleId, city, user.getDistrict());

            String fileName = "放心消费承诺-经营者统计.xlsx";
            if (roleId == 2 && !Objects.isNull(city)) {
                fileName = city + " 放心消费承诺-经营者统计.xlsx";
            } else if (roleId == 3 && !Objects.isNull(city) && !Objects.isNull(user.getDistrict())) {
                fileName = city + "_" + user.getDistrict() + " 放心消费承诺-经营者统计.xlsx";
            }
            ExcelUtil.exportExcel(operatorStatistics, "", "", OperatorStatisticsVo.class, fileName, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresPermissions("fxxfcn:jdtstj")
    @GetMapping("/statListByUserRole")
    @ApiOperation(value = "监督投诉统计", notes = "监督投诉统计/监督投诉统计报表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "开始日期"),
            @ApiImplicitParam(name = "endTime", value = "结束日期"),
            //@ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
            //@ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
            @ApiImplicitParam(name = "type", value = "type：1(放心消费承诺单位)", dataType = "string", required = true)
    })
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位", operDesc = "查询监督投诉统计")
    public ApiResult<Page<List<FeedbackStat>>> statListByUserRole(FeedbackStat feedback) {
        //默认查询"放心消费承诺店"
        //feedback.setType("放心消费承诺店");
        Page p = new Page<>();
        Page page = statService.statListByUserRole(feedback, p);
        return ApiResult.success(page);
    }

    @RequiresPermissions("fxxfcn:jdtstj")
    @GetMapping(value = "/exportByUserRole")
    @ApiOperation(value = "监督投诉统计-导出Excel", notes = "监督投诉统计/监督投诉统计导出Excel")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "string"),
                    @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "string"),
                    @ApiImplicitParam(name = "type", value = "type:1(放心消费承诺店)", dataType = "string", required = true)
            }
    )
    @OperatorLogAnno(operType = "导出", operModul = "放心消费承诺单位", operDesc = "监督投诉统计导出")
    public void exportByUserRole(FeedbackStat feedback, HttpServletResponse response) {
        //feedback.setType("线下无理由退货承诺店");
        List<FeedbackStat> records = statService.exportStatListByUserRole(feedback);

        //根据角色动态更换Excel表头
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Integer roleId = user.getRoleId();

        //测试环境设定参数
        /*Integer roleId = 1;*/

        //Excel表头
        Map<String, String> titleMap = Maps.newLinkedHashMap();
        if (roleId == 1) {
            titleMap.put("city", "地市");
        } else {
            titleMap.put("city", "区县");
        }
        titleMap.put("companyTotal", "承诺单位数量");
        titleMap.put("complaintCompanyNum", "被反馈单位数量");
        titleMap.put("takeOff", "摘牌数量");
        titleMap.put("complaintTotal", "监督投诉的总条数");
        titleMap.put("unprocessed", "待处理");
        titleMap.put("warning", "督促告诫");
        titleMap.put("disqualification", "摘牌");
        titleMap.put("nonExistentComplaints", "投诉问题不存在");
        titleMap.put("other", "其他");

        File f = new File("监督投诉统计.xls");
        OutputStream out;
        try {
            out = new FileOutputStream(f);
            net.mingsoft.utils.ExcelExportUtil.exportExcel(titleMap, records, out, "yyyy-MM-dd HH:mm:ss", response);
            out.close();
        } catch (IOException e) {
            log.error("导出监督投诉统计报表发生异常:", e);
        }
    }


    @GetMapping(value = "/audit")
    @ApiOperation(value = "放心消费承诺单位审核", notes = "放心消费承诺单位审核。当前登录用户的角色id小于auditRoleId，并且当前状态在4（待审核）、 5（县级审核通过）、6（市级审核通过）之一，说明可以审核")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "type", value = "1：审核通过，2：审核不通过", example = "1", dataType = "int", required = true),
                    @ApiImplicitParam(name = "id", value = "承诺单位id", dataType = "int", required = true),
                    @ApiImplicitParam(name = "notes", value = "备注信息"),
            }
    )
    @OperatorLogAnno(operType = "更新", operModul = "放心消费承诺单位", operDesc = "放心消费承诺单位审核")
    public ApiResult audit(@RequestParam(value = "type") Integer type, @RequestParam(value = "id") Integer id, @RequestParam(value = "notes", required = false) String notes) {
        try {
            if (type == 2 && StringUtils.isBlank(notes)) {
                return ApiResult.fail("不通过原因不能为空");
            }

            String flagStr = myApplicantsUnitService.updateApplicantsStatusByAudit(type, id, notes);

            if (flagStr == "success") {
                return ApiResult.success();
            }
            return ApiResult.fail(flagStr);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    @PostMapping("/apply/input")
    @ApiOperation(value = "放心消费承诺新录入", notes = "申请信息-放心消费承诺新录入")
    @DynamicParameters(
            name = "enterpriseUnitNewApplyVo",
            properties = {
                    @DynamicParameter(name = "enterpriseUnitNewApplyVo", value = "enterpriseUnitNewApplyVo", dataTypeClass = EnterpriseUnitNewApplyVo.class, required = true)
            }
    )
    @OperatorLogAnno(operType = "新增", operModul = "放心消费承诺单位", operDesc = "放心消费承诺新录入")
    public ApiResult<EnterpriseUnitNewApplyVo> saveEnterpriseUnitApplyInfo(@RequestBody EnterpriseUnitNewApplyVo enterpriseUnitNewApplyVo) {
        try {

            List<Applicants> applicantsByCreditCode = myApplicantsUnitService.findApplicantsByCreditCode(1, enterpriseUnitNewApplyVo.getCreditCode());

            if (applicantsByCreditCode != null && applicantsByCreditCode.size() > 0) {
                return ApiResult.fail("存在相同统一社会信用代码");
            }

            Applicants applicants = newUnitApplicants(enterpriseUnitNewApplyVo);
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            Integer rode = user.getRoleId();
            applicants.setCreater(user.getId());

            if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
                applicants.setAuditRoleId(4);
            } else if (StringUtils.isNotBlank(applicants.getCity())) {
                applicants.setAuditRoleId(3);
            }

            if (enterpriseUnitNewApplyVo.getDetails() != null && enterpriseUnitNewApplyVo.getDetails().size() > 0) {
                applicants.setDetails(String.join(",", enterpriseUnitNewApplyVo.getDetails()));
            }

            //判断录入类型
            if (rode == 1) {
                applicants.setCreateType("省级录入");
            } else if (rode == 2) {
                applicants.setCreateType("市级录入");
            } else if (rode == 3) {
                applicants.setCreateType("区县录入");
            } else if (rode == 4) {
                applicants.setCreateType("行业协会录入");
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

                        return ApiResult.fail("地址不全,请补全");
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

            return ApiResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }


}

