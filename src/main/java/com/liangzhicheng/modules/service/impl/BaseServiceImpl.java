package com.liangzhicheng.modules.service.impl;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.mapper.IBaseMapper;
import com.liangzhicheng.modules.service.IBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public abstract class BaseServiceImpl<M extends IBaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(T entity) {
        return baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(String ids) {
        int rows = 0;
        if(ToolUtil.isNotNull(ids)){
            if(ids.contains(",")){
                StringTokenizer tokenizer = new StringTokenizer(ids, ",");
                while(tokenizer.hasMoreElements()){
                    rows += this.delete(Integer.parseInt(tokenizer.nextToken()));
                }
            }else{
                rows = this.delete(Integer.parseInt(ids));
            }
        }
        return rows;
    }

    public int delete(Integer id) {
        T entity = this.get(id);
        if(ToolUtil.isNotNull(entity)){
            return baseMapper.deleteById(entity);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(T entity) {
        return baseMapper.updateById(entity);
    }

    @Override
    public LinkedHashMap<String, Object> queryList(Map<String, Object> params) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        Integer userId = MapUtil.getInt(params, "userId");
        wrapper.eq(ToolUtil.isNotNull(userId), "user_id", userId);

        Integer sessionId = MapUtil.getInt(params, "sessionId");
        wrapper.eq(ToolUtil.isNotNull(sessionId), "session_id", sessionId);

        Integer brandId = MapUtil.getInt(params, "brandId");
        wrapper.eq(ToolUtil.isNotNull(brandId), "brand_id", brandId);

        Integer parentId = MapUtil.getInt(params, "parentId");
        wrapper.eq(ToolUtil.isNotNull(parentId), "parent_id", parentId);

        Integer orderId = MapUtil.getInt(params, "orderId");
        wrapper.eq(ToolUtil.isNotNull(orderId), "order_id", orderId);

        String keywords = MapUtil.getStr(params, "keywords");
        wrapper.like(ToolUtil.isNotNull(keywords), "keywords", keywords);

        String name = MapUtil.getStr(params, "name");
        wrapper.like(ToolUtil.isNotNull(name), "name", name);

        Integer isNew = MapUtil.getInt(params, "isNew");
        wrapper.eq(ToolUtil.isNotNull(isNew), "is_new", isNew);

        Integer isHot = MapUtil.getInt(params, "isHot");
        wrapper.eq(ToolUtil.isNotNull(isHot), "is_hot", isHot);

        Integer isOnSale = MapUtil.getInt(params, "isOnSale");
        wrapper.eq(ToolUtil.isNotNull(isOnSale), "is_on_sale", isOnSale);

        Integer isDelete = MapUtil.getInt(params, "isDelete");
        wrapper.eq(ToolUtil.isNotNull(isDelete), "is_delete", isDelete);

        Integer isDefault = MapUtil.getInt(params, "isDefault");
        wrapper.eq(ToolUtil.isNotNull(isDefault), "is_default", isDefault);

        Integer checked = MapUtil.getInt(params, "checked");
        wrapper.eq(ToolUtil.isNotNull(checked), "checked", checked);

        Boolean enabled = MapUtil.getBool(params, "enabled");
        if(ToolUtil.isNotNull(enabled)){
            Date currentTime = new Date();
            wrapper.gt(enabled, "use_end_date", currentTime)
                    .lt(enabled, "use_start_date", currentTime);
        }

        Object ids = MapUtil.get(params, "ids", Object.class);
        wrapper.in(ToolUtil.isNotNull(ids), "id", (List<Integer>) ids);

        Object categoryIds = MapUtil.get(params, "categoryIds", Object.class);
        wrapper.in(ToolUtil.isNotNull(categoryIds), "category_id", (List<Integer>) categoryIds);

        Object sendType = MapUtil.get(params, "sendType", Object.class);
        wrapper.in(ToolUtil.isNotNull(sendType), "send_type", (Integer[]) sendType);

        String sortField = MapUtil.getStr(params, "sortField");
        String sortOrder = MapUtil.getStr(params, "sortOrder");
        if(ToolUtil.isNotNull(sortField) && ToolUtil.isNotNull(sortOrder)){
            switch(sortOrder){
                case "DESC":
                    wrapper.orderByDesc(sortField);
                    break;
                case "ASC":
                    wrapper.orderByAsc(sortField);
                    break;
                default:
                    break;
            }
        }else{
            wrapper.orderByDesc("id");
        }
        List<T> list = baseMapper.selectList(wrapper);
        return this.pageResult(new PageInfo<>(list));
    }

    @Override
    public LinkedHashMap<String, Object> queryListSelf(Map<String, Object> params) {
        List<T> list = baseMapper.queryListSelf(params);
        return this.pageResult(new PageInfo<>(list));
    }

    @Override
    public T get(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    public T getSelf(Integer id){
        return baseMapper.getSelf(id);
    }

    protected LinkedHashMap<String, Object> pageResult(PageInfo<T> pageInfo){
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>(5);
        resultMap.put("pageNo", pageInfo.getPageNum());
        resultMap.put("pageSize", pageInfo.getPageSize());
        resultMap.put("pages", pageInfo.getPages());
        resultMap.put("total", (int) pageInfo.getTotal());
        resultMap.put("records", pageInfo.getList());
        return resultMap;
    }

}
