package com.wycaca.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wycaca.model.response.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RegisterResponse.class);
        return kryo;
    });

    public KryoSerializer() {
        super();
    }

    public static Kryo getKryo() {
        return kryoThreadLocal.get();
    }

    public static void register(Class<?> clazz) {
        getKryo().register(clazz);
    }

    @Override
    public byte[] serialize(Object obj) {
        Kryo kryo = getKryo();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, obj);
        output.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Kryo kryo = getKryo();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        return kryo.readObject(input, clazz);
    }

//    public static void main(String[] args) {
//        KryoSerializer kryoSerializer = new KryoSerializer();
//        String test = "test";
//        byte[] bytes = null;
//        bytes = kryoSerializer.serialize(test);
//        System.out.println(kryoSerializer.deserialize(bytes, String.class));
//    }
}
