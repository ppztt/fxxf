package net.mingsoft.basic.exception;

import com.alibaba.fastjson.JSONObject;
import net.mingsoft.base.entity.ResultData;
import net.mingsoft.basic.util.BasicUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
        return render(request, response, ResultData.build().code(e.getCode()).data(e.getData()).msg("请求失败"), e);
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        LOG.debug("handleException");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return render(request, response, ResultData.build().code(HttpStatus.INTERNAL_SERVER_ERROR)
                        .msg("请求失败")
                , e);
    }

    private ModelAndView render(HttpServletRequest request, HttpServletResponse response, ResultData resultData, Exception e) {
//        Map map = new HashMap();
//        map.put("cls", e.getStackTrace()[0] + ""); //出错的类
//        map.put("url", request.getServletPath()); //请求地址
//        map.put("code", ErrorCodeEnum.CLIENT_REQUEST);
//        map.put("result", false);
//        map.put("msg", message.toString());
//        map.put("exc", e.getClass()); //详细异常信息
        LOG.debug("url: {}",request.getRequestURI());
        e.printStackTrace();
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
