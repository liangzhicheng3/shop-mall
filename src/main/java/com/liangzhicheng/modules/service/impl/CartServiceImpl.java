package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.modules.entity.CartEntity;
import com.liangzhicheng.modules.mapper.ICartMapper;
import com.liangzhicheng.modules.service.ICartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends BaseServiceImpl<ICartMapper, CartEntity> implements ICartService {

}
