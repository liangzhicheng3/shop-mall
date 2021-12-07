package com.liangzhicheng.modules.controller.server;

import com.liangzhicheng.common.constant.ApiConstant;
import com.liangzhicheng.common.response.ResponseResult;
import com.liangzhicheng.modules.controller.basic.BaseController;
import com.liangzhicheng.modules.entity.OrderGoodsEntity;
import com.liangzhicheng.modules.entity.query.Query;
import com.liangzhicheng.modules.service.IOrderGoodsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/server/order/goods")
public class ServerOrderGoodsController extends BaseController {

	@Resource
	private IOrderGoodsService orderGoodsService;

	@PostMapping(value = "/save")
	public ResponseResult save(@RequestBody OrderGoodsEntity orderGoods){
		int rows = orderGoodsService.insert(orderGoods);
		if(rows > 0){
			return buildSuccessInfo();
		}
		return buildFailedInfo(ApiConstant.BASE_FAIL_CODE);
	}

	@PostMapping(value = "/delete")
	public ResponseResult delete(@RequestParam String ids){
		orderGoodsService.delete(ids);
		return buildSuccessInfo();
	}

	@PostMapping(value = "/update")
	public ResponseResult update(@RequestBody OrderGoodsEntity orderGoods){
		orderGoodsService.update(orderGoods);
		return buildSuccessInfo();
	}

	@PostMapping(value = "/list")
	public ResponseResult list(@RequestParam Map<String, Object> params){
		Query query = new Query(params);
		return buildSuccessInfo(orderGoodsService.queryList(query));
	}

	@GetMapping(value = "/get/{id}")
	public ResponseResult get(@PathVariable("id") Integer id){
		return buildSuccessInfo(orderGoodsService.get(id));
	}

}
