package com.wycaca.constant;

public interface SystemConst {
    /**
     * 注册中心端口
     */
    public static final int REGISTER_PORT = 8300;

    /**
     * 超时时间, 默认30s
     */

    public static final long TIMEOUT = 30 * 60 * 1000;

    /**
     * 检测超时间隔时间, 默认60s
     */
    public static final long SCAN_TIME = 60;

    public static final String PROVIDER = "provider";
    public static final String CONSUMER = "consumer";

    public static final String KRYO = "kryo";
}
