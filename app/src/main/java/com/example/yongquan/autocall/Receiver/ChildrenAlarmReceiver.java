package com.example.yongquan.autocall.Receiver;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.yongquan.autocall.Global.AlarmManager;
import com.example.yongquan.autocall.Global.Global_Function;
import com.example.yongquan.autocall.Global.Gv;
import com.example.yongquan.autocall.Global.MyAsyncTaskDisConnect;
import static com.example.yongquan.autocall.Global.Gv.childrenAlarmManager;


public class ChildrenAlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (Gv.sharedPreferences == null) {
                Gv.sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
            }
            if (Gv.STATE_PHONE.equals("idle")) {
                //alarm was be cancel in fuction actioncall
                AlarmManager.actionCall(context, 4);
                new MyAsyncTaskDisConnect().execute();
                Global_Function.addTimeToTal(context);
            } else {
                childrenAlarmManager.actionCheckCall(context, Gv.TIME_DISTANCE_CHECK);
                saveToTalTimeCallInCall(Gv.sharedPreferences, Gv.TIME_DISTANCE_CHECK);

            }
        } catch (Exception e) {
            e.printStackTrace();
//            appendLog("loi -_- \n" + e.toString());
        }
    }

    private void saveToTalTimeCallInCall(SharedPreferences sharedPreferences, long totalTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gv.TOTAL_TIME_TEMP = sharedPreferences.getLong(Gv.TOTAL_TIME_TEMP_STR, 0);
        editor.putLong(Gv.TOTAL_TIME_TEMP_STR, Gv.TOTAL_TIME_TEMP + totalTime);
        editor.apply();
    }


}
