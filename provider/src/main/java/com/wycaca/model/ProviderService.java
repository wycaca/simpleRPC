package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.runable.WorkTask;
import com.wycaca.service.ConnectServiceFactory;
import com.wycaca.threadPoolFactory.NamedThreadPoolFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProviderService extends RegisterService {

    private static final ExecutorService workExecutor = Executors.newFixedThreadPool(10, new NamedThreadPoolFactory("work"));

    public ProviderService(int port) throws IOException {
        super(port);
        this.type = Const.PROVIDER;
    }

    /**
     * 向注册中心发送注册连接
     */
    public RegisterResponse doRegister(String registerIp, int registerPort) throws IOException {
        Socket registerCenterSocket = new Socket(registerIp, registerPort);
        connectService = ConnectServiceFactory.get(registerCenterSocket);
        OutputStream outputStream = connectService.getOutPut();
        String url = this.type + "://" + this.ip + ":" + this.port + "/" + this.path
                + "?" + Const.PARAMS_timestamp + System.currentTimeMillis();
        outputStream.write(commonSerializer.serialize(url));
        outputStream.flush();
        outputStream.close();
        registerCenterSocket.close();
        // 生产者, 需要建立socket服务端, 供消费者连接
        creatServerSocket(this.port);
        return RegisterResponse.ok();
    }

    /**
     * 生产者, 建立socket服务端
     * @param port 端口号
     * @return
     */
    private ServerSocket creatServerSocket(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        // 开始监听
        serverSocket.accept();
        while (true) {
            // todo 获取方法名和入参
            String method = "";
            Object param = null;
            // 持续接受消费者消息, 提供服务
            workExecutor.execute(new WorkTask(method, param));
        }
    }
}
