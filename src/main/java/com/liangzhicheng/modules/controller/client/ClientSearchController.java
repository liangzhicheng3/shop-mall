package com.liangzhicheng.modules.controller.client;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.config.mvc.resolver.annotation.UserParam;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.KeywordsEntity;
import com.liangzhicheng.modules.entity.SearchHistoryEntity;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.service.IApiKeywordsService;
import com.liangzhicheng.modules.service.IApiSearchHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Api(tags = "搜索")
@RestController
@RequestMapping("/client/search")
public class ClientSearchController extends BaseController {

    @Resource
    private IApiKeywordsService apiKeywordsService;
    @Resource
    private IApiSearchHistoryService apiSearchHistoryService;

    @ApiOperation(value = "搜索商品列表") //index
    @PostMapping("/index")
    public ResponseResult index(@UserParam UserEntity user) {
        Map<String, Object> params = new HashMap<>();
        params.put("isDefault", 1);
        params.put("sortField", "id");
        params.put("sortOrder", "ASC");
        LinkedHashMap<String, Object> keywordsMap = apiKeywordsService.queryList(params);
        List<KeywordsEntity> keywordsList = (List<KeywordsEntity>) keywordsMap.get("records");
        //获取输入框默认的关键字
        KeywordsEntity defaultKeywords = ToolUtil.listSizeGT(keywordsList) ? keywordsList.get(0) : null;
        //获取出热门关键字
        List<KeywordsEntity> hotKeywordsList = apiKeywordsService.list(
                Wrappers.<KeywordsEntity>query()
                        .select("DISTINCT keywords", "is_hot")
                        .orderByAsc("id")
                        .last("LIMIT 10"));
        //获取搜索历史
        List<SearchHistoryEntity> searchHistoryList = apiSearchHistoryService.list(
                Wrappers.<SearchHistoryEntity>query()
                        .select("DISTINCT keywords")
                        .eq(user.getId() != null, "user_id", user.getId())
                        .orderByAsc("id")
                        .last("LIMIT 10"));
        String[] historyKeywordsList = new String[searchHistoryList.size()];
        if(ToolUtil.listSizeGT(searchHistoryList)){
            int i = 0;
            for(Iterator<SearchHistoryEntity> historys = searchHistoryList.iterator(); historys.hasNext();){
                historyKeywordsList[i] = historys.next().getKeywords();
                i++;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("defaultKeywords", defaultKeywords);
        resultMap.put("hotKeywordsList", hotKeywordsList);
        resultMap.put("historyKeywordsList", historyKeywordsList);
        return buildSuccessInfo(resultMap);
    }

    @ApiOperation(value = "搜索商品") //helper
    @PostMapping("/helper")
    @ApiImplicitParams({@ApiImplicitParam(name = "keywords", value = "关键字", required = true, dataType = "String")})
    public ResponseResult helper(@UserParam UserEntity user,
                                 String keywords) {
        List<KeywordsEntity> keywordsList = apiKeywordsService.list(
                Wrappers.<KeywordsEntity>query()
                        .select("DISTINCT keywords")
                        .eq(ToolUtil.isNotBlank(keywords), "keywords", keywords)
                        .last("LIMIT 10"));
        String[] keys = new String[keywordsList.size()];
        if(ToolUtil.listSizeGT(keywordsList)){
            int i = 0;
            for(Iterator<KeywordsEntity> items = keywordsList.iterator(); items.hasNext();){
                keys[i] = items.next().getKeywords();
                i++;
            }
        }
        return buildSuccessInfo(keys);
    }

    @ApiOperation(value = "清空搜索历史")
    @PostMapping("/clear")
    public ResponseResult clear(@UserParam UserEntity user) {
        apiSearchHistoryService.remove(
                Wrappers.<SearchHistoryEntity>lambdaQuery()
                        .eq(user.getId() != null, SearchHistoryEntity::getUserId, user.getId()));
        return buildSuccessInfo();
    }

}
