package com.wycaca.runable;

import java.net.Socket;

public class ServiceTask implements Runnable{
    private final Socket socket;

    public ServiceTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
