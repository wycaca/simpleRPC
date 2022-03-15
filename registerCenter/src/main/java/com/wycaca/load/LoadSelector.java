package com.wycaca.load;

import com.wycaca.model.RegisterService;

import java.util.concurrent.ConcurrentMap;

public interface LoadSelector {
    /**
     * 负载均衡 选择接口
     * @return
     */
    public RegisterService select(ConcurrentMap<String, RegisterService> serviceMap);
}
