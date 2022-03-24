package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.impl.SocketImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConsumerService extends RegisterService {

    private Socket socket;

    public ConsumerService(int port) throws IOException {
        super(port);
        this.type = Const.CONSUMER;
    }

    /**
     * 消费者订阅提供者, 建立直接连接
     */
    public void doSubscribe(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
    }

    /**
     * 向注册中心发送注册连接
     */
    public RegisterResponse doRegister(String registerIp, int registerPort) throws IOException {
        // 开启客户端, 连接注册中心的服务器Socket
        ConnectFactory connectFactory = new SocketImpl(new Socket(registerIp, registerPort));
        OutputStream outputStream = connectFactory.getOutPut();
        String url = this.type + "://" + this.ip + ":" + this.port + "/" + this.path
                + "?" + Const.PARAMS_TIMESTAMP + "=" + System.currentTimeMillis();
        outputStream.write(commonSerializer.serialize(url));
        outputStream.flush();
        outputStream.close();
        // 如果是消费者, 需要接受返回信息
        InputStream inputStream = connectFactory.getInput();
        byte[] bytes = new byte[inputStream.available()];
        return commonSerializer.deserialize(bytes, RegisterResponse.class);
    }
}
