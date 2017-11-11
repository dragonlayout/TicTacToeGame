package maged.csi5230.edu.tictactoegame.utils;

import android.telephony.SmsManager;

/**
 * Created by dragonlayout on 2017/11/11.
 */

public class SmsUtils {

    public static void sendMessage(long phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(String.valueOf(phoneNumber), null, message, null, null);
    }
}
