package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.impl.SocketImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ConsumerService extends RegisterService {

    private Socket socket;

    public ConsumerService(int port, Class<?> registerClazz) throws IOException {
        super(port, registerClazz);
        url.setProtocol(Const.CONSUMER);
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
    @Override
    public RegisterResponse doRegister(String registerIp, int registerPort) throws IOException {
        super.doRegister(registerIp, registerPort);
        // 如果是消费者, 需要接受返回信息
        ConnectFactory connectFactory = new SocketImpl(new Socket(registerIp, registerPort));
        InputStream inputStream = connectFactory.getInput();
        byte[] bytes = new byte[inputStream.available()];
        return commonSerializer.deserialize(bytes, RegisterResponse.class);
    }
}
