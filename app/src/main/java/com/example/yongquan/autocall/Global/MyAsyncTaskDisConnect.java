package com.example.yongquan.autocall.Global;

import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Method;

public class MyAsyncTaskDisConnect extends AsyncTask<Void, Integer, Void> {

        public MyAsyncTaskDisConnect() {

        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String serviceManagerName = "android.os.ServiceManager";
                String serviceManagerNativeName = "android.os.ServiceManagerNative";
                String telephonyName = "com.android.internal.telephony.ITelephony";
                Class<?> telephonyClass;
                Class<?> telephonyStubClass;
                Class<?> serviceManagerClass;
                Class<?> serviceManagerNativeClass;
                Method telephonyEndCall;
                Object telephonyObject;
                Object serviceManagerObject;
                telephonyClass = Class.forName(telephonyName);
                telephonyStubClass = telephonyClass.getClasses()[0];
                serviceManagerClass = Class.forName(serviceManagerName);
                serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
                Method getService = // getDefaults[29];
                        serviceManagerClass.getMethod("getService", String.class);
                Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
                if(Gv.tmpBinder==null) {
                    Gv.tmpBinder = new Binder();
                    Gv.tmpBinder.attachInterface(null, "fake");
                }
                serviceManagerObject = tempInterfaceMethod.invoke(null, Gv.tmpBinder);
                IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
                Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                telephonyObject = serviceMethod.invoke(null, retbinder);
                telephonyEndCall = telephonyClass.getMethod("endCall");
                telephonyEndCall.invoke(telephonyObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }