package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.service.ConnectServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConsumerService extends RegisterService {

    public ConsumerService(int port) throws IOException {
        super(port);
        this.type = Const.CONSUMER;
    }

    /**
     * 消费者订阅提供者, 建立直接连接
     */
    public Socket doSubscribe(String ip, int port) throws IOException {
        return new Socket(ip, port);
    }

    /**
     * 向注册中心发送注册连接
     */
    public RegisterResponse doRegister(String registerIp, int registerPort) throws IOException {
        Socket registerCenterSocket = new Socket(registerIp, registerPort);
        connectService = ConnectServiceFactory.get(registerCenterSocket);
        OutputStream outputStream = connectService.getOutPut();
        String url = this.type + "://" + this.ip + ":" + this.port + "/" + this.path
                + "?" + Const.PARAMS_TIMESTAMP + System.currentTimeMillis();
        outputStream.write(commonSerializer.serialize(url));
        outputStream.flush();
        outputStream.close();
        registerCenterSocket.close();
        // 如果是消费者, 需要接受返回信息
        InputStream inputStream = connectService.getInput();
        byte[] bytes = new byte[inputStream.available()];
        registerCenterSocket.close();
        return (RegisterResponse) commonSerializer.deserialize(bytes, RegisterResponse.class);
    }
}
