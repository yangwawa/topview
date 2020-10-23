package com.yangwawa.topview.hook;

import android.util.Log;
import android.view.IWindowSession;
import android.view.WindowManagerGlobal;

import com.yangwawa.topview.utils.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class WindowManagerHook implements InvocationHandler {

    private static final String TAG = "WindowManagerHook";
    private IWindowSession mOriWindowSession;
    private IWindowSession mProxyWindowSession;

    public void hook(){
        try {
            mProxyWindowSession = (IWindowSession) Proxy.newProxyInstance(IWindowSession.class.getClassLoader(), new Class[]{ IWindowSession.class }, this);
            mOriWindowSession = WindowManagerGlobal.getWindowSession();
            ReflectUtils.reflect(WindowManagerGlobal.class).field("sWindowSession", mProxyWindowSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Log.v(TAG, "invoke method: " + method.getName() + " objects: " + Arrays.deepToString(objects));
        Object result = method.invoke(mOriWindowSession, objects);
        return result;
    }
}
