package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.demo.ProviderServiceDemo;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.runable.WorkTask;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.impl.SocketImpl;
import com.wycaca.threadPoolFactory.NamedThreadPoolFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

    public ProviderService(int port) throws IOException {
        super(port);
        this.type = Const.PROVIDER;
        // 通过反射, 获取提供者服务的一些属性
        Class<ProviderServiceDemo> providerServiceDemoClazz = ProviderServiceDemo.class;
        String clazzName = providerServiceDemoClazz.getName();
        this.setName(clazzName);
        this.setPath(clazzName);
        // 获取所有method名
        Method[] methods = providerServiceDemoClazz.getDeclaredMethods();
        List<String> methodNameList = new ArrayList<>();
        for (Method method : methods) {
            methodNameList.add(method.getName());
        }
        this.setMethods(methodNameList);
        this.setRegisterTimestamp(System.currentTimeMillis());
        this.url = this.type + "://" + this.ip + ":" + this.port + "/" + this.path
                + "?" + Const.PARAMS_TIMESTAMP + "=" + System.currentTimeMillis() + "&"
                + Const.PARAMS_METHODS + "=" + this.getMethodNameStr();
    }

    /**
     * 向注册中心发送注册连接
     */
    public RegisterResponse doRegister(String registerIp, int registerPort) throws IOException {
        // 开启客户端, 连接注册中心的服务器Socket
        ConnectFactory connectFactory = new SocketImpl(new Socket(registerIp, registerPort));
        OutputStream outputStream = connectFactory.getOutPut();
        outputStream.write(commonSerializer.serialize(url));
        outputStream.flush();
        outputStream.close();
        // 生产者, 需要建立socket服务端, 供消费者连接
        start(this.port);
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
        // 开始监听
        serverSocket.accept();
        while (true) {
            logger.info("服务{} 生产者启动成功, 端口号: {}", this.name, port);
            // todo 获取方法名和入参
            String method = "";
            Object param = null;
            // 持续接受消费者消息, 提供服务
            workExecutor.execute(new WorkTask(method, param));
        }
    }
}
