package com.wycaca.proxy.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class RpcInvoke implements Serializable {
    private String clazzName;
    private String method;
    private int paramsLength;
    private ArrayList<Object> params;
}
