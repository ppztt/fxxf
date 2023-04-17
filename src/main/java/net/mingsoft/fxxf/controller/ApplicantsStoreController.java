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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.fxxf.mapper.ApplicantsMapper;
import net.mingsoft.fxxf.mapper.AuditLogMapper;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.FeedbackTypeMapper;
import net.mingsoft.fxxf.service.*;
import net.mingsoft.fxxf.service.impl.EnterpriseService;
import net.mingsoft.fxxf.service.impl.MyApplicantsStoreService;
import net.mingsoft.fxxf.service.impl.MyApplicantsUnitService;
import net.mingsoft.fxxf.service.impl.MyFeedbackService;
import net.mingsoft.fxxf.bean.vo.*;
import net.mingsoft.utils.ApplicantsImportUtil;
import net.mingsoft.utils.ApplicantsStoreExcelVerifyHandlerImpl;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static net.mingsoft.fxxf.controller.EnterpriseController.newStoreApplicants;

/**
 * <p>
 * 申报单位 前端控制器
 * 无理由退货承诺实体店
 * </p>
 *
 * @author laijunbao
 * @since 2020-01-09
 */
@Api(tags = {"无理由退货承诺实体店"})
@RestController
@RequestMapping("/applicants/store")
@Slf4j
public class ApplicantsStoreController {


    private final String[] title = new String[]{"经营者注册名称", "统一社会信用代码", "门店名称", "经营场所-所在市",
            "经营场所-所在区县", "经营场所-详细地址", "经营类别", "类别明细", "负责人姓名", "负责人电话",
            "适用商品-承诺事项及内容", "退货期限（天）",
            "退货约定-承诺事项及内容", "企业申请日期"};

    /**
     * 无理由退货承诺实体店导入模板
     */
    @Value("${storeTemplateFilePath}")
    private String storeTemplateFilePath;

    /**
     * 导入模板临时文件
     */
    @Value("${importFileTmp}")
    private String importFileTmp;

    @Autowired
    private ApplicantsService applicantsService;

    @Autowired
    private MyApplicantsStoreService myApplicantsStoreService;

    @Autowired
    private MyApplicantsUnitService myApplicantsUnitService;

    @Autowired
    private UserService userService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private MyFeedbackService myFeedbackService;

    @Resource
    FeedbackTypeMapper feedbackTypeMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Resource
    private ApplicantsMapper applicantsMapper;

