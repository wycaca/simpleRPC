package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.impl.SocketImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    public void doSubscribe(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            logger.error("消费者连接生产者失败, ", e);
        }
    }

    /**
     * 向注册中心发送注册连接
     */
    @Override
    public RegisterResponse doRegister(String registerIp, int registerPort) {
        super.doRegister(registerIp, registerPort);
        // 如果是消费者, 需要接受返回信息
        ConnectFactory connectService = null;
        RegisterResponse response = new RegisterResponse();
        try {
            connectService = new SocketImpl(new Socket(registerIp, registerPort));
        } catch (IOException e) {
            logger.error("消费者连接注册中心失败", e);
            return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "消费者连接注册中心失败");
        }
        try (InputStream inputStream = connectService.getInput();
             OutputStream outputStream = connectService.getOutPut();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            byte[] bytesBuffer = new byte[1024];
            int len = -1;
            // BIO方式
            while ((len = inputStream.read(bytesBuffer)) != -1) {
                byteArrayOutputStream.write(bytesBuffer, 0, len);
                // 反序列 注册url
                response = commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), RegisterResponse.class);
            }
        } catch (IOException e) {
            logger.error("消费者注册失败", e);
        }
        // todo 获取返回结果, 判断是否有可有提供者, 注册中心返回一个提供者, 双方建立连接
        if (response.isOk()) {
            String data = (String) response.getData();
            doSubscribe(data.split(":")[0], Integer.parseInt(data.split(":")[1]));
            return RegisterResponse.ok();
        }
        return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "消费者注册失败");
    }
}
