/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */







package net.mingsoft.basic.action;


import cn.hutool.core.util.ReUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.bean.ManagerModifyPwdBean;
import net.mingsoft.basic.biz.IManagerBiz;
import net.mingsoft.basic.biz.IModelBiz;
import net.mingsoft.basic.entity.ManagerEntity;
import net.mingsoft.basic.entity.ModelEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.RedisUtil;
import net.mingsoft.basic.util.StringUtil;
import net.mingsoft.config.MSProperties;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 主界面控制层
 * @author 铭飞开发团队
 * @version
 * 版本号：100-000-000<br/>
 * 创建日期：2014-7-14<br/>
 * 历史修订：<br/>
 */
@Api(tags={"后端-基础接口"})
@Controller
@RequestMapping("/${ms.manager.path}")
public class MainAction extends BaseAction {

	/**
	 * 模块业务层
	 */
	@Autowired
	private IModelBiz modelBiz;

	/**
	 * 管理员业务层
	 */
	@Autowired
	private IManagerBiz managerBiz;

	@Autowired
	private DataSource dataSource;

	@Value("${ms.pass-error-max-times:5}")
	private int passErrorMaxTimes;

	@Value("${ms.pass-error-lock-time:30}")
	private int passErrorLockTime;

	@Value("${ms.pass-error-keep-time:30}")
	private int passErrorKeepTime;

	@Value("${ms.pass-change-max-day:90}")
	private int passChangeMaxDay;


	/**
	 * 加载后台主界面，并查询数据
	 * @param request 请求对象
	 * @return  主界面地址
	 */
	@ApiOperation(value = "加载后台主界面，并查询数据")
	@GetMapping(value = {"/index","/"})
	public String index(HttpServletRequest request) {
		String managerPath = MSProperties.manager.path;
		ManagerEntity managerSession =  BasicUtil.getManager();
		List<ModelEntity> modelList = new ArrayList<ModelEntity>();
		ModelEntity model = new ModelEntity();
		modelList = modelBiz.queryModelByRoleId(managerSession.getRoleId());
		//如果ischild有值，则不显示
		List<BaseEntity> _modelList = new ArrayList<BaseEntity>();
		for(int i=0;i<modelList.size();i++){
			ModelEntity _model = (ModelEntity) modelList.get(i);
			if(StringUtils.isBlank(_model.getIsChild())){
				_modelList.add(_model);
			}
		}
		request.setAttribute("managerSession", managerSession);
		request.setAttribute("modelList", JSONObject.toJSONString(modelList));
		request.setAttribute("client", BasicUtil.getDomain()+"/"+managerPath);
		request.setAttribute("app", BasicUtil.getApp());
		return "/index";
	}

	@ApiIgnore
	@GetMapping("/main")
	public String main(HttpServletRequest request) {
		return "/main";
	}

	/**
	 * 系统信息
	 * @param request
	 * @return 表单页面地址
	 */
	@ApiIgnore
	@ApiOperation(value = "加载UI的表单页面")
	@GetMapping("/system")
	public String system(HttpServletRequest request) {
		return "/system";
	}


