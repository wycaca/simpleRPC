package com.wycaca.threadPoolFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadPoolFactory implements ThreadFactory {

    /**
     * 自增线程计数器
     */
    private final AtomicInteger threadIndex = new AtomicInteger(0);

    // 线程名前缀
    private String prefix;

    public NamedThreadPoolFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(prefix + "-thread-" + threadIndex.getAndIncrement());
        return thread;
    }
}
