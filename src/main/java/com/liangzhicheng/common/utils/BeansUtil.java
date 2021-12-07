package com.liangzhicheng.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;

import java.util.ArrayList;
import java.util.List;

public class BeansUtil {

    public static<T> T copyEntity(Object source, Class<T> cls){
        T target = null;
        if(source != null){
            target = ReflectUtil.newInstance(cls);
            BeanUtil.copyProperties(source, target);
        }
        return target;
    }

    public static<T> List<T> copyList(List<?> source, Class<T> cls) {
        if(source == null || source.size() == 0){
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(source.size());
        T target = null;
        for (Object obj : source) {
            if(obj != null){
                target = ReflectUtil.newInstance(cls);
                BeanUtil.copyProperties(obj, target);
            }
            targetList.add(target);
        }
        return targetList;
    }

}
