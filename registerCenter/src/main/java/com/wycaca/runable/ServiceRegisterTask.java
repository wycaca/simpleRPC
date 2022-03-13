package com.wycaca.runable;

import com.wycaca.service.RegisterCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String url = "";
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            // BIO方式
            while (true) {
                url = reader.readLine();
                String str = registerCenterService.register(url);
                writer.write(str);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
