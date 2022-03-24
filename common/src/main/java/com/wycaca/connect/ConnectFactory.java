package com.wycaca.connect;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConnectFactory {
    InputStream getInput();

    OutputStream getOutPut();

    void endOutput();
}
