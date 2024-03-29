package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.runable.WorkTask;
import com.wycaca.threadPoolFactory.NamedThreadPoolFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class ProviderService extends RegisterService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    private static final ExecutorService workExecutor = Executors.newFixedThreadPool(10, new NamedThreadPoolFactory("work"));

    /**
     * 提供者本身的socket, 用于接收消费者的请求
     */
    private ServerSocket serverSocket;
    private List<String> methods;

    private String getMethodNameStr() {
        StringBuilder methods = new StringBuilder();
        for (int i = 0; i < this.methods.size(); i++) {
            methods.append(this.methods.get(i));
            // 如果不是最后一个元素, 加上 , 分隔
            if (i != this.methods.size() - 1) {
                methods.append(",");
            }
        }
        return methods.toString();
    }

    public ProviderService(int port, Class<?> registerClazz) throws IOException {
        super(port, registerClazz);
        url.setProtocol(Const.PROVIDER);
        // 通过反射, 获取提供者服务的一些属性
        // 获取所有method名
        Method[] methods = registerClazz.getDeclaredMethods();
        List<String> methodNameList = new ArrayList<>();
        for (Method method : methods) {
            methodNameList.add(method.getName());
        }
        this.setMethods(methodNameList);
    }

    /**
     * 向注册中心发送注册连接
     */
    @Override
    public RegisterResponse doRegister(String registerIp, int registerPort) {
        super.doRegister(registerIp, registerPort);
        try {
            this.registerSocket.close();
        } catch (IOException e) {
            logger.error("关闭 提供者 注册 客户端失败, ", e);
        }
        // 生产者, 需要建立socket服务端, 供消费者连接
        try {
            start(this.url.getPort());
        } catch (IOException e) {
            logger.error("启动提供者失败, ", e);
        }
        return RegisterResponse.ok();
    }

    /**
     * 生产者, 建立socket服务端
     *
     * @param port 端口号
     * @return
     */
    private void start(int port) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        while (true) {
            logger.info("服务{} 提供者启动成功, 端口号: {}", this.url.getPath(), port);
            // 持续接受消费者消息, 提供服务
            workExecutor.execute(new WorkTask(serverSocket.accept()));
        }
    }
}
