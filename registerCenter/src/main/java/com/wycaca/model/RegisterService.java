package com.wycaca.model;

import com.wycaca.constant.Const;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.service.ConnectService;
import com.wycaca.service.ConnectServiceFactory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

@Data
@NoArgsConstructor
public class RegisterService {
    /**
     * 注册url
     * 参照dubbo
     */
    protected String registerUrl;
    protected String name;
    /**
     * 类型
     * provider: 提供者
     * consumer: 消费者
     */
    protected String type;
    protected String path;
    protected List<String> methods;
    protected String ip;
    protected Integer port;
    protected Long registerTimestamp;
    protected Long lastPingTimestamp;

    protected Socket socket;
    protected ConnectService connectService;
    protected CommonSerializer commonSerializer;

    public RegisterService(int port) throws IOException {
        // 获取本机IP
        this.ip = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
        connectService = ConnectServiceFactory.get(this.socket);
        commonSerializer = CommonSerializer.getSerializer(Const.KRYO);
    }

    public RegisterService(String registerUrl) throws Exception {
        // registry://10.112.6.12:2181/com.alibaba.dubbo.registry.RegistryService?application=test-provider&dubbo=2.5.3&pid=6816&registry=zookeeper&timestamp=1522284700436
        this.registerUrl = registerUrl;
        URL url = new URL(registerUrl);
        String protocol = url.getProtocol();
        if (Const.PROVIDER.equals(protocol) || Const.CONSUMER.equals(protocol)) {
            this.type = protocol;
        } else {
            throw new IllegalArgumentException("注册服务类型错误");
        }
        this.ip = url.getHost();
        this.port = url.getPort();
        this.path = url.getPath();
        // class path, 最后一个作为name
        String[] paths = this.path.split("\\.");
        this.name = paths[paths.length - 1];
        String file = url.getFile();
    }
}
