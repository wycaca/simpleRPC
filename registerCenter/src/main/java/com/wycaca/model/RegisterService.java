package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.service.ConnectFactory;
import com.wycaca.service.impl.SocketImpl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Data
@NoArgsConstructor
public class RegisterService {
    /**
     * 注册url
     * 参照dubbo
     */
    protected URL url;
    protected Long registerTimestamp;
    protected Long lastPingTimestamp;

    protected CommonSerializer commonSerializer;

    public RegisterService(int port, Class<?> registerClazz) throws IOException {
        // 通过反射, 获取提供者服务的一些属性
        String clazzName = registerClazz.getName();
        url = new URL();
        // 获取本机IP
//        url.setHost(InetAddress.getLocalHost().getHostAddress());
        // Demo, 先写死
        url.setHost("127.0.0.1");
        url.setPort(port);
        url.setPath(clazzName);
        url.addParam(Const.PARAMS_INTERFACE, clazzName);
        this.setRegisterTimestamp(System.currentTimeMillis());
        commonSerializer = CommonSerializer.getSerializer(Const.KRYO);
    }

    public RegisterService(String registerUrl) throws Exception {
        // registry://10.112.6.12:2181/com.alibaba.dubbo.registry.RegistryService?application=test-provider&dubbo=2.5.3&pid=6816&registry=zookeeper&timestamp=1522284700436
        url = new URL(registerUrl);
    }

    /**
     * 向注册中心发送注册连接
     */
    public RegisterResponse doRegister(String registerIp, int registerPort) throws IOException {
        // 开启客户端, 连接注册中心的服务器Socket
        ConnectFactory connectFactory = new SocketImpl(new Socket(registerIp, registerPort));
        OutputStream outputStream = connectFactory.getOutPut();
        // 向注册中心发送注册Url
        outputStream.write(commonSerializer.serialize(getRegisterUrl()));
        outputStream.flush();
        outputStream.close();
        return RegisterResponse.ok();
    }

    public String getRegisterUrl() {
        return url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/" + url.getPath() + url.getParamsStr();
    }
}
