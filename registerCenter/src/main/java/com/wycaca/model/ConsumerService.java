package com.wycaca.model;

import com.wycaca.connect.ConnectFactory;
import com.wycaca.connect.impl.SocketFactory;
import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.proxy.RPCProxyFactory;
import com.wycaca.proxy.impl.JdkProxyFactory;
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
     * 向注册中心发送注册连接
     */
    @Override
    public RegisterResponse doRegister(String registerIp, int registerPort) {
        // 如果是消费者, 需要接受返回信息
        // 开启客户端, 连接注册中心的服务器Socket
        ConnectFactory connectService = null;
        try {
            registerSocket = new Socket(registerIp, registerPort);
            connectService = new SocketFactory(registerSocket);
        } catch (IOException e) {
            logger.error("连接注册中心失败, ", e);
            return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "连接注册中心失败");
        }
        RegisterResponse response = new RegisterResponse();
        try (InputStream inputStream = connectService.getInput();
             OutputStream outputStream = connectService.getOutPut();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            // 向注册中心发送注册Url
            outputStream.write(commonSerializer.serialize(getRegisterUrl()));
            outputStream.flush();

            byte[] bytesBuffer = new byte[1024];
            int len = -1;
            // BIO方式
            while ((len = inputStream.read(bytesBuffer)) != -1) {
                byteArrayOutputStream.write(bytesBuffer, 0, len);
                // 反序列 注册url
                response = commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), RegisterResponse.class);
                // 获取返回结果, 判断是否有可有提供者, 注册中心返回一个提供者, 双方建立连接
                if (response.isOk()) {
                    String data = (String) response.getData();
                    doSubscribe(data.split(":")[0], Integer.parseInt(data.split(":")[1]));
                    return RegisterResponse.ok();
                } else {
                    logger.error("消费者注册失败, {}", response.getMessage());
                    return response;
                }
            }
        } catch (IOException e) {
            logger.error("消费者注册失败", e);
        }

        return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "消费者注册失败");
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

    public <T> T getRemoteProxyClazz(Class<T> providerService) {
        RPCProxyFactory proxyFactory = new JdkProxyFactory();
        return proxyFactory.getProxy(providerService, new SocketFactory(socket), commonSerializer);
    }
}
