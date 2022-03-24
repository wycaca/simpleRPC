package com.wycaca.proxy.impl;

import com.wycaca.connect.ConnectFactory;
import com.wycaca.proxy.RPCProxyFactory;
import com.wycaca.serializer.CommonSerializer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;

public class JdkProxyFactory implements RPCProxyFactory {
    @Override
    public <T> T getProxy(Class<?> interfaceClazz, ConnectFactory connectFactory, CommonSerializer commonSerializer) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{interfaceClazz}, (proxy, method, args) -> {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(connectFactory.getOutPut());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // 发送给提供者
            objectOutputStream.write(commonSerializer.serialize(interfaceClazz.getName()));
            objectOutputStream.write(commonSerializer.serialize(method.getName()));
            objectOutputStream.write(commonSerializer.serialize(args));
            // 判断返回 是否为 void
            if (method.getReturnType() != Void.TYPE) {
                // 接受返回结果
                byte[] bytesBuffer = new byte[1024];
                int len = -1;
                // BIO方式
                while ((len = connectFactory.getInput().read(bytesBuffer)) != -1) {
                    byteArrayOutputStream.write(bytesBuffer, 0, len);
                    // 反序列 注册url
                    Object result = commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), Object.class);
                    return result;
                }
            }
            return null;
        });
    }
}
