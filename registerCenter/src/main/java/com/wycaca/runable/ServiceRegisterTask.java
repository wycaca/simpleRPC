package com.wycaca.runable;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.service.ConnectService;
import com.wycaca.service.ConnectServiceFactory;
import com.wycaca.service.RegisterCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServiceRegisterTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegisterTask.class);
    private final RegisterCenterService registerCenterService;
    private final ConnectService connectService;
    private final CommonSerializer commonSerializer;

    public ServiceRegisterTask(Socket socket) {
        registerCenterService = new RegisterCenterService();
        connectService = ConnectServiceFactory.get(socket);
        commonSerializer = CommonSerializer.getSerializer(Const.KRYO);
    }

    @Override
    public void run() {
        // 持续监听socket, 接受各种消息
        InputStream inputStream = null;
        OutputStream outputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String url = "";
        try {
            inputStream = connectService.getInput();
            outputStream = connectService.getOutPut();
            byteArrayOutputStream = new ByteArrayOutputStream();
            // BIO方式
            while (true) {
                byte[] bytesBuffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(bytesBuffer)) != -1) {
                    byteArrayOutputStream.write(bytesBuffer, 0, len);
                }
                url = (String) commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), String.class);
                RegisterResponse response = registerCenterService.register(url);
                outputStream.write(commonSerializer.serialize(response));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
