package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IBaseService<T> extends IService<T> {

    int insert(T entity);

    int delete(String ids);

    int update(T entity);

    LinkedHashMap<String, Object> queryList(Map<String, Object> params);

    LinkedHashMap<String, Object> queryListSelf(Map<String, Object> params);

    T get(Integer id);

    T getSelf(Integer id);

}
