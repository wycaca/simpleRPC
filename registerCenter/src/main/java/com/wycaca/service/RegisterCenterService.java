package com.wycaca.service;

import com.wycaca.constant.SystemConst;
import com.wycaca.model.RegisterService;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.runable.KeepAliveTask;
import com.wycaca.runable.ServiceRegisterTask;
import com.wycaca.threadPoolFactory.NamedThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.*;

public class RegisterCenterService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterCenterService.class);

    private static final ExecutorService registerExecutor = Executors.newFixedThreadPool(10, new NamedThreadPoolFactory("register"));

    private static final ScheduledExecutorService keepAliveExecutor = Executors.newScheduledThreadPool(2, new NamedThreadPoolFactory("keep_alive"));

    // 保存已注册的服务, <服务类路径, <服务注册url, 服务对象>>
    // com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService
    private static final ConcurrentMap<String, ConcurrentMap<String, RegisterService>> providerPathMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, RegisterService> providerMap = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, Map<String, RegisterService>> consumerPathMap = new ConcurrentHashMap<>();
    // consumer://10.10.6.145/com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService?application=cti-link-agent-gateway&application.version=0.0.1&category=consumers&check=false&default.check=false&default.version=0.0.1&dubbo=2.8.4&interface=com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService&methods=pauseTask,start,listAgentTask,pause&pid=30269&revision=0.0.1&side=consumer&timestamp=1646892725284
    private static final ConcurrentMap<String, RegisterService> consumerMap = new ConcurrentHashMap<>();

    public RegisterResponse register(String url) {
        RegisterService registerService = null;
        try {
            registerService = new RegisterService(url);
        } catch (Exception e) {
            logger.error("注册服务失败, ", e);
            return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "注册服务失败, " + url);
        }
        // 区分提供者, 消费者
        // 如果是提供者加入注册服务
        if (SystemConst.PROVIDER.equals(registerService.getType())) {
            // 已存在, 则更新 心跳时间
            if (providerPathMap.containsKey(registerService.getPath())) {
                registerService = providerPathMap.get(registerService.getPath()).get(registerService.getRegisterUrl());
                registerService.setLastPingTimestamp(System.currentTimeMillis());
            }
            providerMap.put(registerService.getRegisterUrl(), registerService);
            providerPathMap.put(registerService.getPath(), providerMap);
            logger.info("生产者服务: {}, 注册/心跳成功, class path: {}", registerService.getName(), registerService.getPath());
            return RegisterResponse.ok();
        }
        // 如果是消费者, 查找提供者
        else if (SystemConst.CONSUMER.equals(registerService.getType())) {
            // 如果有对应的提供者, 返回socket连接信息, 使提供者和消费者直接建立连接
            if (consumerPathMap.containsKey(registerService.getPath())) {
                // 选择一个生产者, 目前直接find any, 随机?
                // todo 负载均衡策略
                RegisterService providerService = providerPathMap.get(registerService.getPath()).values().stream().findAny().get();
                // 注册消费者
                consumerMap.put(registerService.getRegisterUrl(), registerService);
                consumerPathMap.put(registerService.getPath(), consumerMap);
                logger.info("消费者服务: {}, 注册成功", registerService.getPath());
                return RegisterResponse.ok(providerService.getIp() + ":" + providerService.getPort());
            } else {
                // 没有, 直接返回错误信息
                return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未找到可用生产者, " + registerService.getName());
            }
        } else {
            return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "注册服务类型错误");
        }
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(SystemConst.REGISTER_PORT));
        try {
            logger.info("注册中心启动成功, 端口号: {}", SystemConst.REGISTER_PORT);
            // 持续监听 服务 socket
            while (true) {
                // 心跳检测线程池
                logger.info("注册中心 心跳检测线程池 已启动");
                keepAliveExecutor.scheduleWithFixedDelay(new KeepAliveTask(), SystemConst.SCAN_TIME, SystemConst.SCAN_TIME, TimeUnit.SECONDS);
                // 服务发现线程池
                logger.info("注册中心 服务发现线程池 已启动");
                registerExecutor.execute(new ServiceRegisterTask(serverSocket.accept()));
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
