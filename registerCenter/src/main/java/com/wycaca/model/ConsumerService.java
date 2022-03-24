package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.impl.SocketImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ConsumerService extends RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

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
    public RegisterResponse doRegister(String registerIp, int registerPort) {
        super.doRegister(registerIp, registerPort);
        // 如果是消费者, 需要接受返回信息
        ConnectFactory connectFactory = null;
        try {
            connectFactory = new SocketImpl(new Socket(registerIp, registerPort));
        } catch (IOException e) {
            logger.error("消费者连接注册中心失败", e);
            return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "消费者连接注册中心失败");
        }
        try (InputStream inputStream = connectFactory.getInput()) {
            byte[] bytes = new byte[inputStream.available()];
            return commonSerializer.deserialize(bytes, RegisterResponse.class);
        } catch (IOException e) {
            logger.error("消费者注册失败", e);
        }
        return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "消费者注册失败");
    }
}
