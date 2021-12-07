package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.UserEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/server/user")
public class ServerUserController extends BaseController {

    @Resource
    private IUserService userService;

    @PostMapping(value = "/save")
    public ResponseResult save(@RequestBody UserEntity user){
        int rows = userService.insert(user);
        if(rows > 0){
            return buildSuccessInfo();
        }
        return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam String ids){
        userService.delete(ids);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody UserEntity user){
        userService.update(user);
        return buildSuccessInfo();
    }

    @PostMapping(value = "/list")
    public ResponseResult list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        return buildSuccessInfo(userService.queryListSelf(query));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseResult get(@PathVariable("id") Integer id){
        return buildSuccessInfo(userService.get(id));
    }

}
