package net.mingsoft.fxxf.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatisticsRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatusUpdateRequest;
import net.mingsoft.fxxf.bean.request.EnterpriseNewApplyRequest;
import net.mingsoft.fxxf.bean.vo.ApplicantsFindVo;
import net.mingsoft.fxxf.bean.vo.ApplicantsParamsVo;
import net.mingsoft.fxxf.bean.vo.ExcelImportErrorMsgVo;
import net.mingsoft.fxxf.bean.vo.OperatorStatisticsVo;
import net.mingsoft.fxxf.service.ApplicantsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 申报单位相关接口
 *
 * @author: huangjunjian
 */
@Api(tags = {"申报单位相关接口"})
@RestController
@RequestMapping("/${ms.manager.path}/applicants")
@Slf4j
public class ApplicantsController {

    @Autowired
    private ApplicantsService applicantsService;

    /**
     * 经营者条件分页列表查询
     */
    @PostMapping("/listPage")
    @ApiOperation(value = "经营者列表-分页列表查询")
    public BaseResult<BasePageResult<Applicants>> list(@RequestBody ApplicantsPageRequest applicantsPageRequest) {
        return applicantsService.listPage(applicantsPageRequest);
    }

    /**
     * 根据 id 查询承诺单位
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "经营者列表-根据id查询单位")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "单位id", required = true)})
    public BaseResult<ApplicantsParamsVo> findApplicantsById(@PathVariable(value = "id") Integer id) {
        Applicants applicants = applicantsService.getById(id);
        if (applicants != null) {
            return BaseResult.success(applicantsService.findApplicants(applicants));
        }

        return BaseResult.success();
    }

    @GetMapping("/find")
    @ApiOperation(value = "经营者列表-根据id和经营者注册名称查询是否重复", notes = "经营者列表-根据id和经营者注册名称查询是否重复")
    public BaseResult<ApplicantsFindVo> findApplicantsByRegName(@RequestParam(value = "id") Integer id,
                                                                @RequestParam(value = "creditCode") String creditCode,
                                                                @RequestParam(value = "type") String type) {
        return BaseResult.success(applicantsService.findApplicantsByRegName(id, creditCode, type));
    }

    /**
     * 根据 id 更新实体店
     */
    @PostMapping("/update")
    @ApiOperation(value = "经营者列表-编辑保存")
    public BaseResult<String> updateApplicants(@RequestBody ApplicantsParamsVo applicants) {
        return applicantsService.updateApplicants(applicants);
    }

