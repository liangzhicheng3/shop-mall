package com.liangzhicheng.config.aop;

import com.liangzhicheng.common.bean.RedisBean;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.utils.JSONUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.aop.annotation.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 需要测试
 */
@Aspect
@Component
public class CacheAspect {

    @Resource
    private RedisBean redisBean;

    @Pointcut("@annotation(com.liangzhicheng.config.aop.annotation.Cache)")
    public void cache() {
    }

    @Around("cache()")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取被代理的方法
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new TransactionException("发生未知错误！");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = joinPoint
                .getTarget()
                .getClass()
                .getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        //获取类名、方法名和参数
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        //获取被代理的方法上的注解中参数值
        String key = method.getAnnotation(Cache.class).key();
        boolean assign = method.getAnnotation(Cache.class).assign();
        String cacheKey = key;
        if(!assign){
            //根据类名，方法名生成key
            cacheKey = ToolUtil.generateCacheKey(key, className, methodName);
        }
        Object result;
        String redisResult;
        if(!redisBean.hasKey(cacheKey)){
            //缓存不存在，则调用原方法，并将结果放入缓存中
            result = joinPoint.proceed(args);
            redisResult = JSONUtil.toJSONString(result);
            redisBean.set(cacheKey, redisResult);
        }else{
            //缓存命中
            redisResult = redisBean.get(cacheKey);
            //获取被代理方法的返回值类型
            Class<?> returnType = method.getReturnType();
            result = JSONUtil.parseObject(redisResult, returnType);
        }
        return result;
    }

}
