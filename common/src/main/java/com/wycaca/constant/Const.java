package com.wycaca.constant;

public interface Const {
    /**
     * 注册中心端口
     */
    public static final int REGISTER_PORT = 8300;

    /**
     * 超时时间, 默认10s
     */

    public static final long TIMEOUT = 10 * 60 * 1000;

    /**
     * 检测超时间隔时间, 默认30s
     */
    public static final long SCAN_TIME = 30;

    public static final String PROVIDER = "provider";
    public static final String CONSUMER = "consumer";

    /**
     * 序列化方式
     */
    public static final String KRYO = "kryo";

    /**
     * URL 参数中的Key
     */
    public static final String PARAMS_TIMESTAMP = "timestamp";
}
