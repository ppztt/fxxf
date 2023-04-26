/**
 * Copyright (c) 2012-2022 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */




package net.mingsoft.config;

import cn.hutool.core.codec.Base64;
import net.mingsoft.basic.realm.ManagerAuthRealm;
import net.mingsoft.basic.strategy.ILoginStrategy;
import net.mingsoft.basic.strategy.IModelStrategy;
import net.mingsoft.basic.strategy.ManagerLoginStrategy;
import net.mingsoft.basic.strategy.ManagerModelStrategy;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

	@Autowired(required = false)
	MSProperties msProperties;

	/**
	 * 开启Shiro的注解(如@RequiresRoles , @RequiresPermissions),需借助SspringAOP扫描使用Sshiro注解的类，并在必要时进行安全逻辑验证
	 * 配置以下两个bean(Defaul tAdvisorAutoProxyCreator和uthorizat ionAttributeSourceAdvisor)即可实现此功能
	 */
	@Bean
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(true);
		return advisorAutoProxyCreator;
	}

	/**
	 * 开启shiro aop注解支持
	 * 使用代理方式;所以需要开启代码支持
	 * @param securityManager
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
			@Autowired(required = false) DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		autoProxyCreator.setProxyTargetClass(true);
		return autoProxyCreator;
	}

	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
		shiroFilterFactoryBean.setLoginUrl(MSProperties.manager.path + "/login.do");
		// 设置无权限时跳转的 url;
		shiroFilterFactoryBean.setUnauthorizedUrl(MSProperties.manager.path + "/404.do");

		// 设置拦截器
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 游客，开发权限
		filterChainDefinitionMap.put("/static/**", "anon");
		filterChainDefinitionMap.put("/html/**", "anon");
		// 开放登陆接口
		filterChainDefinitionMap.put(MSProperties.manager.path + "/login.do", "anon");
		filterChainDefinitionMap.put(MSProperties.manager.path + "/checkLogin.do", "anon");
		filterChainDefinitionMap.put(MSProperties.manager.path + "/changepass.do", "anon");
		filterChainDefinitionMap.put(MSProperties.manager.path + "/updatePasswordForce.do", "anon");
		filterChainDefinitionMap.put(MSProperties.manager.path + "/index/**", "anon");
		// 其余接口一律拦截
		// 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
		filterChainDefinitionMap.put(MSProperties.manager.path + "/**", "user");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * 注入 securityManager
	 */
	@Bean
	public SecurityManager securityManager(DefaultWebSessionManager sessionManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(customRealm());
		//cookie管理配置对象
		securityManager.setRememberMeManager(rememberMeManager());
		//设置session管理
		securityManager.setSessionManager(sessionManager);
		return securityManager;
	}

	/**
	 * 重写defaultWebSessionManager,防止url拼接jsessionid
	 * @return
	 */
	@Bean
	public DefaultWebSessionManager defaultWebSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(getMemorySessionDAO());
		sessionManager.setSessionIdCookie(getSimpleCookie());
		sessionManager.setSessionIdUrlRewritingEnabled(false);
		return sessionManager;
	}

	/**
	 * cookie对象
	 * @return
	 */
	public SimpleCookie rememberMeCookie() {
		// 设置cookie名称，对应login.html页面的<input type="checkbox" name="rememberMe"/>
		SimpleCookie cookie = new SimpleCookie("rememberMe");
		// 设置cookie的过期时间，单位为秒，这里为一天
		cookie.setMaxAge(86400);
		return cookie;
	}

	/**
	 * cookie管理对象
	 * @return
	 */
	public CookieRememberMeManager rememberMeManager() {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(rememberMeCookie());
		// rememberMe cookie加密的密钥
		cookieRememberMeManager.setCipherKey(Base64.decode(msProperties.getShiroKey()));
		return cookieRememberMeManager;
	}

	@Bean
	public MemorySessionDAO getMemorySessionDAO() {
		return new MemorySessionDAO();
	}

	@Bean
	public SimpleCookie getSimpleCookie() {
		SimpleCookie simpleCookie = new SimpleCookie();
		simpleCookie.setName(msProperties.getCookieName());
		return simpleCookie;

	}



	/**
	 * 自定义身份认证 realm;
	 * <p>
	 * 必须写这个类，并加上 @Bean 注解，目的是注入 CustomRealm， 否则会影响 CustomRealm类 中其他类的依赖注入
	 */
	@Bean
	public ManagerAuthRealm customRealm() {
		return new ManagerAuthRealm();
	}

	/**
	 * 管理员菜单策略
	 *
	 * @return
	 */
	@Bean
	public IModelStrategy modelStrategy() {
		return  new ManagerModelStrategy();
	}

	/**
	 * 管理登录策略
	 *
	 * @return
	 */
	@Bean
	public ILoginStrategy loginStrategy() {
		return new ManagerLoginStrategy();
	}
}
