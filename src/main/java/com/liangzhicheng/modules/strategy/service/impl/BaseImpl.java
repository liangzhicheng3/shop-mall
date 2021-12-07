package com.liangzhicheng.modules.strategy.service.impl;

import com.liangzhicheng.common.bean.AreaBean;
import com.liangzhicheng.modules.entity.AreaCodeEntity;
import com.liangzhicheng.modules.entity.AreaNameEntity;
import com.liangzhicheng.modules.strategy.Area;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class BaseImpl {

    @Resource
    private AreaBean areaBean;

    public List<Map<String, Object>> list(Area area) {
        String type = area.getType();
        String id = area.getId();
        List<AreaCodeEntity> codeList = areaBean.getCodeList();
        List<AreaNameEntity> nameList = areaBean.getNameList();
        List<Map<String, Object>> codeListMap = new ArrayList<>(codeList.size());
        List<Map<String, Object>> nameListMap = new ArrayList<>(nameList.size());
        for(Iterator<AreaCodeEntity> codes = codeList.iterator(); codes.hasNext();){
            AreaCodeEntity code = codes.next();
            Integer areaLevel = code.getAreaLevel();
            String areaId = code.getAreaId();
            if("country".equals(type)
                    && areaLevel == 0){
                this.handleArea(code, codeListMap);

            }else if("province".equals(type)
                    && areaLevel == 1
                    && areaId.substring(0, 3).equals(id)){
                this.handleArea(code, codeListMap);

            }else if("city".equals(type)
                    && areaLevel == 2
                    && areaId.substring(0, 5).equals(id)){
                this.handleArea(code, codeListMap);

            }else if("district".equals(type)
                    && areaLevel == 3
                    && areaId.substring(0, 7).equals(id)){
                this.handleArea(code, codeListMap);
            }
        }
        for(Map<String, Object> map : codeListMap){
            String areaCode = map.get("areaCode").toString();
            for(Iterator<AreaNameEntity> names = nameList.iterator(); names.hasNext();){
                AreaNameEntity name = names.next();
                if(areaCode.equals(name.getAreaCode())){
                    Map<String, Object> nameMap = new HashMap<>();
                    nameMap.put("id", map.get("areaId"));
                    nameMap.put("name", name.getAreaName());
                    nameListMap.add(nameMap);
                }
            }
        }
        return nameListMap;
    }

    private void handleArea(AreaCodeEntity code, List<Map<String, Object>> list){
        Map<String, Object> codeMap = new HashMap<>();
        codeMap.put("areaId", code.getAreaId());
        codeMap.put("areaCode", code.getAreaCode());
        list.add(codeMap);
    }

}
