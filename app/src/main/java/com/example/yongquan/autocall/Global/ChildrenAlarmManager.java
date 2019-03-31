package com.example.yongquan.autocall.Global;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.yongquan.autocall.Receiver.ChildrenAlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

public class ChildrenAlarmManager {

    private static PendingIntent sender;
    private static android.app.AlarmManager alarmManager;
    public ChildrenAlarmManager(){}

    public static void actionCheckCall(Context context, long timeSecond){
        if(Gv.receiverIntentChild==null) {
            Gv.receiverIntentChild = new Intent(context, ChildrenAlarmReceiver.class);
        }
        if(sender==null) {
            sender = PendingIntent.getBroadcast(context, 123456789, Gv.receiverIntentChild, 0);
            alarmManager = (android.app.AlarmManager) context.getSystemService(ALARM_SERVICE);
        }
        cancelAlarm();
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact (android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+timeSecond * 1000, sender);
        } else {
            alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+timeSecond * 1000, sender);
        }
    }

    public static void cancelAlarm(){
        if(alarmManager!=null && sender!=null) {
            alarmManager.cancel(sender);
        }
    }
}
