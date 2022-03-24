package com.wycaca.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ConnectFactory {
    InputStream getInput() throws IOException;

    OutputStream getOutPut() throws IOException;
}
