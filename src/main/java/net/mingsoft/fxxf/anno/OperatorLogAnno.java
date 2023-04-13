package net.mingsoft.fxxf.anno;

import java.lang.annotation.*;

/**
 * 定义操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperatorLogAnno
{
	/*
	* 操作类型
	*  */
	String operType();

	/*
	* 操作模块
	*  */
	String operModul();

	/*
	* 操作说明
	*  */
	String operDesc();
}