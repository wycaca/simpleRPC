package com.wycaca.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wycaca.model.response.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KryoSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RegisterResponse.class);
        kryo.setReferences(true);
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
        Output output = new Output();
        kryo.writeObject(output, obj);
        return output.toBytes();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Kryo kryo = getKryo();
        Input input = new Input();
        input.read(bytes);
        return kryo.readObject(input, clazz);
    }
}
