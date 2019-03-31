package com.example.yongquan.autocall;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
/**
 * Created by apple on 12/16/15.
 */
public class MyApp extends Application {
    public static boolean isShowLog = false;
    private static MyApp instance;
    private static Context context;
    private static Activity activity;

    private static boolean isBackGround = false;

    static Dialog dialog;
    private Handler mainThreadHandler;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getContent(){
        return  context;
    }

}

