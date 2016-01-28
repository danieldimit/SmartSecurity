package com.proseminar.smartsecurity;

import android.telephony.SmsManager;

public class Sms {
    public static void sendSMS(String toPhoneNumber, String smsMessage) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
    }
}
