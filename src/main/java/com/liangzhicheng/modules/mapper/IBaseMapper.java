package com.liangzhicheng.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IBaseMapper<T> extends BaseMapper<T> {

    Integer getTotal(Map<String, Object> params);

    List<T> queryListSelf(Map<String, Object> params);

    T getSelf(@Param("id") Integer id);

}
