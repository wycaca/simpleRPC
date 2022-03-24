package com.wycaca.runable;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.RegisterCenterService;
import com.wycaca.service.impl.SocketImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServiceRegisterTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegisterTask.class);
    private final RegisterCenterService registerCenterService;
    private final ConnectFactory connectService;
    private final CommonSerializer commonSerializer;

    public ServiceRegisterTask(RegisterCenterService registerCenterService, Socket socket) throws IOException {
        this.registerCenterService = registerCenterService;
        // 连接上提供者的Socket服务器
        connectService = new SocketImpl(socket);
        commonSerializer = CommonSerializer.getSerializer(Const.KRYO);
    }

    @Override
    public void run() {
        // 持续监听socket, 接受注册消息
        String url = "";
        try (InputStream inputStream = connectService.getInput();
             OutputStream outputStream = connectService.getOutPut();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            // BIO方式
            while (true) {
                byte[] bytesBuffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(bytesBuffer)) != -1) {
                    byteArrayOutputStream.write(bytesBuffer, 0, len);
                }
                // 反序列 注册url
                url = commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), String.class);
                RegisterResponse response = registerCenterService.register(url);
                // todo 返回消息
//                outputStream.write(commonSerializer.serialize(response));
//                outputStream.flush();
            }
        } catch (IOException e) {
            logger.error("注册中心注册服务失败, ", e);
        }
    }
}
