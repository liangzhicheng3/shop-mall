package com.liangzhicheng.modules.controller.client;

import com.liangzhicheng.common.oss.basic.OSSFactory;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "文件上传")
@RestController
@RequestMapping("/client")
public class ClientUploadController extends BaseController {

    @ApiOperation(value = "上传")
    @PostMapping(value = "/upload")
    public ResponseResult upload(@RequestParam("file") MultipartFile file) throws Exception {
        if(file.isEmpty()){
            return buildFailedInfo("文件上传不能为空");
        }
        String url = OSSFactory.build().upload(file);
        log.info("[文件上传] response url：{}", url);
        return buildSuccessInfo(url);
    }

}
