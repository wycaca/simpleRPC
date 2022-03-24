package com.wycaca.connect;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ConnectFactory {
    InputStream getInput();

    OutputStream getOutPut();

    Socket getSocket();

    void endOutput();
}
