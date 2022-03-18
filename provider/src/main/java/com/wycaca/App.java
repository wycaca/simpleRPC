package com.wycaca;

import com.wycaca.constant.Const;
import com.wycaca.demo.ProviderServiceDemo;
import com.wycaca.model.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            ProviderService provideService = new ProviderService(8100);
            Class<ProviderServiceDemo> providerServiceDemoClazz = ProviderServiceDemo.class;
            String clazzName = providerServiceDemoClazz.getName();
            provideService.setName(clazzName);
            provideService.setPath(clazzName);
            // 获取所有method名
            Method[] methods = providerServiceDemoClazz.getDeclaredMethods();
            List<String> methodNameList = new ArrayList<>();
            for (Method method : methods) {
                methodNameList.add(method.getName());
            }
            provideService.setMethods(methodNameList);
            provideService.setRegisterTimestamp(System.currentTimeMillis());

            // 注册
            provideService.doRegister("127.0.0.1", Const.REGISTER_PORT);
        } catch (IOException e) {
            logger.error("生产者服务异常, ", e);
        }
    }
}
