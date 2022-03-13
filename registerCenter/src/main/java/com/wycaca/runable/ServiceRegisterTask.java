package com.wycaca.runable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wycaca.constant.SystemConst;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.service.ConnectService;
import com.wycaca.service.ConnectServiceFactory;
import com.wycaca.service.RegisterCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ServiceRegisterTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegisterTask.class);
    private final RegisterCenterService registerCenterService;
    private final ConnectService connectService;
    private final CommonSerializer commonSerializer;

    public ServiceRegisterTask(Socket socket) {
        registerCenterService = new RegisterCenterService();
        connectService = ConnectServiceFactory.get(socket);
        commonSerializer = CommonSerializer.getSerializer(SystemConst.KRYO);
    }

    @Override
    public void run() {
        // 持续监听socket, 接受各种消息
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String url = "";
        try {
            inputStream = connectService.getInput();
            outputStream = connectService.getOutPut();
            // BIO方式
            while (true) {
                byte[] bytes = new byte[inputStream.available()];
                url = (String) commonSerializer.deserialize(bytes, String.class);
                RegisterResponse response = registerCenterService.register(url);
                outputStream.write(commonSerializer.serialize(response));
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
        }
    }
}
