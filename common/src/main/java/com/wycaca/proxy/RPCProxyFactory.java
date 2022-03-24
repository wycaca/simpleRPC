package com.wycaca.proxy;

import com.wycaca.connect.ConnectFactory;
import com.wycaca.serializer.CommonSerializer;

public interface RPCProxyFactory {
    // todo 没抽象出来, 直接拿jdk的方法签名改一下
    <T> T getProxy(Class<?> interfaceClazz, ConnectFactory connectFactory, CommonSerializer commonSerializer);
}
