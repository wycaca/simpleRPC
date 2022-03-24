package com.wycaca.serializer;

import com.wycaca.constant.Const;

public interface CommonSerializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<?> clazz);

    static CommonSerializer getSerializer(String type) {
        switch (type) {
            case Const.KRYO:
                return new KryoSerializer();
            default:
                return new KryoSerializer();
        }
    }
}
