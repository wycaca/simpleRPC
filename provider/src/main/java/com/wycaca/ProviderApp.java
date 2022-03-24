package com.wycaca;

import com.wycaca.constant.Const;
import com.wycaca.demo.ProviderServiceDemo;
import com.wycaca.model.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProviderApp {
    private static final Logger logger = LoggerFactory.getLogger(ProviderApp.class);

    public static void main(String[] args) {
        try {
            ProviderService provideService = new ProviderService(8100, ProviderServiceDemo.class);
            // 注册
            provideService.doRegister("127.0.0.1", Const.REGISTER_PORT);
        } catch (IOException e) {
            logger.error("生产者服务异常, ", e);
        }
    }
}
