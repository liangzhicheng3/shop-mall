package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.TimeUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.FootprintEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IApiFootprintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = {"足迹"})
@RestController
@RequestMapping(value = "/client/footprint")
public class ClientFootprintController extends BaseController {

    @Resource
    private IApiFootprintService apiFootprintService;

    @ApiOperation(value = "删除足迹")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseResult delete(@UserParam UserEntity user,
                                 @PathVariable("id") Integer id){
        ToolUtil.isFalse(ToolUtil.isNull(user) || user.getId() == null, "删除有误！");
        FootprintEntity footprint = apiFootprintService.get(id);
        ToolUtil.isFalse(ToolUtil.isNull(footprint), "删除有误！");
        apiFootprintService.remove(
                Wrappers.<FootprintEntity>lambdaQuery()
                        .eq(FootprintEntity::getUserId, user.getId())
                        .eq(FootprintEntity::getGoodsId, footprint.getGoodsId()));
        return buildSuccessInfo("删除成功！");
    }

    @ApiOperation(value = "获取足迹列表")
    @PostMapping(value = "/list")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNo", value = "当前页码", defaultValue = "1", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = false, dataType = "Integer")})
    public ResponseResult list(@UserParam UserEntity user,
                               Integer pageNo,
                               Integer pageSize){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        Query query = new Query(params);
        LinkedHashMap<String, Object> queryFootprintMap = apiFootprintService.queryListSelf(query);
        List<FootprintEntity> queryFootprintList = (List<FootprintEntity>) queryFootprintMap.get("records");
        Map<String, List<FootprintEntity>> footprintMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareTo(s1);
            }
        });
        Map<String, Object> resultMap = new HashMap<>();
        if(ToolUtil.listSizeGT(queryFootprintList)){
            for(Iterator<FootprintEntity> it = queryFootprintList.iterator(); it.hasNext();){
                FootprintEntity footprint = it.next();
                String addTime = TimeUtil.formatTime(footprint.getAddTime(), Constants.DATE_PATTERN);
                List<FootprintEntity> tempList = footprintMap.get(addTime);
                if (footprintMap.get(addTime) == null) {
                    tempList = new ArrayList<>();
                }
                tempList.add(footprint);
                footprintMap.put(addTime, tempList);
            }
            List<List<FootprintEntity>> footprintList = new ArrayList<List<FootprintEntity>>();
            for(Map.Entry<String, List<FootprintEntity>> entry : footprintMap.entrySet()){
                footprintList.add(entry.getValue());
            }
            resultMap.put("pageNo", queryFootprintMap.get("pageNo"));
            resultMap.put("pageSize", queryFootprintMap.get("pageSize"));
            resultMap.put("pages", queryFootprintMap.get("pages"));
            resultMap.put("total", queryFootprintMap.get("total"));
            resultMap.put("records", footprintList);
        }
        return buildSuccessInfo(resultMap);
    }

}
