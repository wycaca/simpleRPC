package com.wycaca;

import com.wycaca.constant.Const;
import com.wycaca.demo.ServiceDemo;
import com.wycaca.model.ConsumerService;
import com.wycaca.model.response.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConsumerApp {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) {
        try {
            ConsumerService consumerService = new ConsumerService(8100, ServiceDemo.class);
            // 注册
            RegisterResponse response = consumerService.doRegister("127.0.0.1", Const.REGISTER_PORT);
            if (response.isOk()) {
                logger.info("消费者注册成功");
            } else {
                logger.error("消费者注册失败, 错误码: {},{}", response.getCode(), response.getMessage());
            }
            ServiceDemo serviceDemo = consumerService.getRemoteProxyClazz(ServiceDemo.class);
            serviceDemo.sayHello("wycaca");
        } catch (IOException e) {
            logger.error("消费者服务异常, ", e);
        }
    }
}
