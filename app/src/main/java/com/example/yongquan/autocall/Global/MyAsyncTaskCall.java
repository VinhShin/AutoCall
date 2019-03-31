package com.example.yongquan.autocall.Global;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyAsyncTaskCall extends AsyncTask<Void, Integer, Void> {
        Context context;
        public MyAsyncTaskCall(Context context) {
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (Gv.date == null) {
                    Gv.date = new Date();
                }
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(Gv.date);
                SharedPreferences sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String str = sharedPreferences.getString("contact_t", "");
                Gv.listContact_temp = Global_Function.convertStringToArray(str);

                if (Gv.listContact_temp == null) {
                    str = sharedPreferences.getString("contact", "");
                    Gv.listContact_temp = Global_Function.convertStringToArray(str);
                    editor.putString("contact_t", Global_Function.converStringFromArray(Gv.listContact_temp));
                }
                Gv.INDEX_PHONE = sharedPreferences.getInt(Gv.INDEX_PHONE_STR, 0);
                Gv.TIME_START = sharedPreferences.getString(Gv.TIME_START_STR, "00:00");
                Gv.TIME_END = sharedPreferences.getString(Gv.TIME_END_STR, "23:59");
                Gv.SMS_UNABLE = sharedPreferences.getBoolean(Gv.SMS_UNABLE_STR, false);
                Gv.SERVICE_IS_START = sharedPreferences.getBoolean(Gv.SERVICE_IS_START_STR, false);
                if (Gv.SMS_UNABLE) {
                    checkSendSMS(sharedPreferences, calendar);
                }
                int TIME_START = (Integer.valueOf(Gv.TIME_START.split(":")[0])) * 60 + (Integer.valueOf(Gv.TIME_START.split(":")[1]));
                int TIME_END = (Integer.valueOf(Gv.TIME_END.split(":")[0])) * 60 + (Integer.valueOf(Gv.TIME_END.split(":")[1]));

                if (Gv.random == null) {
                    Gv.random = new Random();
                }
                Gv.INDEX_PHONE = Gv.random.nextInt(Gv.listContact_temp.size());

                if (!Gv.SERVICE_IS_START || TIME_START == TIME_END ||
                        TIME_START > TIME_END ||
                        calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE) < TIME_START ||
                        calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE) > TIME_END) {
                    return null;
                }
                //call
                String phone =  Gv.listContact_temp.get(Gv.INDEX_PHONE).getPhone();

                Gv.callIntent = new Intent(Intent.ACTION_CALL);
                Gv.callIntent.setData(Uri.parse("tel:" + phone));
                Gv.callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(Gv.callIntent);


//                callTo(context, Gv.listContact_temp.get(Gv.INDEX_PHONE).getPhone());
                editor.putInt(Gv.INDEX_PHONE_STR, Gv.INDEX_PHONE);
                Gv.listContact_temp.remove(Gv.INDEX_PHONE);
                editor.putString("contact_t", Global_Function.converStringFromArray(Gv.listContact_temp));
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
//                appendLog("hy: "+e.getMessage().toString() + " \n");
            }
            return null;
        }
    public static void checkSendSMS(SharedPreferences sharedPreferences, Calendar calendar) {
        Gv.TIME_SEND_SMS = sharedPreferences.getInt(Gv.TIME_SEND_SMS_STR, 7);
        Gv.DAY_SEND_SMS = sharedPreferences.getInt(Gv.DAY_SEND_SMS_STR, 7);
        Gv.WAS_SEND_SMS = sharedPreferences.getBoolean(Gv.WAS_SEND_SMS_STR, false);
        Gv.SMS_CONTENT = sharedPreferences.getString(Gv.SMS_CONTENT_STR, "");
        Gv.SMS_SENDTO = sharedPreferences.getString(Gv.SMS_SENDTO_STR, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!Gv.WAS_SEND_SMS && (
                Gv.DAY_SEND_SMS == calendar.get(Calendar.DAY_OF_WEEK) ||
                        (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && Gv.DAY_SEND_SMS == 8)) &&
                calendar.get(Calendar.HOUR_OF_DAY) == Gv.TIME_SEND_SMS) {
            Gv.CALL_SUCCESS = sharedPreferences.getInt(Gv.CALL_SUCCESS_STR, 0);
            long thoigiangoi = Gv.TOTAL_TIME_CALL / 60;
            long thoigiangois = Gv.TOTAL_TIME_CALL % 60;
            String messageToSend = Gv.SMS_CONTENT +
                    " \n " + "Tong thoi gian goi : " + thoigiangoi + " phut " + thoigiangois + " giay \n " +
                    "So cuoc goi thanh cong : " + Gv.CALL_SUCCESS;
            String number = Gv.SMS_SENDTO;

            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(messageToSend);
            sms.sendMultipartTextMessage(number, null, parts, null, null);

            Gv.WAS_SEND_SMS = true;
            Gv.TOTAL_TIME_CALL = 0;

            editor.putLong(Gv.TOTAL_TIME_CALL_STR, Gv.TOTAL_TIME_CALL);
            editor.putInt(Gv.CALL_SUCCESS_STR, 0);
            editor.putBoolean(Gv.WAS_SEND_SMS_STR, Gv.WAS_SEND_SMS);
        }
        if (Gv.WAS_SEND_SMS && ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && Gv.DAY_SEND_SMS == 8) ||
                calendar.get(Calendar.DAY_OF_WEEK) > Gv.DAY_SEND_SMS)) {

            Gv.WAS_SEND_SMS = false;
            editor.putBoolean(Gv.WAS_SEND_SMS_STR, Gv.WAS_SEND_SMS);
        }
        editor.apply();
    }
    }