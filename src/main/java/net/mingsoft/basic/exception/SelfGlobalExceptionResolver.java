package net.mingsoft.basic.exception;

import com.alibaba.fastjson.JSONObject;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author 1y
 * @date 2022/11/8
 */
@ControllerAdvice
public class SelfGlobalExceptionResolver extends GlobalExceptionResolver {

    @ExceptionHandler(value = BusinessException.class)
    public ModelAndView handleBusinessException(HttpServletRequest request, HttpServletResponse response, BusinessException e) {
        LOG.debug("handleBusinessException");
        response.setStatus(e.getCode().value());
        return render(request, response, ResultData.build().code(e.getCode()).data(e.getData()).msg(e.getMsg()), e);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ModelAndView handleArgValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e) {
        LOG.debug("handleArgValidException");
        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(null).msg(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()), e);
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        LOG.debug("handleException");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR).msg("请求失败"), e);
    }

    @Override
    @ExceptionHandler(BindException.class)
    public ModelAndView handleValidExceptionHandler(HttpServletRequest request, HttpServletResponse response, BindException e) {
        LOG.debug("handleValidExceptionHandler");
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append("字段").append(error.getField()).append(" 参数值有误").append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return render(request, response, ResultData.build().code(HttpStatus.NOT_ACCEPTABLE).msg(message.toString()), e);
    }

    private ModelAndView render(HttpServletRequest request, HttpServletResponse response, ResultData resultData, Exception e) {
//        Map map = new HashMap();
//        map.put("cls", e.getStackTrace()[0] + ""); //出错的类
//        map.put("url", request.getServletPath()); //请求地址
//        map.put("code", ErrorCodeEnum.CLIENT_REQUEST);
//        map.put("result", false);
//        map.put("msg", message.toString());
//        map.put("exc", e.getClass()); //详细异常信息
        LOG.debug("url: {}", request.getRequestURI());
        LOG.error("全局异常ExceptionHandler", e);
        if (BasicUtil.isAjaxRequest(request)) {
            try {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSONObject.toJSONString(resultData));
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            return new ModelAndView("/error/index", resultData);
        }

        return null;
    }
}
