package com.liangzhicheng.common.advice;

import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@ControllerAdvice(basePackages = {"com"})
public class SysControllerAdvice extends BaseController {

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public ResponseResult runtimeException(RuntimeException e) {
        String errorMessage = "服务器发生异常，异常信息为：";
        log.error(errorMessage + "{}", this.write(e));
        return buildFailedInfo(errorMessage + e.getMessage());
    }

    @ExceptionHandler({
            TransactionException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class})
    @ResponseBody
    public ResponseResult exception(Exception e) {
        String errorMessage = this.write(e);
        if(e instanceof BindException){
            log.error("参数绑定异常，异常信息为：{}", errorMessage);
            return buildFailedInfo(e.getMessage());
        }else if(e instanceof MissingServletRequestParameterException){
            log.error("参数缺失异常，异常信息为：{}", errorMessage);
            return buildFailedInfo(e.getMessage());
        }else if(e instanceof TypeMismatchException){
            log.error("参数类型异常，异常信息为：{}", errorMessage);
            return buildFailedInfo(e.getMessage());
        }
        log.error("业务处理异常，异常信息为：{}", errorMessage);
        return buildFailedInfo(e.getMessage());
    }

    private String write(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw,true));
        return sw.toString();
    }

}
