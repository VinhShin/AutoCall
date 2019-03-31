package com.example.yongquan.autocall;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.yongquan.autocall.Global.Gv;

/**
 * Created by DELL on 7/29/2018.
 */

public class PhoneCallListener extends PhoneStateListener {

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if (state == TelephonyManager.CALL_STATE_RINGING) {
            Gv.STATE_PHONE = "ringing";
//            Log.d("YongQuan1","ringing");
        }

        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            Gv.STATE_PHONE = "offhook";
            Log.d("state","offhook");
            Gv.statusIdel=0;
        }

        if (state == TelephonyManager.CALL_STATE_IDLE) {
            Log.d("state","idle");
            if(Gv.statusIdel>2){
                Gv.STATE_PHONE ="idle";
            }
            Gv.statusIdel++;
        }
    }
}
