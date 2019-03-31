package com.example.yongquan.autocall.Receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.yongquan.autocall.AddPhone;
import com.example.yongquan.autocall.Global.AlarmManager;
import com.example.yongquan.autocall.Global.Global_Function;
import com.example.yongquan.autocall.Global.Gv;
import com.example.yongquan.autocall.Global.MyAsyncTaskDisConnect;
import com.example.yongquan.autocall.Model.Contact;

import java.util.ArrayList;

import static com.example.yongquan.autocall.SettingActivity.TGBD;
import static com.example.yongquan.autocall.SettingActivity.TGC;
import static com.example.yongquan.autocall.SettingActivity.TGKN;
import static com.example.yongquan.autocall.SettingActivity.TGKT;
import static com.example.yongquan.autocall.SettingActivity.rd_connect_munite;
import static com.example.yongquan.autocall.SettingActivity.rd_connect_second;
import static com.example.yongquan.autocall.SettingActivity.rd_wait_munite;
import static com.example.yongquan.autocall.SettingActivity.rd_wait_second;

/**
 * Created by DELL on 8/5/2018.
 */

public class SmsSetting extends WakefulBroadcastReceiver {

    public static final String SMS_EXTRA_NAME = "pdus";
    private Context context = null;
    String regexStr = "^[0-9]*$";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        AlarmManager.cancelAlarm();
        new MyAsyncTaskDisConnect().execute();
        if(Gv.childrenAlarmManager!=null) {
            Gv.childrenAlarmManager.cancelAlarm();
        }
        processSMS(intent);

