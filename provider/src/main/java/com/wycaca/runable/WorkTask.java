package com.wycaca.runable;

import com.wycaca.connect.ConnectFactory;
import com.wycaca.connect.impl.SocketFactory;
import com.wycaca.constant.Const;
import com.wycaca.proxy.model.RpcInvoke;
import com.wycaca.serializer.CommonSerializer;
import com.wycaca.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        RpcInvoke rpcInvoke = new RpcInvoke();
        try (InputStream inputStream = connectService.getInput();
             OutputStream outputStream = connectService.getOutPut();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ) {
            // 获取 调用 必要的参数
            byte[] bytesBuffer = new byte[1024];
            int len = -1;
            // BIO方式
            while ((len = inputStream.read(bytesBuffer)) != -1) {
                byteArrayOutputStream.write(bytesBuffer, 0, len);
                // 反序列
                rpcInvoke = commonSerializer.deserialize(byteArrayOutputStream.toByteArray(), RpcInvoke.class);
                break;
            }
            // todo 完善 接口 具体实现, 现在就随便写写了, 只能获取同目录下的 Impl 实现类
            Class<?> serviceClazz = Class.forName(rpcInvoke.getClazzName() + "Impl");
            Method method = ReflectUtils.findMethodByMethodName(serviceClazz, rpcInvoke.getMethod());
            Object result = method.invoke(serviceClazz.newInstance(), rpcInvoke.getParams().toArray());
            // 返回结果
            if (method.getReturnType() != Void.TYPE) {
                outputStream.write(commonSerializer.serialize(result));
                outputStream.flush();
            }
        } catch (IOException e) {
            logger.error("注册中心注册服务失败, ", e);
        } catch (ClassNotFoundException e) {
            logger.error("提供者 未找到对应服务类 {}, ", rpcInvoke.getClazzName(), e);
        } catch (NoSuchMethodException e) {
            logger.error("提供者 未找到对应方法 {}, ", rpcInvoke.getMethod(), e);
        } catch (InstantiationException e) {
            logger.error("提供者 服务类创建失败 {}, ", rpcInvoke.getClazzName(), e);
        } catch (IllegalAccessException e) {
            logger.error("提供者 {} 方法 参数错误, ", rpcInvoke.getMethod(), e);
        } catch (InvocationTargetException e) {
            logger.error("提供者 {} 执行方法 反射抛出异常, ", rpcInvoke.getMethod(), e);
        }
    }
}
