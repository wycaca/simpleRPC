package com.wycaca.runable;

import com.wycaca.constant.Const;
import com.wycaca.model.RegisterService;
import com.wycaca.service.RegisterCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class KeepAliveTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(KeepAliveTask.class);

    private final RegisterCenterService registerCenterService;

    public KeepAliveTask(RegisterCenterService registerCenterService) {
        this.registerCenterService = registerCenterService;
    }

    @Override
    public void run() {
//        logger.info("开始检测是否存在心跳异常服务");
        removeService(registerCenterService.getPROVIDER_MAP());
        removeService(registerCenterService.getCONSUMER_MAP());
    }

    // todo 有问题, map传不进来
    private void removeService(ConcurrentMap<String, RegisterService> serviceMap) {
        long now = System.currentTimeMillis();
        Set<Map.Entry<String, RegisterService>> entrySet = serviceMap.entrySet();
        for (Map.Entry<String, RegisterService> entry : entrySet) {
            String key = entry.getKey();
            RegisterService service = entry.getValue();
            long mergeTime = service.getLastPingTimestamp() - now;
            if (mergeTime > Const.TIMEOUT) {
                serviceMap.remove(key);
                logger.info("{} 服务超时, 已移除", service.getRegisterUrl());
            }
        }
    }
}