        startCall();
    }

    public void processSMS(Intent intent) {
        Bundle extras = intent.getExtras();
        String messages = "";
        if (extras != null) {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);
            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                messages = sms.getMessageBody().toString();
            }
            //check line
            String[] lines = messages.split("\\r?\\n");
            int check = -1;
            SharedPreferences sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (lines[0].equalsIgnoreCase("themds")) {
                ArrayList<Contact> list = new ArrayList<>();
                for (int i = 1; i < lines.length; i++) {
                    String arr[] = lines[i].split(" ");

                    if (arr.length > 1 && arr[1].matches(regexStr)) {
                        list.add(new Contact(arr[0], arr[1]));
                    }
                }
                if (list.size() > 0) {
                    editor.putString("contact", Global_Function.converStringFromArray(list));
                    editor.apply();
                    AddPhone.setListView(list);
                }
            }else if (lines[0].equalsIgnoreCase("themtds")) {
                ArrayList<Contact> list = new ArrayList<>();
                for (int i = 1; i < lines.length; i++) {
                    String arr[] = lines[i].split(" ");

                    if (arr.length > 1 && arr[1].matches(regexStr)) {
                        list.add(new Contact(arr[0], arr[1]));
                    }
                }
                if (list.size() > 0) {
                    ArrayList<Contact> listOld = Global_Function.convertStringToArray(sharedPreferences.getString("contact",""));
                    listOld.addAll(list);
                    editor.putString("contact", Global_Function.converStringFromArray(listOld));
                    editor.apply();
                    AddPhone.setListView(listOld);
                }
            }
            else {
                for (String line : lines) {
                    isSyntaxForRegisterCourse(line, editor);
                    Log.d("YongQuan", "l " + line);

                }
                editor.apply();
            }
        }
    }

    public int isSyntaxForRegisterCourse(String body, SharedPreferences.Editor editor) {
        String arr[] = body.split(" ");
        String regexStr = "^[0-9]*$";
        if (arr.length == 3) {
            if (arr[0].equalsIgnoreCase("TGKN")) {
                if (!arr[1].matches(regexStr) || arr[1].length() < 1 || arr[1].length() > 5 || Integer.valueOf(arr[1]) < 0) {
                    return -1;
                } else if (arr[2].equalsIgnoreCase("phut")) {
                    editor.putInt(Gv.TIME_CONNECT_STR, Integer.valueOf(arr[1]));
                    editor.putBoolean(Gv.TIME_CONNECT_MUNITE_STR, true);
                    if (TGKN != null) {
                        TGKN.setText(arr[1]);
                        rd_connect_munite.setChecked(true);
                        rd_connect_second.setChecked(false);
                    }
                } else if (arr[2].equalsIgnoreCase("giay")) {
                    editor.putInt(Gv.TIME_CONNECT_STR, Integer.valueOf(arr[1]));
                    editor.putBoolean(Gv.TIME_CONNECT_MUNITE_STR, false);
                    if (TGKN != null) {
                        TGKN.setText(arr[1]);
                        rd_connect_munite.setChecked(false);
                        rd_connect_second.setChecked(true);
                    }
                }
            } else if (arr[0].equalsIgnoreCase("TGC")) {
                if (!arr[1].matches(regexStr) || arr[1].length() < 1 || arr[1].length() > 5 || Integer.valueOf(arr[1]) < 0) {
                    return -1;
                } else if (arr[2].equalsIgnoreCase("phut")) {
                    editor.putInt(Gv.TIME_WAITING_STR, Integer.valueOf(arr[1]));
                    editor.putBoolean(Gv.TIME_WAIT_MUNITE_STR, true);
                    if (TGC != null) {
                        TGC.setText(arr[1]);
                        rd_wait_munite.setChecked(true);
                        rd_wait_second.setChecked(false);
                    }
                } else if (arr[2].equalsIgnoreCase("giay")) {
                    editor.putInt(Gv.TIME_WAITING_STR, Integer.valueOf(arr[1]));
                    editor.putBoolean(Gv.TIME_WAIT_MUNITE_STR, false);
                    if (TGC != null) {
                        TGC.setText(arr[1]);
                        rd_wait_munite.setChecked(false);
                        rd_wait_second.setChecked(true);
                    }
                }
            } else if (arr[0].equalsIgnoreCase("KG")) {
                String strTimeStart[] = arr[1].split(":");
                String strTimeEnd[] = arr[2].split(":");

                if (arr[1].length() != 5 || arr[1].charAt(2) != ':' ||
                        !strTimeStart[0].matches(regexStr) ||
                        !strTimeStart[1].matches(regexStr) ||
                        Integer.valueOf(strTimeStart[0]) > 24 ||
                        Integer.valueOf(strTimeStart[0]) < 0 ||
                        Integer.valueOf(strTimeStart[1]) > 59 ||
                        Integer.valueOf(strTimeStart[1]) < 0) {
                } else {
                    editor.putString(Gv.TIME_START_STR, arr[1]);
                    if (TGBD != null) {
                        TGBD.setText(arr[1]);
                    }
                }

                if (arr[2].length() != 5 || arr[2].charAt(2) != ':' ||
                        !strTimeEnd[0].matches(regexStr) ||
                        !strTimeEnd[1].matches(regexStr) ||
                        Integer.valueOf(strTimeEnd[0]) > 24 ||
                        Integer.valueOf(strTimeEnd[0]) < 0 ||
                        Integer.valueOf(strTimeEnd[1]) > 59 ||
                        Integer.valueOf(strTimeEnd[1]) < 0) {
                } else {
                    editor.putString(Gv.TIME_END_STR, arr[2]);
                    if (TGBD != null) {
                        TGKT.setText(arr[2]);
                    }
                }
            } else if (arr[0].equalsIgnoreCase("them")) {
                if (arr[2].matches(regexStr)) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
                    String str = sharedPreferences.getString("contact", "");
                    ArrayList<Contact> listContact = Global_Function.convertStringToArray(str);
                    if (listContact == null) {
                        listContact = new ArrayList<Contact>();
                    }
                    listContact.add(new Contact(arr[1], arr[2]));
                    editor.putString("contact", Global_Function.converStringFromArray(listContact));
                    editor.apply();
                    AddPhone.setListView(listContact);
                    Log.d("YongQuan", "cung vao day lun");
                }
            }
        }
        return -1;
    }

    private void startCall(){
        Gv.STATE_PHONE = "idle";
        SharedPreferences sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
        boolean serviceActivated = sharedPreferences.getBoolean(Gv.SERVICE_IS_START_STR, false);
        if (serviceActivated) {
            AlarmManager.actionCall(context, 10);
            Global_Function.sendNotification(context, "Ứng dụng đang chạy ngầm", 1);
        }

    }
}
