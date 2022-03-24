package com.wycaca.runable;

import com.wycaca.connect.ConnectFactory;
import com.wycaca.connect.impl.SocketFactory;
import com.wycaca.constant.Const;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class WorkTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WorkTask.class);

    private final ConnectFactory connectService;
    private final CommonSerializer commonSerializer;

    public WorkTask(Socket socket) throws IOException {
        // 获取socket连接, 接受消息
        connectService = new SocketFactory(socket);
        commonSerializer = CommonSerializer.getSerializer(Const.KRYO);
    }

    @Override
    public void run() {
        // 持续监听socket, 接受注册消息
        String clazzName = "";
        String methodName = "";
        try (InputStream inputStream = connectService.getInput();
             OutputStream outputStream = connectService.getOutPut();
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ) {
            // 获取 调用 必要的参数
            clazzName = (String) objectInputStream.readObject();
            methodName = (String) objectInputStream.readObject();
            Object[] params = (Object[]) objectInputStream.readObject();

            Class<?> serviceClazz = Class.forName(clazzName);
            Method method = ReflectUtils.findMethodByMethodName(serviceClazz, methodName);
            Object result = method.invoke(serviceClazz.newInstance(), params);
            // 返回结果
            outputStream.write(commonSerializer.serialize(result));
            outputStream.flush();
        } catch (IOException e) {
            logger.error("注册中心注册服务失败, ", e);
        } catch (ClassNotFoundException e) {
            logger.error("提供者 未找到对应服务类 {}, ", clazzName, e);
        } catch (NoSuchMethodException e) {
            logger.error("提供者 未找到对应方法 {}, ", methodName, e);
        } catch (InstantiationException e) {
            logger.error("提供者 服务类创建失败 {}, ", clazzName, e);
        } catch (IllegalAccessException e) {
            logger.error("提供者 {} 方法 参数错误, ", methodName, e);
        } catch (InvocationTargetException e) {
            logger.error("提供者 {} 执行方法 反射抛出异常, ", methodName, e);
        }
    }
}
