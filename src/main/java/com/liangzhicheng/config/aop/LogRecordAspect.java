package com.liangzhicheng.config.aop;

import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.aop.annotation.LogRecord;
import com.liangzhicheng.modules.entity.LogRecordEntity;
import com.liangzhicheng.modules.service.ILogRecordService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
public class LogRecordAspect {

    @Resource
    private ILogRecordService logRecordService;

    @Pointcut("@annotation(com.liangzhicheng.config.aop.annotation.LogRecord)")
    public void logRecord(){}

    @Before("logRecord()")
    public void saveLogRecord(JoinPoint joinPoint){
        //获取被代理的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogRecord logRecord = signature.getMethod().getAnnotation(LogRecord.class);
        LogRecordEntity log = new LogRecordEntity();
        //获取被代理的方法上的注解中参数值
        log.setOperate(logRecord.operate());
        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.setMethod(className + "." + methodName + "()");
        //请求的参数
        Object[] args = joinPoint.getArgs();
        String params = JSONUtil.toJSONString(args[0]);
        log.setParams(params);
        //获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //设置用户id和ip地址
        log.setUserId(request.getHeader("userId"));
        log.setIp(ToolUtil.getClientUrl(request));
        log.setCreateDate(new Date());
        //保存操作记录日志
        logRecordService.insert(log);
    }

}
