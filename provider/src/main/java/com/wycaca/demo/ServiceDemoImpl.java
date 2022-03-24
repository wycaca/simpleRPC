package com.wycaca.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDemoImpl implements ServiceDemo {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDemo.class);

    public void sayHello(String name) {
        logger.info("hi, {}", name);
    }
}
