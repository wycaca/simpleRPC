package com.wycaca.service;

import com.wycaca.constant.SystemConst;
import com.wycaca.model.RegisterService;
import com.wycaca.runable.ServiceRegisterTask;
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

    private static final ExecutorService registerExecutor = Executors.newFixedThreadPool(10, new NamedThreadPoolFactory("register"));

    private static final ExecutorService keepAliveExecutor = Executors.newFixedThreadPool(10, new NamedThreadPoolFactory("keep_alive"));

    // 保存已注册的服务, 服务名: 服务路径
    private static final ConcurrentMap<String, RegisterService> providerMap = new ConcurrentHashMap<>();

    public String register(String url) {
        RegisterService registerService = null;
        try {
            registerService = new RegisterService(url);
        } catch (Exception e) {
            logger.error("注册服务失败, ", e);
        }
        // 不为null, 未注册过
        if (registerService == null) {
            return "注册服务失败, " + url;
        }
        // 区分提供者, 消费者
        // 如果是提供者加入注册服务
        if (SystemConst.PROVIDER.equals(registerService.getType())) {
            providerMap.put(registerService.getName(), registerService);
            logger.info("生产者服务: {}, 注册成功, class path: {}", registerService.getName(), registerService.getPath());
            return "生产者服务: " + registerService.getName() + ", 注册成功";
        }
        // 如果是消费者, 查找提供者
        else if (SystemConst.CONSUMER.equals(registerService.getType())) {
            logger.info("消费者服务: {}, 注册成功, 可用生产者 path: {}", registerService.getName(), registerService.getPath());
            // 如果有对应的提供者, 返回socket连接信息, 使提供者和消费者直接建立连接
            if (providerMap.containsKey(registerService.getName())) {
                return registerService.getIp() + ":" + registerService.getPort();
            } else {
                // 没有, 直接返回错误信息
                return "未找到可用生产者";
            }
        } else {
            return "注册服务类型错误";
        }
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(SystemConst.REGISTER_PORT));
        try {
            logger.info("注册中心启动成功, 端口号: {}", SystemConst.REGISTER_PORT);
            // 持续监听 服务 socket
            while (true) {
                // 服务发现线程池
                registerExecutor.execute(new ServiceRegisterTask(serverSocket.accept()));
                // todo 心跳检测线程池
            }
        } finally {
            serverSocket.close();
        }
    }

    public static void main(String[] args) {
        RegisterCenterService registerCenterService = new RegisterCenterService();
        try {
            registerCenterService.start();
        } catch (IOException e) {
            logger.error("注册中心异常: ", e);
        }
    }
}
