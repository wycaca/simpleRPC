package com.wycaca.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RegisterResponse implements Serializable {
    private Integer code;
    private String message;
    private Object data;

    public RegisterResponse(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public static RegisterResponse fail(Integer code, String msg) {
        return new RegisterResponse(code, msg);
    }

    public static RegisterResponse ok() {
        RegisterResponse registerResponse  = new RegisterResponse();
        registerResponse.code = HttpStatus.OK.value();
        return registerResponse;
    }

    public static RegisterResponse ok(Object data) {
        RegisterResponse registerResponse  = new RegisterResponse();
        registerResponse.code = HttpStatus.OK.value();
        registerResponse.data = data;
        return registerResponse;
    }
}