	/**
	 * 修改登录密码，若不填写密码则表示不修改
	 *
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 */
	@ApiOperation(value = "修改登录密码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "oldManagerPassword", value = "旧密码", required = true,paramType="query"),
			@ApiImplicitParam(name = "newManagerPassword", value = "新密码", required = true,paramType="query"),
	})
	@PostMapping("/updatePassword")
	@ResponseBody
	public ResultData updatePassword(@ModelAttribute @ApiIgnore ManagerModifyPwdBean managerModifyPwdBean, HttpServletResponse response, HttpServletRequest request) {
		if (checkManagerPassChangeErrLock(managerModifyPwdBean.getManagerName())) {
			// 密码错误超时锁定中
			return ResultData.build().error("密码错误超时锁定中（分钟）：" + RedisUtil.ttl("PassChangeErrLock:" + managerModifyPwdBean.getManagerName()) / 60);
		}
		String oldManagerPassword = request.getParameter("oldManagerPassword");
		//获取新的密码
		String newManagerPassword = managerModifyPwdBean.getNewManagerPassword();
		//获取管理员信息
		ManagerEntity manager = BasicUtil.getManager();
		// 判断新密码和旧密码是否为空
		if (StringUtils.isBlank(newManagerPassword) || StringUtils.isBlank(oldManagerPassword)) {
			return ResultData.build().error(getResString("err.empty", this.getResString("managerPassword")));
		}
		// 判断新密码长度
		if (!StringUtil.checkLength(newManagerPassword, 8, 30)) {
			return ResultData.build().error(getResString("err.length", this.getResString("managerPassword"), "8", "30"));
		}
		// 判断新密码是否符合规则
		if (!checkManagerPass(newManagerPassword)) {
			return ResultData.build().error("请输入由大小写字母、数字和特殊符号组合的密码");
		}
		//判断旧的密码是否正确
		if (!DigestUtils.sha256Hex(oldManagerPassword).equals(manager.getManagerPassword())) {
			int times = 1;
			String passChangeErrTimes = (String) RedisUtil.getValue("PassChangeErrTimes:" + manager.getManagerName());
			if (StringUtils.isNotBlank(passChangeErrTimes)) {
				try {
					times = Integer.parseInt(passChangeErrTimes);
					times++;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (times >= passErrorMaxTimes) {
				if (!checkManagerPassChangeErrLock(manager.getManagerName())) {
					RedisUtil.addValue("PassChangeErrLock:" + manager.getManagerName(), "Locked", passErrorLockTime, TimeUnit.MINUTES);
					times = 0;
				}
			}
			RedisUtil.addValue("PassChangeErrTimes:" + manager.getManagerName(), String.valueOf(times), passErrorKeepTime, TimeUnit.MINUTES);
			return ResultData.build().error(this.getResString("manager.password.old.err"));
		}
		//更改密码
		manager.setManagerPassword(DigestUtils.sha256Hex(newManagerPassword));
		//更新
		managerBiz.updateUserPasswordByUserName(manager);
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		RedisUtil.addValue("PassChangeMaxDay:" + manager.getManagerName(), "Changed", passChangeMaxDay, TimeUnit.DAYS);
		return ResultData.build().success();
	}

	@ApiOperation(value = "超时修改登录密码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "oldManagerPassword", value = "旧密码", required = true,paramType="query"),
			@ApiImplicitParam(name = "newManagerPassword", value = "新密码", required = true,paramType="query"),
	})
	@PostMapping("/updatePasswordForce")
	@ResponseBody
	public ResultData updatePasswordForce(@ModelAttribute @ApiIgnore ManagerModifyPwdBean managerModifyPwdBean, HttpServletResponse response, HttpServletRequest request) {
		if (checkManagerPassChangeErrLock(managerModifyPwdBean.getManagerName())) {
			// 密码错误超时锁定中
			return ResultData.build().error("密码错误超时锁定中（分钟）：" + RedisUtil.ttl("PassChangeErrLock:" + managerModifyPwdBean.getManagerName()) / 60);
		}
		//获取新的密码
		String newManagerPassword = managerModifyPwdBean.getNewManagerPassword();
		//获取管理员信息
		ManagerEntity manager = managerBiz.getManagerByManagerName(managerModifyPwdBean.getManagerName());
		// 判断新密码和旧密码是否为空
		if (StringUtils.isBlank(newManagerPassword) || StringUtils.isBlank(managerModifyPwdBean.getOldManagerPassword())) {
			return ResultData.build().error(getResString("err.empty", this.getResString("managerPassword")));
		}
		// 判断新密码长度
		if (!StringUtil.checkLength(newManagerPassword, 8, 30)) {
			return ResultData.build().error(getResString("err.length", this.getResString("managerPassword"), "8", "30"));
		}
		// 判断新密码是否符合规则
		if (!checkManagerPass(newManagerPassword)) {
			return ResultData.build().error("请输入由大小写字母、数字和特殊符号组合的密码");
		}
		//判断旧的密码是否正确
		if(!DigestUtils.sha256Hex(managerModifyPwdBean.getOldManagerPassword()).equals(manager.getManagerPassword())){
			int times = 1;
			String passChangeErrTimes = (String) RedisUtil.getValue("PassChangeErrTimes:" + manager.getManagerName());
			if (StringUtils.isNotBlank(passChangeErrTimes)) {
				try {
					times = Integer.parseInt(passChangeErrTimes);
					times++;
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (times >= passErrorMaxTimes) {
				if (!checkManagerPassChangeErrLock(manager.getManagerName())) {
					RedisUtil.addValue("PassChangeErrLock:" + manager.getManagerName(), "Locked", passErrorLockTime, TimeUnit.MINUTES);
					times = 0;
				}
			}
			RedisUtil.addValue("PassChangeErrTimes:" + manager.getManagerName(), String.valueOf(times), passErrorKeepTime, TimeUnit.MINUTES);
			return ResultData.build().error(this.getResString("manager.password.old.err"));
		}
		//更改密码
		manager.setManagerPassword(DigestUtils.sha256Hex(newManagerPassword));
		//更新
		managerBiz.updateUserPasswordByUserName(manager);
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		RedisUtil.addValue("PassChangeMaxDay:" + manager.getManagerName(), "Changed", passChangeMaxDay, TimeUnit.DAYS);
		return ResultData.build().success();
	}

	/**
	 * 退出系统
	 * @param request 请求对象
	 * @return true退出成功
	 */
	@ApiOperation(value = "退出系统")
	@GetMapping("/loginOut")
	@ResponseBody
	public ResultData loginOut(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return ResultData.build().success();
	}

	/**
	 * 获取系统配置信息
	 * @param request 请求对象
	 * @return true退出成功
	 */
	@ApiOperation(value = "获取系统配置信息")
	@PostMapping("/system")
	@ResponseBody
	public ResultData system(HttpServletResponse response, HttpServletRequest request) {

		Properties props = System.getProperties();// 获取当前的系统属性
		HashMap<String, Object> map = new HashMap<>();
		map.put("CPU核数", String.valueOf(Runtime.getRuntime().availableProcessors()));

		// 单位B 转换为M
		map.put("虚拟机内存总量", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		map.put("虚拟机空闲内存量", String.valueOf(Runtime.getRuntime().freeMemory() / 1048576));
		map.put("虚拟机使用最大内存量", String.valueOf(Runtime.getRuntime().maxMemory() / 1048576));

		map.put("系统名称", props.getProperty("os.name"));
		map.put("系统构架", props.getProperty("os.arch"));
		map.put("系统版本", props.getProperty("os.version"));

		map.put("Java版本", props.getProperty("java.version"));
		map.put("Java安装路径", props.getProperty("java.home"));

		CpuInfo cpu = OshiUtil.getCpuInfo();
		map.put("cpu信息", cpu.getCpuModel() + "" + cpu.getCpuNum());
		map.put("内存总量", OshiUtil.getMemory().getTotal() / 1048576);
		map.put("内存可用", OshiUtil.getMemory().getAvailable() / 1048576);

		//数据库
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData mtdt = connection.getMetaData();
			map.put("数据库链接", mtdt.getURL());
			map.put("数据库",mtdt.getDatabaseProductName());
			map.put("数据库版本",mtdt.getDatabaseProductVersion());

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		ServletContext context = request.getServletContext();
		map.put("web容器",context.getServerInfo());
		map.put("发布路径",context.getRealPath(""));
//
		return ResultData.build().success(map);
	}

	public boolean checkManagerPassChangeErrLock(String managerName) {
		String checkValue = (String) RedisUtil.getValue("PassChangeErrLock:" + managerName);
		return StringUtils.isNotBlank(checkValue);
	}

	/**
	 * 检查密码复杂度
	 * @param pass
	 * @return
	 */
	private boolean checkManagerPass(String pass) {
		return ReUtil.isMatch("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&,.*]).{8,30}$", pass);
	}
}
