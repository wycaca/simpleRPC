package com.wycaca.service;

import com.wycaca.constant.SystemConst;
import com.wycaca.model.RegisterService;
import com.wycaca.runable.ServiceTask;
import com.wycaca.threadPoolFactory.NamedThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterCenterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterCenterService.class);

    private static final ExecutorService executor = Executors.newFixedThreadPool(10, new NamedThreadPoolFactory("register"));

    // 保存已注册的服务, 服务名: 服务路径
    private static final ConcurrentMap<String, RegisterService> serviceMap = new ConcurrentHashMap<>();

    public void register(String url) {
        RegisterService registerService = null;
        try {
            registerService = new RegisterService(url);
        } catch (Exception e) {
            logger.error("注册服务失败, ", e);
        }
        // 不为null, 未注册过
        if (registerService != null && !serviceMap.containsKey(registerService.getName())) {
            serviceMap.put(registerService.getName(), registerService);
            logger.info("服务: {}, 注册成功, class path: {}", registerService.getName(), registerService.getPath());
        }
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(SystemConst.REGISTER_PORT));
        try {
            // 持续监听 服务 socket
            while (true) {
                executor.execute(new ServiceTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }
}