    @Resource
    FeedbackStatService statService;

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
     * @param status   状态(1:在期； 0:摘牌)
     * @param search   搜索条件
     * @return ApiResult
     * @throws
     * @description 获取无理由退货承诺实体店表格数据
     * @author laijunbao
     * @updateTime 2020-01-09-0009 14:07
     */
    @RequiresPermissions("wlythcn:list")
    @GetMapping("/list")
    @ApiOperation(value = "表格数据", notes = "无理由退货承诺实体店表格数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "current", value = "当前页", dataType = "int", example = "1", defaultValue = "1"),
                    @ApiImplicitParam(name = "size", value = "每页条数", dataType = "int", example = "10", defaultValue = "10"),
                    @ApiImplicitParam(name = "city", value = "地市", dataType = "string", example = "广州"),
                    @ApiImplicitParam(name = "district", value = "区县", dataType = "string", example = "天河"),
                    @ApiImplicitParam(name = "town", value = "镇", dataType = "string"),
                    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌)"),
                    @ApiImplicitParam(name = "startTime", value = "创建时间（格式：2020-10-01）"),
                    @ApiImplicitParam(name = "endTime", value = "创建时间（格式：2020-10-01）"),
                    @ApiImplicitParam(name = "search", value = "搜索条件", dataType = "string"),
                    @ApiImplicitParam(name = "management", value = "经营类别", dataType = "string"),
                    @ApiImplicitParam(name = "details", value = "类别明细", dataType = "string")
            }
    )
    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "查询经营者列表")
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
                    2, city, district, town, status, startTime, endTime, roleId, user.getCity(),
                    user.getDistrict(), search, management, details);

            return ApiResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(e.getMessage());
        }
    }

    @RequiresPermissions("wlythcn:list")
    @PostMapping("/list/applicants/remove/{id}")
    @ApiOperation(value = "经营者列表-根据 id 删除承诺单位", notes = "经营者列表-根据 id 删除承诺单位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "承诺单位id", required = true),
    })
    @OperatorLogAnno(operType = "删除", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 id 删除承诺单位")
    public ApiResult delApplicantsById(@PathVariable(value = "id") Integer id) {
        try {
            applicantsService.removeById(id);
            return ApiResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @RequiresPermissions("wlythcn:list")
    @PostMapping("/list/applicants/remove")
    @ApiOperation(value = "经营者列表-根据 ids 删除承诺单位", notes = "经营者列表-根据 ids 删除承诺单位")
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "ids", value = "承诺单位ids", required = true, dataTypeClass = List.class)
    }))
    @OperatorLogAnno(operType = "删除", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 ids 删除承诺单位")
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

    @RequiresPermissions("wlythcn:list")
    @GetMapping("/find")
    @ApiOperation(value = "经营者列表-根据id和经营者注册名称查询是否重复", notes = "经营者列表-根据id和经营者注册名称查询是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true),
            @ApiImplicitParam(name = "creditCode", value = "统一社会信用代码", required = true),
    })
    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 id 查询承诺单位")
    public ApiResult<ApplicantsFindVo> findApplicantsByRegName(@RequestParam(value = "id") Integer id,
                                                               @RequestParam(value = "creditCode") String creditCode) {
        try {
            List<Applicants> applicants = myApplicantsStoreService.findApplicantsByIdRegName(id, creditCode);

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
     * @description 根据 id 查询承诺单位
     * @author laijunbao
     * @updateTime 2020-01-09-0009 16:06
     */
    @RequiresPermissions("wlythcn:list")
    @GetMapping("/list/{id}")
    @ApiOperation(value = "经营者列表-根据 id 查询无理由退货承诺实体店", notes = "经营者列表-根据 id 查询无理由退货承诺实体店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "实体店id", required = true),
    })
    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "经营者列表-根据id和经营者注册名称查询是否重复")
    public ApiResult<ApplicantsStoreParamsVo> findApplicantsById(@PathVariable(value = "id") Integer id) {
        try {
            Applicants applicants = applicantsService.getById(id);
            ApplicantsStoreParamsVo applicantsStoreParamsVo = new ApplicantsStoreParamsVo();
            if (applicants != null) {
                BeanUtils.copyProperties(applicants, applicantsStoreParamsVo);
                applicantsStoreParamsVo.setManagement(applicants.getManagement());
                if (applicants.getDetails() != null && StringUtils.isNotBlank(applicants.getDetails())) {
                    applicantsStoreParamsVo.setDetails(Arrays.asList(applicants.getDetails().split(",")));
                }

                List<AuditLogVo> auditLogs = auditLogMapper.getAuditLog(id, null);

                applicantsStoreParamsVo.setAuditLogs(auditLogs);
                return ApiResult.success(applicantsStoreParamsVo);
            } else {
                return ApiResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    /**
     * @param id 实体店id
     * @return
     * @throws
     * @description 根据 id 更新实体店
     * @author laijunbao
     * @updateTime 2020-01-09-0009 16:06
     */
    @RequiresPermissions("wlythcn:list")
    @PostMapping("/list/{id}")
    @ApiOperation(value = "经营者列表-编辑保存", notes = "经营者列表-根据 id 更新无理由退货承诺实体店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "实体店id", required = true),
    })
    @DynamicParameters(
            name = "applicants",
            properties = {
                    @DynamicParameter(name = "applicants", value = "applicants", dataTypeClass = ApplicantsStoreParamsVo.class, required = true),
            }
    )
    @OperatorLogAnno(operType = "更新", operModul = "无理由退货承诺", operDesc = "经营者列表-根据 id 更新承诺单位")
    public ApiResult updateApplicants(@PathVariable(value = "id") Integer id, @RequestBody ApplicantsStoreParamsVo2 applicants) {
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
    @RequiresPermissions("wlythcn:list")
    @PostMapping("/list/del/{id}")
    @ApiOperation(value = "经营者列表-摘牌提交", notes = "经营者列表-根据无理由退货承诺实体店 id 摘牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "承诺单位id", required = true)
    })
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "delReason", value = "摘牌具体原因", required = true),
            @DynamicParameter(name = "delOther", value = "摘牌其它必要信息", required = true)
    }))
    @OperatorLogAnno(operType = "更新", operModul = "无理由退货承诺", operDesc = "经营者列表-根据承诺单位 id 摘牌")
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

    @RequiresPermissions("wlythcn:list")
    @GetMapping(value = "/downTemplateFile")
    @ApiOperation(value = "经营者列表-模板下载", notes = "模板下载")
    @OperatorLogAnno(operType = "下载", operModul = "无理由退货承诺", operDesc = "经营者列表-模板下载")
    public void downTemplateFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            TemplateExportParams params = new TemplateExportParams(ResourceUtils.getFile(storeTemplateFilePath).getPath());
            // 输出全部的sheet
            params.setScanAllsheet(true);
            Workbook workbook = ExcelExportUtil.exportExcel(params, new HashMap<>());
            String fileName = "线下无理由退货承诺店导入模板（备注：请启用宏）.xlsm";
            ExcelUtil.downLoadExcel(fileName, request, response, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresPermissions("wlythcn:upload")
    @PostMapping(value = "/preImport")
    @ApiOperation(value = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项", notes = "经营者列表-检查导入的在期单位名称中是否存在与现有在期名单相同项")
    @ApiImplicitParam(name = "file", value = "导入文件", required = true, dataType = "__File")
    @OperatorLogAnno(operType = "导入", operModul = "无理由退货承诺", operDesc = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项")
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
            importParams.setVerifyHandler(new ApplicantsStoreExcelVerifyHandlerImpl());
            importParams.setImportFields(title);

            ExcelImportResult<ApplicantsStoreExcelImportVo> result = ExcelImportUtil.importExcelMore(
                    in, ApplicantsStoreExcelImportVo.class, importParams);

            // 是否存在校验失败
            boolean verfiyFail = result.isVerifyFail();
            List<ApplicantsStoreExcelImportVo> failList = result.getFailList();
            List<ApplicantsStoreExcelImportVo> applicantsStoreExcelImportVos = result.getList();

            if (verfiyFail) {
                failList.stream().forEach(e -> {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo(e.getRowNum(), e.getErrorMsg()));
                });
                if (errorMsgVoList.size() > 0) {
                    return ApiResult.fail(errorMsgVoList);
                }
            }
            if (applicantsStoreExcelImportVos.size() > 0) {

                //检测数据是否跨区县
                User user = (User) SecurityUtils.getSubject().getPrincipal();
                int roleId = user.getRoleId();

                if (roleId == 3) {

                    String district = user.getDistrict();
                    for (int i = 0; i < applicantsStoreExcelImportVos.size(); i++) {
                        String vo = applicantsStoreExcelImportVos.get(i).getDistrict();
                        if (!vo.contains(district)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他地区数据，请去掉后再上传"));
                            return ApiResult.fail(errorMsgVoList);
                        }
                    }
                } else if (roleId == 2) {
                    String city = user.getCity();
                    for (int i = 0; i < applicantsStoreExcelImportVos.size(); i++) {
                        String vo = applicantsStoreExcelImportVos.get(i).getCity();
                        if (!vo.contains(city)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他市区数据，请去掉后再上传"));
                            return ApiResult.fail(errorMsgVoList);
                        }
                    }
                } else if (roleId == 4) {
                    String city = user.getCity();
                    for (int i = 0; i < applicantsStoreExcelImportVos.size(); i++) {
                        String vo = applicantsStoreExcelImportVos.get(i).getCity();
                        if (!vo.contains(city)) {
                            errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】包含其他其他市区数据，请去掉后再上传"));
                            return ApiResult.fail(errorMsgVoList);
                        }
                    }
                }

                List<String> creditCodes = new ArrayList<>();

                for (int i = 0; i < applicantsStoreExcelImportVos.size(); i++) {
                    creditCodes.add(applicantsStoreExcelImportVos.get(i).getCreditCode());
                }
                String[] array = new String[creditCodes.size()];
                array = creditCodes.toArray(array);
                List<ArrayList<String>> same = ApplicantsImportUtil.findSame(array);
                if (same.size() > 0) {
                    errorMsgVoList.add(new ExcelImportErrorMsgVo("当前导入文件中【统一社会信用代码】存在重复，行:" + same));
                    return ApiResult.fail(errorMsgVoList);
                }

                List<Applicants> applicantsStatus5_6 = myApplicantsUnitService.findApplicantsByCreditCodeAndStatus5_6(creditCodes.toArray(), 2);
                AtomicInteger rowNum0 = new AtomicInteger(3);
                AtomicBoolean flag0 = new AtomicBoolean(false);
                boolean isAduit = applicantsStoreExcelImportVos.stream().sequential().anyMatch(app -> {
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

                List<Applicants> applicantsStatus1 = myApplicantsUnitService.findApplicantsByRegName(2, creditCodes.toArray());
                AtomicInteger rowNum1 = new AtomicInteger(3);
                AtomicBoolean flag1 = new AtomicBoolean(false);
                boolean isRepact1 = applicantsStoreExcelImportVos.stream().sequential().anyMatch(app -> {
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

                List<Applicants> applicantsStatus4 = myApplicantsUnitService.findApplicantsByRegNameAndStatus4(2, creditCodes.toArray());

                boolean isRepact = false;
                AtomicInteger rowNum = new AtomicInteger(3);
                AtomicBoolean flag = new AtomicBoolean(false);
                if (applicantsStatus4 != null && applicantsStatus4.size() > 0) {
                    isRepact = applicantsStoreExcelImportVos.stream().sequential().anyMatch(app -> {
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

    @RequiresPermissions("wlythcn:upload")
    @PostMapping(value = "/import")
    @ApiOperation(value = "经营者列表-导入", notes = "经营者列表-导入")
//    @ApiImplicitParam(name = "map", value = "参数map", required = true)
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "fileId", value = "文件id", required = true)
    }))
    @OperatorLogAnno(operType = "导入", operModul = "无理由退货承诺", operDesc = "经营者列表-导入")
    public ApiResult<List<Applicants>> templateImport(@RequestBody Map map) {
        File file = null;
        try {
            String path = ResourceUtils.getURL("classpath:").getPath() + importFileTmp;
            file = ResourceUtils.getFile(path + map.get("fileId"));

            ImportParams importParams = new ImportParams();
            importParams.setStartRows(1);
            // importParams.setNeedVerify(true);
            // importParams.setVerifyHandler(new ApplicantsStoreExcelVerifyHandlerImpl());

            // List<ApplicantsStoreExcelImportVo> applicantsStoreExcelVos = ExcelImportUtil.importExcel(in, ApplicantsStoreExcelImportVo.class, importParams);
            ExcelImportResult<ApplicantsStoreExcelImportVo> result = ExcelImportUtil.importExcelMore(
                    file, ApplicantsStoreExcelImportVo.class, importParams);

            // 是否存在校验失败
            boolean verfiyFail = result.isVerifyFail();
            List<ApplicantsStoreExcelImportVo> failList = result.getFailList();
            List<ApplicantsStoreExcelImportVo> applicantsStoreExcelVos = result.getList();

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
            if (applicantsStoreExcelVos.size() > 0) {

                //检测数据是否跨区县
//                User user = (User) SecurityUtils.getSubject().getPrincipal();
//                int roleId = user.getRoleId();
//
//                if (roleId==3){
//
//                    String district = user.getDistrict();
//                    for (int i = 0; i < applicantsStoreExcelVos.size(); i++) {
//                        String vo=applicantsStoreExcelVos.get(i).getDistrict();
//                        if (!vo.contains(district)){
//                            return ApiResult.fail("地市用户,暂不支持导入");
//                        }
//                    }
//                }else if (roleId ==2){
//                    return ApiResult.fail("地市用户,暂不支持导入");
//                }

                List<Applicants> applicantsList = myApplicantsStoreService.templateImport(applicantsStoreExcelVos);

                return ApiResult.success(applicantsList);
            }
            return ApiResult.fail("导入文件为空，请重新选择");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return ApiResult.fail("导入失败");
    }

    @RequiresPermissions("wlythcn:list")
    @GetMapping(value = "/export")
    @ApiOperation(value = "经营者列表-导出", notes = "经营者列表-导出")
    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌)，为空则导出全部", example = "1")
    @OperatorLogAnno(operType = "导出", operModul = "无理由退货承诺", operDesc = "经营者列表-导出")
    public void export(@RequestParam(value = "status", required = false) String status, HttpServletRequest request, HttpServletResponse response) {

        // 获取登录用户
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
        Integer roleId = user.getRoleId();
        String city = user.getCity();

        List<Applicants> applicantsList = applicantsMapper.applicantsExport(2, status, roleId, user.getCity(), user.getDistrict());

        /*List<Applicants> applicantsList = applicantsService.list(
                new QueryWrapper<Applicants>()
                        .eq("type",2)
                        .eq(StringUtils.isNotBlank(status), "status", status)
                        .eq(roleId != 1, "city", user.getCity()));*/

        List<ApplicantsStoreExcelVo> applicantsStoreExcelVos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM");
        applicantsList.stream().forEach(app -> {
            ApplicantsStoreExcelVo applicantsStoreExcelVo = new ApplicantsStoreExcelVo();
            BeanUtils.copyProperties(app, applicantsStoreExcelVo);
            if (app.getApplicationDate() != null) {
                applicantsStoreExcelVo.setApplicationDate(formatter.format(app.getApplicationDate()));
            }
            if (app.getIndustryDate() != null) {
                applicantsStoreExcelVo.setIndustryDate(formatter.format(app.getIndustryDate()));
            }
            if (app.getCcDate() != null) {
                applicantsStoreExcelVo.setCcDate(formatter.format(app.getCcDate()));
            }
            if (app.getDelTime() != null) {
                applicantsStoreExcelVo.setDelTime(formatter.format(app.getDelTime()));
            }
            if (app.getStartTime() != null) {
                applicantsStoreExcelVo.setStartTime(formatterDate.format(app.getStartTime()));
            }
            applicantsStoreExcelVo.setDelReason(app.getDelReason());
            applicantsStoreExcelVo.setDelOther(app.getDelOther());
            applicantsStoreExcelVos.add(applicantsStoreExcelVo);
        });

        String fileName = "线下无理由退货承诺店.xlsx";
        if (roleId == 2 && !Objects.isNull(city)) {
            fileName = city + " 线下无理由退货承诺店.xlsx";
        } else if (roleId == 3 && !Objects.isNull(city) && !Objects.isNull(user.getDistrict())) {
            fileName = city + "_" + user.getDistrict() + " 线下无理由退货承诺店.xlsx";
        }
        ExcelUtil.exportExcel(applicantsStoreExcelVos, "", "", ApplicantsStoreExcelVo.class, fileName, request, response);
    }

    @RequiresPermissions("wlythcn:jdtstj")
    @PostMapping("/operatorStatistics/list")
    @ApiOperation(value = "经营者统计-列表", notes = "经营者统计-列表")
    @ApiOperationSupport(params = @DynamicParameters(name = "map", properties = {
            @DynamicParameter(name = "current", value = "当前页", example = "1"),
            @DynamicParameter(name = "size", value = "每页条数", example = "10"),
            @DynamicParameter(name = "startTime", value = "开始时间", required = false),
            @DynamicParameter(name = "endTime", value = "结束时间", required = false),
    }))
    @OperatorLogAnno(operType = "查询", operModul = "无理由退货承诺", operDesc = "经营者统计-列表")
    public ApiResult<PageResultLocal<StoreOperatorStatisticsVo>> operatorStatistics(@RequestBody Map map) {

        try {
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            // roleId == 1 ，说明是管理员，可以查看全部，否则根据地市去查
            Integer roleId = user.getRoleId();
            String city = user.getCity();

            List<StoreOperatorStatisticsVo> operatorStatistics = applicantsMapper.storeOperatorStatistics(
                    map.get("startTime").toString(),
                    map.get("endTime").toString(), roleId, city, user.getDistrict());

            return ApiResult.success(operatorStatistics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResult.fail();
    }

    @RequiresPermissions("wlythcn:jdtstj")
    @GetMapping("/operatorStatistics/export")
    @ApiOperation(value = "经营者统计-导出", notes = "经营者统计-导出")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
                    @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
            }
    )
    @OperatorLogAnno(operType = "导出", operModul = "无理由退货承诺", operDesc = "经营者统计-列表导出")
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

            List<StoreOperatorStatisticsVo> operatorStatistics = applicantsMapper.storeOperatorStatistics(startTime, endTime, roleId, city, user.getDistrict());

            String fileName = "无理由退货实体店-经营者统计.xlsx";
            if (roleId == 2 && !Objects.isNull(city)) {
                fileName = city + " 无理由退货实体店-经营者统计.xlsx";
            } else if (roleId == 3 && !Objects.isNull(city) && !Objects.isNull(user.getDistrict())) {
                fileName = city + "_" + user.getDistrict() + " 无理由退货实体店-经营者统计.xlsx";
            }

            ExcelUtil.exportExcel(operatorStatistics, "", "", StoreOperatorStatisticsVo.class, fileName, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/audit")
    @ApiOperation(value = "无理由退货承诺单位审核", notes = "无理由退货承诺单位审核。当前登录用户的角色id小于auditRoleId，并且当前状态在4（待审核）、 5（县级审核通过）、6（市级审核通过）之一，说明可以审核")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "type", value = "1：审核通过，2：审核不通过", example = "1", dataType = "int", required = true),
                    @ApiImplicitParam(name = "id", value = "承诺单位id", dataType = "int", required = true),
                    @ApiImplicitParam(name = "notes", value = "备注信息", required = true),
            }
    )
    @OperatorLogAnno(operType = "更新", operModul = "无理由退货承诺", operDesc = "放心消费承诺单位审核")
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
    @ApiOperation(value = "无理由退货承诺新录入", notes = "无理由退货承诺新录入")
    @DynamicParameters(
            name = "enterpriseStoreNewApplyVo",
            properties = {
                    @DynamicParameter(name = "enterpriseStoreNewApplyVo", value = "enterpriseStoreNewApplyVo", dataTypeClass = EnterpriseStoreNewApplyVo.class, required = true)
            }
    )
    @OperatorLogAnno(operType = "新增", operModul = "无理由退货承诺", operDesc = "放心消费承诺新录入")
    public ApiResult<EnterpriseStoreNewApplyVo> saveEnterpriseApplyInfo(@RequestBody EnterpriseStoreNewApplyVo enterpriseStoreNewApplyVo) {
        try {
            List<Applicants> applicantsByCreditCode = myApplicantsUnitService.findApplicantsByCreditCode(2, enterpriseStoreNewApplyVo.getCreditCode());

            if (applicantsByCreditCode != null && applicantsByCreditCode.size() > 0) {
                return ApiResult.fail("存在相同统一社会信用代码");
            }

            Applicants applicants = newStoreApplicants(enterpriseStoreNewApplyVo);
            // 获取登录用户
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            Integer rode = user.getRoleId();
            applicants.setCreater(user.getId());

            if (StringUtils.isNotBlank(applicants.getCity()) && StringUtils.isNotBlank(applicants.getDistrict())) {
                applicants.setAuditRoleId(4);
            } else if (StringUtils.isNotBlank(applicants.getCity())) {
                applicants.setAuditRoleId(3);
            }

            if (enterpriseStoreNewApplyVo.getDetails() != null && enterpriseStoreNewApplyVo.getDetails().size() > 0) {
                applicants.setDetails(String.join(",", enterpriseStoreNewApplyVo.getDetails()));
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

