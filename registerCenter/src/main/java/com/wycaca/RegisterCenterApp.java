package com.wycaca;

import com.wycaca.service.RegisterCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RegisterCenterApp {
    private static final Logger logger = LoggerFactory.getLogger(RegisterCenterApp.class);

    public static void main(String[] args) {
        RegisterCenterService registerCenterService = new RegisterCenterService();
        try {
            registerCenterService.start();
        } catch (IOException e) {
            logger.error("注册中心异常: ", e);
        }
    }
}
