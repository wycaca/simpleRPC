package com.wycaca.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderServiceDemo {
    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceDemo.class);

    private void sayHello(String name) {
        logger.info("hi, {}", name);
    }
}
