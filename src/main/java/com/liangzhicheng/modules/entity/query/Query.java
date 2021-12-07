package com.liangzhicheng.modules.entity.query;

import com.github.pagehelper.PageHelper;
import com.liangzhicheng.common.utils.ToolUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Query extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNo;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    public Query(Map<String, Object> params) {
        this.putAll(params);
        //分页参数
        Object pageNoStr = params.get("pageNo");
        Object pageSizeStr = params.get("pageSize");
        if(ToolUtil.isNotNull(pageNoStr)
                && ToolUtil.isNotNull(pageSizeStr)){
            this.pageNo = Integer.parseInt(pageNoStr.toString());
            this.pageSize = Integer.parseInt(pageSizeStr.toString());
            this.put("currentNo", (pageNo - 1) * pageSize);
            this.put("pageNo", pageNo);
            this.put("pageSize", pageSize);
        }else{
            this.pageNo = 1;
            this.put("currentNo", 0);
            this.put("pageNo", 1);
            this.put("pageSize", pageSize);
        }
        //分页设置
        PageHelper.startPage(pageNo, pageSize);
        //防止SQL注入（因为sortField、sortOrder是通过拼接SQL实现排序，会有SQL注入风险）
        Object sortField = params.get("sortField");
        Object sortOrder = params.get("sortOrder");
        if(ToolUtil.isNotNull(sortField)
                && ToolUtil.isNotNull(sortOrder)){
            this.put("sortField", ToolUtil.sqlInject(sortField.toString()));
            this.put("sortOrder", ToolUtil.sqlInject(sortOrder.toString()));
        }
    }

}
