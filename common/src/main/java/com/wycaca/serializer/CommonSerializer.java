package com.wycaca.serializer;

import com.wycaca.constant.SystemConst;

public interface CommonSerializer {
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    static CommonSerializer getSerializer(String type) {
        switch (type) {
            case SystemConst.KRYO:
                return new KryoSerializer();
            default:
                return new KryoSerializer();
        }
    }
}