    @PostMapping("/remove/{id}")
    @ApiOperation(value = "经营者列表-根据id删除单位")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "承诺单位id", required = true)})
    public BaseResult<String> delApplicantsById(@PathVariable(value = "id") Integer id) {

        return applicantsService.removeById(id) ? BaseResult.success(String.format("经营者列表-根据id：%d删除单位成功", id)) :
                BaseResult.fail(String.format("经营者列表-根据id：%d删除单位失败", id));
    }


    @PostMapping("/remove")
    @ApiOperation(value = "经营者列表-根据id批量删除承诺单位", notes = "经营者列表-根据 ids 删除承诺单位")
    public BaseResult delApplicantsById(@RequestBody Map map) {
        List<String> ids = (List<String>) map.get("ids");
        if (!ids.isEmpty()) {
            return applicantsService.removeByIds(ids) ? BaseResult.success(String.format("经营者列表-根据id：%s批量删除单位成功", ids)) :
                    BaseResult.fail(String.format("经营者列表-根据id：%s批量删除单位失败", ids));
        }

        return BaseResult.fail("ids为空", null);
    }

    /**
     * 根据经营者id更新状态及原因
     */
    @PostMapping("/updateApplicantsStatus")
    @ApiOperation(value = "根据经营者id更新状态及原因", notes = "根据经营者id更新状态及原因")
    public BaseResult<String> updateApplicantsStatus(@RequestBody ApplicantsStatusUpdateRequest applicantsStatusUpdateRequest) {
        return applicantsService.updateApplicantsStatus(applicantsStatusUpdateRequest);
    }

    @GetMapping(value = "/downTemplateFile/{type}")
    @ApiOperation(value = "经营者列表-模板下载", notes = "模板下载")
    public void downTemplateFile(@PathVariable(value = "type") Integer type, HttpServletRequest request, HttpServletResponse response) {
        applicantsService.downTemplateFile(type, request, response);
    }

    @PostMapping(value = "/preImport")
    @ApiOperation(value = "经营者列表-预导入，检查导入的在期单位名称中是否存在与现有在期名单相同项")
    public BaseResult<ArrayList<ExcelImportErrorMsgVo>> templatePreImport(@RequestParam("type") Integer type,
                                                                          @RequestParam("file") MultipartFile file) {

        if (!Objects.isNull(file) && !file.getOriginalFilename().endsWith(".xlsm")) {
            return BaseResult.fail("上传失败，请上传xlsm格式文件");
        }

        return applicantsService.templatePreImport(type, file);
    }

    @GetMapping(value = "/audit")
    @ApiOperation(value = "经营者审核", notes = "当前登录用户的角色id小于auditRoleId，并且当前状态在4（待审核）、 5（县级审核通过）、6（市级审核通过）之一，说明可以审核")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "type", value = "1：审核通过，2：审核不通过", example = "1", dataType = "int", required = true),
                    @ApiImplicitParam(name = "id", value = "承诺单位id", dataType = "int", required = true),
                    @ApiImplicitParam(name = "notes", value = "备注信息", required = true),
            }
    )
    public BaseResult audit(@RequestParam(value = "type") Integer type, @RequestParam(value = "id") Integer id,
                            @RequestParam(value = "notes", required = false) String notes) {
        return applicantsService.audit(id, type, notes);
    }

    @RequiresPermissions("wlythcn:upload")
    @PostMapping(value = "/import/{type}/{fileId}")
    @ApiOperation(value = "经营者列表-导入")
    public BaseResult<ArrayList<Applicants>> templateImport(@PathVariable(value = "type") Integer type, @PathVariable("fileId") String fileId) {
        return applicantsService.templateImport(type, fileId);
    }

    @RequiresPermissions("wlythcn:list")
    @GetMapping(value = "/export")
    @ApiOperation(value = "经营者列表-导出", notes = "经营者列表-导出")
    @ApiImplicitParam(name = "status", value = "状态(1:在期； 0:摘牌)，为空则导出全部", example = "1")
    public void export(@RequestParam(value = "type") Integer type,
                       @RequestParam(value = "status", required = false) String status,
                       HttpServletRequest request, HttpServletResponse response) {

        applicantsService.export(type, status, request, response);
    }


    @RequiresPermissions("wlythcn:jdtstj")
    @PostMapping("/operatorStatistics/list")
    @ApiOperation(value = "经营者统计-列表", notes = "经营者统计-列表")
    public BaseResult<ArrayList<OperatorStatisticsVo>> operatorStatistics(@RequestBody ApplicantsStatisticsRequest applicantsStatisticsRequest) {
        return applicantsService.operatorStatistics(applicantsStatisticsRequest);
    }

    @RequiresPermissions("wlythcn:jdtstj")
    @GetMapping("/operatorStatistics/export")
    @ApiOperation(value = "经营者统计-导出", notes = "经营者统计-导出")
    public void operatorStatisticsExport(@RequestParam(value = "type") Integer type,
                                         @RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime,
                                         HttpServletRequest request, HttpServletResponse response) {

        try {
            applicantsService.operatorStatisticsExport(type, startTime, endTime, request, response);
        } catch (Exception e) {
            log.error("经营者统计-导出接口异常", e);
        }
    }


    @PostMapping("/apply/input")
    @ApiOperation(value = "经营者企业数据记录录入")
    public BaseResult saveEnterpriseApplyInfo(@RequestBody EnterpriseNewApplyRequest enterpriseNewApplyRequest) {
        try {
            return applicantsService.saveEnterpriseApplyInfo(enterpriseNewApplyRequest);
        } catch (Exception e) {
            log.error("经营者企业数据记录录入异常", e);
            return BaseResult.fail(e.getMessage());
        }
    }
}
