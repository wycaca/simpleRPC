package com.wycaca.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {
    public static Method findMethodByMethodName(Class<?> clazz, String methodName)
            throws NoSuchMethodException {
        List<Method> targetMethod = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                targetMethod.add(method);
            }
        }
        if (targetMethod.isEmpty()) {
            throw new NoSuchMethodException("找不到 " + methodName + "方法");
        }
        if (targetMethod.size() > 1) {
            throw new IllegalStateException("类 " + clazz.getName() + " 有多个 " + methodName + "方法");
        }
        return targetMethod.get(0);
    }
}
