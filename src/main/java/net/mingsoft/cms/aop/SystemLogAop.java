package net.mingsoft.cms.aop;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import io.swagger.annotations.ApiOperation;
import net.mingsoft.base.entity.BaseEntity;
import net.mingsoft.basic.aop.BaseAop;
import net.mingsoft.basic.bean.ManagerModifyPwdBean;
import net.mingsoft.basic.biz.ILogBiz;
import net.mingsoft.basic.constant.e.BusinessTypeEnum;
import net.mingsoft.basic.constant.e.OperatorTypeEnum;
import net.mingsoft.basic.entity.LogEntity;
import net.mingsoft.basic.util.BasicUtil;
import net.mingsoft.basic.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @author luwb
 * @date 2023-04-28
 */
@Aspect
@Component("selfSystemLogAop")
public class SystemLogAop extends BaseAop {

    /**
     * 成功状态
     */
    private static final String SUCCESS = "success";
    /**
     * 失败状态
     */
    private static final String ERROR = "error";

    @Autowired
    private ILogBiz logBiz;

    /**
     * 切入点
     */
    @Pointcut("execution(* net.mingsoft.basic.action.MainAction.updatePassword*(..))")
    public void logPointCut()
    { }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result)
    {
        handleLog(joinPoint, null, result);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e)
    {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object result) {
        try {
            // 获得注解
            ApiOperation controllerLog = getAnnotation(joinPoint, ApiOperation.class);
            if (controllerLog == null) {
                return;
            }

            LogEntity logEntity = new LogEntity();
            logEntity.setLogStatus(SUCCESS);
            // 请求的地址
            String ip = BasicUtil.getIp();
            // 设置IP
            logEntity.setLogIp(ip);
            // 过滤id
            SimplePropertyPreFilter classAFilter = new SimplePropertyPreFilter(BaseEntity.class, "id");
            SerializeFilter[] filters = new SerializeFilter[]{classAFilter};
            // 设置返回参数
            logEntity.setLogResult(JSON.toJSONString(result, filters, SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteDateUseDateFormat));
            // 设置请求地址
            String requestURI = SpringUtil.getRequest().getRequestURI();
            logEntity.setLogUrl(requestURI);
            if (e != null) {
                logEntity.setLogStatus(ERROR);
                logEntity.setLogErrorMsg(StringUtils.substring(e.getMessage(), 0, 4000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logEntity.setLogMethod(className + "." + methodName + "()");
            // 设置请求方式
            logEntity.setLogRequestMethod(SpringUtil.getRequest().getMethod());

            // 设置action动作
            logEntity.setLogBusinessType(BusinessTypeEnum.UPDATE.name().toLowerCase());
            // 设置标题
            logEntity.setLogTitle(controllerLog.value());
            // 设置操作人类别
            logEntity.setLogUserType(OperatorTypeEnum.MANAGE.name().toLowerCase());
            // 获取参数的信息，传入到数据库中。
            boolean isJson = StringUtils.isNotBlank(SpringUtil.getRequest().getContentType()) && MediaType.valueOf(SpringUtil.getRequest().getContentType()).includes(MediaType.APPLICATION_JSON);
            // 如果是json请求参数需要获取方法体上的参数
            if (isJson) {
                Object jsonParam = getJsonParam(joinPoint);
                if (ObjectUtil.isNotNull(jsonParam)) {
                    String jsonString = JSON.toJSONString(jsonParam, SerializerFeature.PrettyFormat,
                            SerializerFeature.WriteDateUseDateFormat);
                    logEntity.setLogParam(jsonString);
                }
            } else {
                Map<String, String[]> map = SpringUtil.getRequest().getParameterMap();
                String params = JSON.toJSONString(map, SerializerFeature.PrettyFormat,
                        SerializerFeature.WriteDateUseDateFormat);
                logEntity.setLogParam(params);
            }
            logEntity.setCreateDate(new Date());
            setLogUser(logEntity, requestURI);
            logBiz.saveData(logEntity);
        } catch (Exception exp) {
            LOG.error("日志记录错误:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 修改密码后是退出登录的状态,操作人从请求参数获取
     */
    private void setLogUser(LogEntity logEntity, String requestURI) {
        if (CharSequenceUtil.isBlank(logEntity.getLogUser()) && (requestURI.contains("/updatePassword"))) {
            String logParam = logEntity.getLogParam();
            ManagerModifyPwdBean modifyPwdBean = JSON.parseObject(logParam, ManagerModifyPwdBean.class);
            String logUser = CharSequenceUtil.subBetween(modifyPwdBean.getManagerName(), "\"", "\"");
            logEntity.setLogUser(logUser);
        }
    }
}
