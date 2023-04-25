package net.mingsoft.fxxf.service;


import com.baomidou.mybatisplus.extension.service.IService;
import net.mingsoft.fxxf.bean.base.BasePageResult;
import net.mingsoft.fxxf.bean.base.BaseResult;
import net.mingsoft.fxxf.bean.entity.Applicants;
import net.mingsoft.fxxf.bean.request.ApplicantsPageRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatisticsRequest;
import net.mingsoft.fxxf.bean.request.ApplicantsStatusUpdateRequest;
import net.mingsoft.fxxf.bean.request.EnterpriseNewApplyRequest;
import net.mingsoft.fxxf.bean.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 申报单位 服务类
 */
public interface ApplicantsService extends IService<Applicants> {

    List<RegionVo> getGdRegion();

    BaseResult<BasePageResult<Applicants>> listPage(ApplicantsPageRequest applicantsPageRequest);


    ApplicantsFindVo findApplicantsByRegName(Integer id, String creditCode, String type);


    /**
     * 经营者列表-根据id查询单位
     */
    ApplicantsParamsVo findApplicants(Applicants applicants);


    /**
     * 经营者列表-编辑保存
     */
    BaseResult<String> updateApplicants(ApplicantsParamsVo applicantsParamVo);

    /**
     * 经营者列表-根据单位id更新状态及原因
     */
    BaseResult<String> updateApplicantsStatus(ApplicantsStatusUpdateRequest applicantsStatusUpdateRequest);

    /**
     * 经营者列表-模板下载
     */
    void downTemplateFile(Integer type, HttpServletRequest request, HttpServletResponse response);

    BaseResult templatePreImport(Integer type,MultipartFile file);

    BaseResult audit(Integer id, Integer type, String notes);

    BaseResult<ArrayList<Applicants>> templateImport(String fileId);

    void export(Integer type, String status, HttpServletRequest request, HttpServletResponse response);

    BaseResult<ArrayList<OperatorStatisticsVo>> operatorStatistics(ApplicantsStatisticsRequest applicantsStatisticsRequest);

    void operatorStatisticsExport(Integer type, String startTime, String endTime, HttpServletRequest request, HttpServletResponse response);

    BaseResult<EnterpriseUnitNewApplyVo> saveEnterpriseApplyInfo(EnterpriseNewApplyRequest enterpriseNewApplyRequest);

    List<Applicants> findApplicantsByCreditCode(Integer type, String creditCode);

    List<Applicants> findApplicantsByCreditCodes(Integer type, List<String> creditCode);

    List<Applicants> findApplicantsByCreditCode(Integer id, Integer type, String creditCode);

}
