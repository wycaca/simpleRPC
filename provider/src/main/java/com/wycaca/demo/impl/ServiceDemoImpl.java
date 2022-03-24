package com.wycaca.demo.impl;

import com.wycaca.demo.ServiceDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDemoImpl implements ServiceDemo {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDemo.class);

    public void sayHello(String name) {
        System.out.println("hi, " + name);
        logger.info("hi, {}", name);
    }
}
