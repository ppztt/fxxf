package net.mingsoft.fxxf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.fxxf.anno.OperatorLogAnno;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.fxxf.mapper.FeedbackMapper;
import net.mingsoft.fxxf.mapper.UserMapper;
import net.mingsoft.fxxf.vo.ApplicantBasePageRequest;
import net.mingsoft.fxxf.vo.BasePageResult;
import net.mingsoft.fxxf.vo.BaseResult;
import net.mingsoft.fxxf.vo.FeedbackComplaintVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 留言反馈
 */
@Api(tags = {"留言反馈相关接口"})
@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FeedbackMapper feedbackMapper;

    /**
     *  查询企业投诉数量和已处理数量统计数据
     */
    // @RequiresPermissions("wlythcn:jdts")
    @GetMapping("/countByApplicantList")
    @ApiOperation(value = "监督投诉-列表", notes = "监督投诉-列表")
    @OperatorLogAnno(operType = "查询", operModul = "放心消费承诺单位/无理由退货承诺单位", operDesc = "监督投诉-列表")
    public BaseResult<BasePageResult<FeedbackComplaintVo>> countByApplicantList(ApplicantBasePageRequest applicantBasePageRequest) {
        try {
            // 获取登录用户
            Subject currentSubject = SecurityUtils.getSubject();
            ManagerEntity manager = (ManagerEntity) currentSubject.getPrincipal();
            Integer roleId = manager.getRoleId();

            User user = userMapper.selectById(manager.getId());
            Map<String, Object> map = new HashMap<>();

            map.put("roleId", roleId);
            map.put("city", user.getCity());
            map.put("district", user.getDistrict());
            map.put("type", applicantBasePageRequest.getType());
            map.put("search", applicantBasePageRequest.getSearch());
            IPage<FeedbackComplaintVo> feedbackIPage = feedbackMapper.feedbackList(new Page<>(applicantBasePageRequest.getCurrent(), applicantBasePageRequest.getSize()), map);
            return BaseResult.success(new BasePageResult<>(feedbackIPage.getCurrent(), feedbackIPage.getSize(), feedbackIPage.getPages(), feedbackIPage.getTotal(), feedbackIPage.getRecords()));
        } catch (Exception e) {
            log.error("获取放心消费承诺单位表格数据失败", e);
            return BaseResult.fail();
        }
    }

}
