package com.example.yongquan.autocall.Global;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.yongquan.autocall.Model.Contact;
import com.example.yongquan.autocall.Receiver.NotificationDismissedReceiver;
import com.example.yongquan.autocall.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Global_Function {

    public static ArrayList<Contact> convertStringToArray(String str) {
        if (str != "") {
            ArrayList<Contact> contact = new ArrayList<Contact>();
            String[] tempArray;
            tempArray = str.split("__");
            for (int i = 0; i < tempArray.length; i++) {
                String[] temp = tempArray[i].split("_");
                if (temp.length > 1) {
                    contact.add(new Contact(temp[0], temp[1]));
                }
            }
            if (contact.size() > 0) {
                return contact;
            } else {
                return null;
            }
        }
        return null;
    }


    public static String converStringFromArray(ArrayList<Contact> list) {
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            str += list.get(i).getName() + "_" + list.get(i).getPhone() + "__";
        }
        return str;
    }

    public static void sendNotification(Context context, String message, int notificationId) {
        Bundle b = new Bundle();
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("com.stack.notificationId", notificationId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setDeleteIntent(createOnDismissedIntent(context, 11))
                .setSound(defaultSoundUri);
//                .setContentIntent(pendingIntent);
        Notification note = notificationBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }

    private static PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("com.stack.notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    public static void disconnectCall() {
        try {
            Log.d("YongQuan","disconnect be call");
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
    }

    public static void callTo(Context contextParent, String phone) {

        if (Gv.callIntent == null) {
            Gv.callIntent = new Intent(Intent.ACTION_CALL);
            Gv.callIntent.setData(Uri.parse("tel:" + phone));
            Gv.callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);

            PackageManager packageManager = contextParent.getPackageManager();
            List activities = packageManager.queryIntentActivities(Gv.callIntent, PackageManager.MATCH_DEFAULT_ONLY);

            for (int j = 0; j < activities.size(); j++) {

                if (activities.get(j).toString().toLowerCase().contains("com.android.phone")) {
                    try {
                        Runtime.getRuntime().exec("pm clear com.android.phone");
                        Runtime.getRuntime().exec("pm clear com.android.providers.telephony");
                        Log.d("clear package", "s");
                        trimCache(contextParent);
                    } catch (IOException e) {
                        Log.d("clear package", e.toString());
                        e.printStackTrace();
                    }
                    Gv.callIntent.setPackage("com.android.phone");
                } else if (activities.get(j).toString().toLowerCase().contains("call")) {
                    String pack = (activities.get(j).toString().split("[ ]")[1].split("[/]")[0]);
                    try {
                        Log.d("aa", pack);
                        Runtime.getRuntime().exec("pm clear " + pack);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gv.callIntent.setPackage(pack);
                }
            }

            if (ActivityCompat.checkSelfPermission(contextParent, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        contextParent.startActivity(Gv.callIntent);

    }

    public static void addTimeToTal(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("YongQuan", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long totalTime = sharedPreferences.getLong(Gv.TOTAL_TIME_TEMP_STR, 0);
        if (totalTime > 45) {
            Gv.CALL_SUCCESS = sharedPreferences.getInt(Gv.CALL_SUCCESS_STR, 0);
            Gv.CALL_SUCCESS++;
            editor.putInt(Gv.CALL_SUCCESS_STR, Gv.CALL_SUCCESS);
        }
        Gv.TOTAL_TIME_CALL = sharedPreferences.getLong(Gv.TOTAL_TIME_CALL_STR, 0);
        editor.putLong(Gv.TOTAL_TIME_CALL_STR, Gv.TOTAL_TIME_CALL + totalTime);
        //RESET LAI
        editor.putLong(Gv.TOTAL_TIME_TEMP_STR, 0);

        editor.apply();
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

//    public static void appendLog(String text) {
//        String filename = "filename1.txt";
//        File file = new File("/storage/emulated/0/", filename);
//        FileOutputStream fos;
//        try {// /storage/sdcard0/filename1.txt
//            fos = new FileOutputStream(file, true);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
//            myOutWriter.append(text);
//            myOutWriter.close();
//            fos.close();
//        } catch (FileNotFoundException e) {
//
//        } catch (IOException e) {
//            // handle exception
//        }
//
//    }

    public static void generateCall(Context context) {
        Log.d("YongQuan","be call");
        try {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(new Date());
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
                return;
            }

            callTo(context, Gv.listContact_temp.get(Gv.INDEX_PHONE).getPhone());
            editor.putInt(Gv.INDEX_PHONE_STR, Gv.INDEX_PHONE);
            Gv.listContact_temp.remove(Gv.INDEX_PHONE);
            editor.putString("contact_t", Global_Function.converStringFromArray(Gv.listContact_temp));
            editor.apply();
        } catch (Exception e) {
//            e.printStackTrace();
//            appendLog("hy: "+e.getMessage().toString() + " \n");
        }

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
