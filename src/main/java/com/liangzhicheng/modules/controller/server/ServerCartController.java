package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.CartEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.ICartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/server/cart")
public class ServerCartController extends BaseController {

	@Resource
	private ICartService cartService;

	@PostMapping(value = "/save")
	public ResponseResult save(@RequestBody CartEntity cart){
		int rows = cartService.insert(cart);
		if(rows > 0){
			return buildSuccessInfo();
		}
		return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
	}

	@PostMapping(value = "/delete")
	public ResponseResult delete(@RequestParam String ids){
		cartService.delete(ids);
		return buildSuccessInfo();
	}

	@PostMapping(value = "/update")
	public ResponseResult update(@RequestBody CartEntity cart){
		cartService.update(cart);
		return buildSuccessInfo();
	}

	@PostMapping(value = "/list")
	public ResponseResult list(@RequestParam Map<String, Object> params){
		Query query = new Query(params);
		return buildSuccessInfo(cartService.queryListSelf(query));
	}

	@GetMapping(value = "/get/{id}")
	public ResponseResult get(@PathVariable("id") Integer id){
		return buildSuccessInfo(cartService.get(id));
	}

}
