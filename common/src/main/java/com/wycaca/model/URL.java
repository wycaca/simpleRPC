package com.wycaca.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class URL implements Serializable {
    private String protocol;
    private String host;
    private int port;
    private String path;
    private String file;

    public URL(String url) {
        String[] str = new String[2];
        str = url.split("://");
        protocol = str[0];
        url = str[1];
        str = url.split(":");
        host = str[0];
        url = str[1];
        str = url.split("/");
        port = Integer.parseInt(str[0]);
        url = str[1];
        str = url.split("\\?");
        path = str[0];
        file = str[1];
    }

    public static void main(String[] args) {
        URL url = new URL("consumer://10.10.6.145:9999/com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService?application=cti-link-agent-gateway&application.version=0.0.1&category=consumers&check=false&default.check=false&default.version=0.0.1&dubbo=2.8.4&interface=com.tinet.ctilink.agent.service.AgentOutcallScheduleTaskService&methods=pauseTask,start,listAgentTask,pause&pid=30269&revision=0.0.1&side=consumer&timestamp=1646892725284");
        System.out.println(url);
    }
}
