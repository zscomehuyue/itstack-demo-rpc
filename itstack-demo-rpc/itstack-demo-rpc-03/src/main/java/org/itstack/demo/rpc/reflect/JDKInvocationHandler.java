package org.itstack.demo.rpc.reflect;


import org.itstack.demo.rpc.network.future.SyncWrite;
import org.itstack.demo.rpc.network.msg.Request;
import org.itstack.demo.rpc.network.msg.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JDKInvocationHandler implements InvocationHandler {

    private Request request;

    public JDKInvocationHandler(Request request) {
        this.request = request;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class[] paramTypes = method.getParameterTypes();
        if ("toString".equals(methodName) && paramTypes.length == 0) {
            return request.toString();
        } else if ("hashCode".equals(methodName) && paramTypes.length == 0) {
            return request.hashCode();
        } else if ("equals".equals(methodName) && paramTypes.length == 1) {
            return request.equals(args[0]);
        }
        //设置参数
        request.setMethodName(methodName);
        request.setParamTypes(paramTypes);
        request.setArgs(args);
        request.setRef(request.getRef());

        // todo 利用缓存保存请求id对应的请求对象，并根据设置的等待时间，等待获取结果；返回response；
        Response response = new SyncWrite().writeAndSync(request.getChannel(), request, 5000);
        //异步调用  todo 涉及到客户端等待时间，服务端执行完成时间，客户端如果执行了get方法，一致等待，直到服务端完成后发送消息；
        return response.getResult();

    }

}
