package com.wycaca.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ConnectService {
    public abstract InputStream getInput() throws IOException;

    public abstract OutputStream getOutPut() throws IOException;
}
