package com.wycaca;

import com.wycaca.constant.Const;
import com.wycaca.demo.ServiceDemo;
import com.wycaca.model.ProviderService;
import com.wycaca.model.response.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProviderApp {
    private static final Logger logger = LoggerFactory.getLogger(ProviderApp.class);

    public static void main(String[] args) {
        try {
            ProviderService provideService = new ProviderService(8100, ServiceDemo.class);
            // 注册
            RegisterResponse response = provideService.doRegister("127.0.0.1", Const.REGISTER_PORT);
            if (response.isOk()) {
                logger.info("注册成功");
            } else {
                logger.error("注册失败, 错误码: {},{}", response.getCode(), response.getMessage());
            }
        } catch (IOException e) {
            logger.error("生产者服务异常, ", e);
        }
    }
}
