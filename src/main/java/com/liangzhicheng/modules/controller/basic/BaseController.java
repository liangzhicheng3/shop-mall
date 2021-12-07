package com.liangzhicheng.modules.controller.basic;

import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.common.constant.ApiConstant;

public abstract class BaseController {

    protected ResponseResult buildSuccessInfo() {
        return this.buildSuccessInfo(null);
    }

    protected ResponseResult buildSuccessInfo(Object resultData) {
        return new ResponseResult(ApiConstant.BASE_SUCCESS_CODE, ApiConstant.getMessage(ApiConstant.BASE_SUCCESS_CODE), resultData);
    }

    protected ResponseResult buildFailedInfo(int errorCode) {
        return new ResponseResult(errorCode, ApiConstant.getMessage(errorCode), null);
    }

    protected ResponseResult buildFailedInfo(int errorCode, String appendMsg) {
        return new ResponseResult(errorCode, appendMsg, null);
    }

    protected ResponseResult buildFailedInfo(String errorMsg) {
        return new ResponseResult(ApiConstant.BASE_FAIL_CODE, errorMsg, null);
    }

}
