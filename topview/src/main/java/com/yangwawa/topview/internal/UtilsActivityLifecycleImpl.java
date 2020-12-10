package com.yangwawa.topview.internal;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2020/03/19
 *     desc  :
 * </pre>
 */
final class UtilsActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

    static final UtilsActivityLifecycleImpl INSTANCE = new UtilsActivityLifecycleImpl();

    private final LinkedList<Activity> mActivityList = new LinkedList<>();

    void init(Application app) {
        app.registerActivityLifecycleCallbacks(this);
    }

    void unInit(Application app) {
        mActivityList.clear();
        app.unregisterActivityLifecycleCallbacks(this);
    }

    Activity getTopActivity() {
        List<Activity> activityList = getActivityList();
        for (Activity activity : activityList) {
            if (!UtilsBridge.isActivityAlive(activity)) {
                continue;
            }
            return activity;
        }
        return null;
    }

    List<Activity> getActivityList() {
        if (!mActivityList.isEmpty()) {
            return new LinkedList<>(mActivityList);
        }
        List<Activity> reflectActivities = getActivitiesByReflect();
        mActivityList.addAll(reflectActivities);
        return new LinkedList<>(mActivityList);
    }

    Application getApplicationByReflect() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object thread = getActivityThread();
            Object app = activityThreadClass.getMethod("getApplication").invoke(thread);
            if (app == null) {
                return null;
            }
            return (Application) app;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // lifecycle start
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {/**/}

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        setTopActivity(activity);
    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {/**/}

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityResumed(@NonNull final Activity activity) {
        setTopActivity(activity);
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {/**/}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {/**/}

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {/**/}

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {/**/}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        mActivityList.remove(activity);
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {/**/}
    ///////////////////////////////////////////////////////////////////////////
    // lifecycle end
    ///////////////////////////////////////////////////////////////////////////

    private void setTopActivity(final Activity activity) {
        if (mActivityList.contains(activity)) {
            if (!mActivityList.getFirst().equals(activity)) {
                mActivityList.remove(activity);
                mActivityList.addFirst(activity);
            }
        } else {
            mActivityList.addFirst(activity);
        }
    }

    /**
     * @return the activities which topActivity is first position
     */
    private List<Activity> getActivitiesByReflect() {
        LinkedList<Activity> list = new LinkedList<>();
        Activity topActivity = null;
        try {
            Object activityThread = getActivityThread();
            Field mActivitiesField = activityThread.getClass().getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Object mActivities = mActivitiesField.get(activityThread);
            if (!(mActivities instanceof Map)) {
                return list;
            }
            Map<Object, Object> binder_activityClientRecord_map = (Map<Object, Object>) mActivities;
            for (Object activityRecord : binder_activityClientRecord_map.values()) {
                Class activityClientRecordClass = activityRecord.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                if (topActivity == null) {
                    Field pausedField = activityClientRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        topActivity = activity;
                    } else {
                        list.add(activity);
                    }
                } else {
                    list.add(activity);
                }
            }
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivitiesByReflect: " + e.getMessage());
        }
        if (topActivity != null) {
            list.addFirst(topActivity);
        }
        return list;
    }

    private Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) return activityThread;
        return getActivityThreadInActivityThreadStaticMethod();
    }

    private Object getActivityThreadInActivityThreadStaticField() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.getMessage());
            return null;
        }
    }

    private Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.getMessage());
            return null;
        }
    }

}
