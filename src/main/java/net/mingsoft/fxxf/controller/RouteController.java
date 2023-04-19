package net.mingsoft.fxxf.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 菜单页面路由跳转控制器
 */
@Api(tags = {"菜单页面路由跳转控制器"})
@Controller("routeController")
@RequestMapping("/${ms.manager.path}/route")
@Slf4j
@Scope("request")
public class RouteController {

//    /**
//     * 监督投诉统计菜单跳转
//     */
//    @GetMapping("/feedback/statistic")
//    public String index() {
//        return "/fxxf/feedback/index";
//    }

    /**
     * /**
     * 放心消费承诺监督投诉页面
     *
     * @return
     */
    @GetMapping("/feedbackIndex")
    public String feedbackIndex(HttpServletRequest request, ModelMap model) {
        return "/fxxf/trustConsumption/supervise/supervise";
    }

    @GetMapping("/feedbackDetail")
    public String feedbackDetail() {
        return "/fxxf/trustConsumption/supervise/checkSupervise";
    }

    @GetMapping("/feedbackHandle")
    public String feedbackHandle() {
        return "/fxxf/trustConsumption/supervise/complaint";
    }

    /**
     * /**
     * 放心消费承诺监督投诉统计
     *
     * @return
     */
    @GetMapping("/statisticsIndex")
    public String statisticsIndex(HttpServletRequest request, ModelMap model) {
        return "/fxxf/trustConsumption/statistics/index";
    }

    /**
     * /**
     * 放心消费承诺经营者统计
     *
     * @return
     */
    @GetMapping("/operatorIndex")
    public String operatorIndex(HttpServletRequest request, ModelMap model) {
        return "/fxxf/trustConsumption/operator/index";
    }

    /**
     * /**
     * 商品和服务类别统计
     *
     * @return
     */
    @GetMapping("/managementIndex")
    public String backstage(HttpServletRequest request, ModelMap model) {
        return "/fxxf/manaGement/commodity/index";
    }

    /**
     * /**
     * 申报材料管理
     *
     * @return
     */
    @GetMapping("/declareMaterial")
    public String declareMaterial(HttpServletRequest request, ModelMap model) {
        return "/fxxf/manaGement/declare/index";
    }


    /**
     * /**
     * 无理由退货承诺监督投诉页面
     *
     * @return
     */
    @GetMapping("/reasonSupervision")
    public String reasonSupervision(HttpServletRequest request, ModelMap model) {
        return "/fxxf/withoutReason/supervise/supervise";
    }

    @GetMapping("/reasonDetail")
    public String reasonDetail() {
        return "/fxxf/withoutReason/supervise/checkSupervise";
    }

    @GetMapping("/reasonHandle")
    public String reasonHandle() {
        return "/fxxf/withoutReason/supervise/complaint";
    }

    /**
     * /**
     * 无理由退货承诺监督投诉统计
     *
     * @return
     */
    @GetMapping("/reasonStatistic")
    public String reasonStatistic(HttpServletRequest request, ModelMap model) {
        return "/fxxf/withoutReason/statistics/index";
    }

    /**
     * /**
     * 无理由退货承诺经营者统计
     *
     * @return
     */
    @GetMapping("/reasonOperator")
    public String reasonOperator(HttpServletRequest request, ModelMap model) {
        return "/fxxf/withoutReason/operator/index";
    }
}


