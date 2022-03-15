package com.wycaca.model;

import com.wycaca.constant.Const;
import lombok.Data;

import java.net.URL;
import java.util.List;

@Data
public class RegisterService {
    /**
     * 注册url
     * 参照dubbo
     */
    private String registerUrl;
    private String name;
    /**
     * 类型
     * provider: 提供者
     * consumer: 消费者
     */
    private String type;
    private String path;
    private List<String> methods;
    private String ip;
    private Integer port;
    private Long registerTimestamp;
    private Long lastPingTimestamp;

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
