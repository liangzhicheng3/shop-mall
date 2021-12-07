package com.liangzhicheng.modules.service.impl;

import com.liangzhicheng.common.utils.BeansUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import com.liangzhicheng.modules.entity.CouponUserEntity;
import com.liangzhicheng.modules.entity.vo.CouponUserVO;
import com.liangzhicheng.modules.mapper.IApiCouponUserMapper;
import com.liangzhicheng.modules.service.IApiCouponUserService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ApiCouponUserServiceImpl extends BaseServiceImpl<IApiCouponUserMapper, CouponUserEntity> implements IApiCouponUserService {

    @Override
    public List<CouponUserVO> getCouponUser(Map<String, Object> params) {
        //校验优惠券是否过期
        List<CouponUserEntity> couponUserList = baseMapper.getCouponUser(params);
        if(ToolUtil.listSizeGT(couponUserList)){
            for(Iterator<CouponUserEntity> it = couponUserList.iterator(); it.hasNext();){
                CouponUserEntity couponUser = it.next();
                if(couponUser.getCouponStatus() == 1){
                    //检查是否过期
                    if(couponUser.getUseEndDate().before(new Date())){
                        couponUser.setCouponStatus(3);
                        baseMapper.updateById(couponUser);
                    }
                }
                if(couponUser.getCouponStatus() == 3){
                    //检查是否不过期
                    if(couponUser.getUseEndDate().after(new Date())){
                        couponUser.setCouponStatus(1);
                        baseMapper.updateById(couponUser);
                    }
                }
            }
            return BeansUtil.copyList(couponUserList, CouponUserVO.class);
        }
        return null;
    }

}
