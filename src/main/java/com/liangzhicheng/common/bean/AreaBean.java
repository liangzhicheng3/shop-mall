package com.liangzhicheng.common.bean;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.modules.entity.AreaCodeEntity;
import com.liangzhicheng.modules.entity.AreaNameEntity;
import com.liangzhicheng.modules.service.IApiAreaCodeService;
import com.liangzhicheng.modules.service.IApiAreaNameService;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Data
@Component
public class AreaBean implements InitializingBean { //启动初始化地区信息

    @Resource
    private IApiAreaCodeService apiAreaCodeService;
    @Resource
    private IApiAreaNameService apiAreaNameService;

    private List<AreaCodeEntity> codeList;
    private List<AreaNameEntity> nameList;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.init();
    }

    private void init(){
        codeList = apiAreaCodeService.list(Wrappers.<AreaCodeEntity>lambdaQuery());
        nameList = apiAreaNameService.list(
                Wrappers.<AreaNameEntity>lambdaQuery()
                        .select(AreaNameEntity::getAreaCode,
                                AreaNameEntity::getAreaName)
                        .eq(AreaNameEntity::getLang, "zh_CN"));
    }

}
