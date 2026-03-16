package com.teacherapp.parentcomm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.Learner;
import com.teacherapp.parentcomm.models.Message;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) return;

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        String format = bundle.getString("format");
        if (pdus == null) return;

        StringBuilder fullBody = new StringBuilder();
        String senderPhone = null;

        for (Object pdu : pdus) {
            SmsMessage sms;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                sms = SmsMessage.createFromPdu((byte[]) pdu);
            }
            if (sms != null) {
                if (senderPhone == null) senderPhone = sms.getOriginatingAddress();
                fullBody.append(sms.getMessageBody());
            }
        }

        if (senderPhone == null || fullBody.length() == 0) return;

        // Try to match sender to a learner
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        Learner learner = db.getLearnerByPhone(senderPhone);

        Message msg = new Message();
        msg.setDateTime(System.currentTimeMillis());
        msg.setPhoneNumber(senderPhone);
        msg.setContent(fullBody.toString());
        msg.setDirection("RECEIVED");
        msg.setStatus("RECEIVED");

        if (learner != null) {
            msg.setLearnerId(learner.getId());
            msg.setLearnerName(learner.getFullName());
            msg.setParentName(learner.getParentName());
        } else {
            msg.setLearnerId(-1);
            msg.setLearnerName("Unknown");
            msg.setParentName(senderPhone);
        }

        db.addMessage(msg);
        Log.d(TAG, "Stored incoming SMS from " + senderPhone);
    }
}
