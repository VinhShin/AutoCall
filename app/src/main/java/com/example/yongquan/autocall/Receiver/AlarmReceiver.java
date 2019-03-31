package com.example.yongquan.autocall.Receiver;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.yongquan.autocall.Global.AlarmManager;
import com.example.yongquan.autocall.Global.Global_Function;
import com.example.yongquan.autocall.Global.Gv;
import com.example.yongquan.autocall.Global.MyAsyncTaskCall;
import com.example.yongquan.autocall.Global.MyAsyncTaskDisConnect;

import static com.example.yongquan.autocall.Global.Gv.childrenAlarmManager;


public class AlarmReceiver extends WakefulBroadcastReceiver {

    int heSoWait = 1;
    int heSoConnect = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            getVariable(context);
            if (intent.getAction().equals(Gv.ACTION_CALL)) {
                childrenAlarmManager.actionCheckCall(context, Gv.TIME_DISTANCE_CHECK);
                AlarmManager.disConnectCall(context, heSoConnect * Gv.TIME_CONNECT);
                new MyAsyncTaskCall(context).execute();
            }//wait
            else {
                childrenAlarmManager.cancelAlarm();
                if (Gv.SERVICE_IS_START) {
                    AlarmManager.actionCall(context, Gv.TIME_WAITING * heSoWait + 2);
                }
                Global_Function.addTimeToTal(context);
                new MyAsyncTaskDisConnect().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVariable(Context context) {
        if(Gv.sharedPreferences==null) {
            Gv.sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
        }
        Gv.TIME_WAIT_MUNITE = Gv.sharedPreferences.getBoolean(Gv.TIME_WAIT_MUNITE_STR, false);
        Gv.TIME_WAITING = Gv.sharedPreferences.getInt(Gv.TIME_WAITING_STR, 9);
        Gv.TIME_CONNECT_MUNITE = Gv.sharedPreferences.getBoolean(Gv.TIME_CONNECT_MUNITE_STR, true);
        Gv.TIME_CONNECT = Gv.sharedPreferences.getInt(Gv.TIME_CONNECT_STR, 9);
        Gv.SERVICE_IS_START = Gv.sharedPreferences.getBoolean(Gv.SERVICE_IS_START_STR, false);
        if (Gv.TIME_WAIT_MUNITE) {
            heSoWait = 60;
        }
        if (Gv.TIME_CONNECT_MUNITE) {
            heSoConnect = 60;
        }
    }
}
