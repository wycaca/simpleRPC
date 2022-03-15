package com.wycaca.load.impl;

import com.wycaca.load.LoadSelector;
import com.wycaca.model.RegisterService;

import java.util.Random;
import java.util.concurrent.ConcurrentMap;

public class RandomSelector implements LoadSelector {
    @Override
    public RegisterService select(ConcurrentMap<String, RegisterService> serviceMap) {
        Random random = new Random();
        RegisterService[] registerServices = serviceMap.values().toArray(new RegisterService[1]);
        return registerServices[random.nextInt(registerServices.length)];
    }
}
