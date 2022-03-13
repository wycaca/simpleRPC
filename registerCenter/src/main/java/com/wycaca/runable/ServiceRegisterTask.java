package com.wycaca.runable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.service.RegisterCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ServiceRegisterTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegisterTask.class);
    private final Socket socket;
    private final RegisterCenterService registerCenterService;

    public ServiceRegisterTask(Socket socket) {
        this.socket = socket;
        registerCenterService = new RegisterCenterService();
    }

    @Override
    public void run() {
        // 持续监听socket, 接受各种消息
        InputStream inputStream = null;
        OutputStream outputStream = null;
        // kryo序列化
        Input reader = null;
        Output writer = null;
        String url = "";
        Kryo kryo = new Kryo();
        kryo.register(RegisterResponse.class);
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            reader = new Input(inputStream);
            writer = new Output(outputStream);
            // BIO方式
            while (true) {
                url = reader.readString();
                RegisterResponse response = registerCenterService.register(url);
                kryo.writeObject(writer, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
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
