package com.yangwawa.topview.internal;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.IWindowSession;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;

import com.yangwawa.topview.TopView;

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
        Log.v(TAG, "invoke method:" + method.getName() + " isAttching=" + TopView.getInstance().isAttching() + " args=" + Arrays.deepToString(objects));
        final Activity activity = ActivityUtils.getTopActivity();
        boolean activityChanged = activity != TopView.getInstance().getCurrentActivity();
        if(activityChanged){
            Log.d(TAG, "activity changed currunt=" + TopView.getInstance().getCurrentActivity() + " new=" + activity);
        }
        if(method.getName().equals("addToDisplay") || method.getName().equals("addToDisplayAsUser")){
            WindowManager.LayoutParams params = (WindowManager.LayoutParams)objects[2];
            if(!TopView.getInstance().isAttching()){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {
                        TopView.getInstance().detachAll();
                        TopView.getInstance().attchAll(activity);
                    }
                });
            }
        }
        if(method.getName().equals("relayout") && activityChanged){
            TopView.getInstance().detachAll();
            TopView.getInstance().attchAll(activity);
        }
//        if(method.getName().equals("remove")){
//            TopView.getInstance().detachAll();
//        }
        Object result = method.invoke(mOriWindowSession, objects);
        return result;
    }
}
