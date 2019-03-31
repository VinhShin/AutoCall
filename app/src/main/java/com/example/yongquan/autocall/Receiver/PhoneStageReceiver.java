package com.example.yongquan.autocall.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.example.yongquan.autocall.Global.AlarmManager;
import com.example.yongquan.autocall.Global.Gv;
import com.example.yongquan.autocall.Global.MyAsyncTaskDisConnect;

public class PhoneStageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            AlarmManager.cancelAlarm();
            new MyAsyncTaskDisConnect().execute();
            if(Gv.childrenAlarmManager!=null) {
                Gv.childrenAlarmManager.cancelAlarm();
            }
            Gv.STATE_PHONE = "ringing";
            SharedPreferences sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
            boolean serviceActivated = sharedPreferences.getBoolean(Gv.SERVICE_IS_START_STR, false);
            if(serviceActivated) {
                AlarmManager.actionCall(context, 15);
            }
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Gv.STATE_PHONE = "offhook";
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            Gv.STATE_PHONE = "idle";
        }

    }
}