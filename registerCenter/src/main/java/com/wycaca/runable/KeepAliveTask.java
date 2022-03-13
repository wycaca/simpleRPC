package com.wycaca.runable;

import com.wycaca.constant.SystemConst;
import com.wycaca.model.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KeepAliveTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(KeepAliveTask.class);
    // 提供者
    private static final ConcurrentMap<String, RegisterService> providerMap = new ConcurrentHashMap<>();
    // 消费者
    private static final ConcurrentMap<String, RegisterService> consumerMap = new ConcurrentHashMap<>();

    @Override
    public void run() {
        logger.info("开始检测是否存在心跳异常服务");
        removeService(providerMap);
        removeService(consumerMap);
    }

    private void removeService(ConcurrentMap<String, RegisterService> serviceMap) {
        long now = System.currentTimeMillis();
        Set<Map.Entry<String, RegisterService>> entrySet = serviceMap.entrySet();
        for (Map.Entry<String, RegisterService> entry : entrySet) {
            String key = entry.getKey();
            RegisterService service = entry.getValue();
            long mergeTime = service.getLastPingTimestamp() - now;
            if (mergeTime > SystemConst.TIMEOUT) {
                serviceMap.remove(key);
                logger.info("{} 服务超时, 已移除", service.getRegisterUrl());
            }
        }
    }
}