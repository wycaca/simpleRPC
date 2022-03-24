package com.wycaca.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class URL implements Serializable {
    /**
     * 协议类型
     * dubbo://, https://
     */
    private String protocol;
    private String host;
    private int port;
    /**
     * 服务名
     * com.XX.XX.service
     */
    private String path;
    /**
     * 参数字符串
     * ?application=test&application.version=0.0.1
     */
    private String file;
    private Map<String, String> params;

    public URL(String url) {
        String[] str = new String[2];
        str = url.split("://");
        // 取到 形如 dubbo://, https://, 协议类型
        protocol = str[0];
        // 协议后的字符串, 直接覆盖
        url = str[1];
        // 获取127.0.0.1:8080, Host地址
        str = url.split(":");
        host = str[0];
        url = str[1];
        // 127.0.0.1:8080/com.XX.XX.service, 获取端口
        str = url.split("/");
        port = Integer.parseInt(str[0]);
        url = str[1];
        // com.XX.XX.service?application=test&application.version=0.0.1, 获取服务名和后面的参数串
        str = url.split("\\?");
        path = str[0];
        file = str[1];
        int i = file.indexOf("=");
        // 判断是否有参数
        if (i > 0) {
            params = new HashMap<>();
            // 拆分参数字符串
            String[] paramsStr = file.split("\\&");
            for (String paramStr : paramsStr) {
                params.put(paramStr.split("=")[0], paramStr.split("=")[1]);
            }
        }

    }

//    public static void main(String[] args) {
//        URL url = new URL("consumer://10.10.6.145:9999/com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService?application=cti-link-agent-gateway&application.version=0.0.1&category=consumers&check=false&default.check=false&default.version=0.0.1&dubbo=2.8.4&interface=com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService&methods=pauseTask,start,listAgentTask,pause&pid=30269&revision=0.0.1&side=consumer&timestamp=1646892725284");
//        System.out.println(url);
//    }
}
