package com.liangzhicheng.modules.service;

import java.util.List;
import java.util.Map;

public interface IApiKdniaoService {

    List<Map<String, Object>> getShippingTrack(String shippingCode, String shippingNo);

}
