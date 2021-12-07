package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.SearchHistoryEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.ISearchHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/server/search/history")
public class ServerSearchHistoryController extends BaseController {

    @Resource
    private ISearchHistoryService searchHistoryService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody SearchHistoryEntity searchHistory){
        int rows = searchHistoryService.insert(searchHistory);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        searchHistoryService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody SearchHistoryEntity searchHistory){
        searchHistoryService.update(searchHistory);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(searchHistoryService.queryListSelf(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(searchHistoryService.get(id));
    }

}
