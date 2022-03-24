package com.wycaca.model;

import com.wycaca.connect.ConnectFactory;
import com.wycaca.connect.impl.SocketFactory;
import com.wycaca.constant.Const;
import com.wycaca.model.response.RegisterResponse;
import com.wycaca.serializer.CommonSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Data
@NoArgsConstructor
public class RegisterService {
    private Logger logger = LoggerFactory.getLogger(RegisterService.class);
    /**
     * 注册url
     * 参照dubbo
     */
    protected URL url;
    protected Long registerTimestamp;
    protected Long lastPingTimestamp;
    protected Socket registerSocket;

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
    public RegisterResponse doRegister(String registerIp, int registerPort) {
        // 开启客户端, 连接注册中心的服务器Socket
        ConnectFactory connectService = null;
        try {
            registerSocket = new Socket(registerIp, registerPort);
            connectService = new SocketFactory(registerSocket);
        } catch (IOException e) {
            logger.error("连接注册中心失败, ", e);
            return RegisterResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "连接注册中心失败");
        }

        try (InputStream inputStream = connectService.getInput();
             OutputStream outputStream = connectService.getOutPut();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            // 向注册中心发送注册Url
            outputStream.write(commonSerializer.serialize(getRegisterUrl()));
            outputStream.flush();

            // 收到正确返回, 返回成功
            byte[] bytesBuffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(bytesBuffer)) != -1) {
                byteArrayOutputStream.write(bytesBuffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("向注册中心注册失败, ", e);
        }
//        RegisterResponse result = commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), RegisterResponse.class);
        return RegisterResponse.ok();
    }

    public String getRegisterUrl() {
        return url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/" + url.getPath() + url.getParamsStr();
    }
}
