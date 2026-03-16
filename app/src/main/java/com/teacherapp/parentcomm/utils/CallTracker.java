package com.teacherapp.parentcomm.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.teacherapp.parentcomm.database.DatabaseHelper;
import com.teacherapp.parentcomm.models.Learner;

public class CallTracker {
    private static final String TAG = "CallTracker";

    private static Learner pendingLearner;
    private static long callStartTime;

    public static void setPendingCall(Learner learner) {
        pendingLearner = learner;
        callStartTime = System.currentTimeMillis();
    }

    public static void checkPendingCall(Context context) {
        if (pendingLearner == null) return;

        try {
            // Query recent call logs for this number
            String phone = pendingLearner.getParentPhone();
            Cursor cursor = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.DURATION, CallLog.Calls.DATE, CallLog.Calls.TYPE},
                    CallLog.Calls.NUMBER + " LIKE ?",
                    new String[]{"%" + phone.substring(Math.max(0, phone.length() - 7))},
                    CallLog.Calls.DATE + " DESC"
            );

            long duration = 0;
            if (cursor != null && cursor.moveToFirst()) {
                long callDate = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                // Only if call was made after we set pending
                if (callDate >= callStartTime - 5000) {
                    duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                }
                cursor.close();
            }

            com.teacherapp.parentcomm.models.CallLog log = new com.teacherapp.parentcomm.models.CallLog(
                    pendingLearner.getId(),
                    pendingLearner.getFullName(),
                    pendingLearner.getParentName(),
                    pendingLearner.getParentPhone(),
                    callStartTime,
                    duration,
                    "OUTGOING"
            );
            DatabaseHelper.getInstance(context).addCallLog(log);
            Log.d(TAG, "Call logged for " + pendingLearner.getFullName() + ", duration=" + duration);
        } catch (Exception e) {
            Log.e(TAG, "Error logging call: " + e.getMessage());
            // Save call anyway without duration
            com.teacherapp.parentcomm.models.CallLog log = new com.teacherapp.parentcomm.models.CallLog(
                    pendingLearner.getId(), pendingLearner.getFullName(),
                    pendingLearner.getParentName(), pendingLearner.getParentPhone(),
                    callStartTime, 0, "OUTGOING"
            );
            DatabaseHelper.getInstance(context).addCallLog(log);
        } finally {
            pendingLearner = null;
        }
    }
}
