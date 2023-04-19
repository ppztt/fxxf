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
    @GetMapping("/consumer")
    public String consumer(HttpServletRequest request, ModelMap model) {

        return "/fxxf/applicant/consumer";
    }

    @GetMapping("/check")
    public String check() {
        return "/fxxf/applicant/check";
    }


    @GetMapping("/backstageUser")
    public String backstageUser() {
        return "/fxxf/backstageUser/index";
    }
    @GetMapping("/associationUser")
    public String associationUser() {
        return "/fxxf/associationUser/associationUser";
    }

    @GetMapping("/Unwarranted")
    public String Unwarranted() {
        return "/fxxf/Unwarranted/Unwarranted";
    }
    @GetMapping("/UnwarrantedCheck")
    public String UnwarrantedCheck() {
        return "/fxxf/Unwarranted/check";
    }

    @GetMapping("/userInfoChange")
    public String userInfoChange() {
        return "/fxxf/userInfoChange/index";
    }

}
