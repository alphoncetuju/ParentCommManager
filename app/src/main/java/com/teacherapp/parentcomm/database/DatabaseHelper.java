package com.teacherapp.parentcomm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.teacherapp.parentcomm.models.CallLog;
import com.teacherapp.parentcomm.models.Learner;
import com.teacherapp.parentcomm.models.Message;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "parent_comm.db";
    private static final int DB_VERSION = 1;

    // Learner table
    public static final String TABLE_LEARNERS = "learners";
    public static final String COL_L_ID = "_id";
    public static final String COL_L_FULL_NAME = "full_name";
    public static final String COL_L_FIRST_NAME = "first_name";
    public static final String COL_L_PARENT_NAME = "parent_name";
    public static final String COL_L_PHONE = "parent_phone";
    public static final String COL_L_CLASS = "class_name";
    public static final String COL_L_CREATED = "created_at";

    // Call log table
    public static final String TABLE_CALLS = "call_logs";
    public static final String COL_C_ID = "_id";
    public static final String COL_C_LEARNER_ID = "learner_id";
    public static final String COL_C_LEARNER_NAME = "learner_name";
    public static final String COL_C_PARENT_NAME = "parent_name";
    public static final String COL_C_PHONE = "phone_number";
    public static final String COL_C_DATETIME = "date_time";
    public static final String COL_C_DURATION = "duration";
    public static final String COL_C_TYPE = "call_type";

    // Messages table
    public static final String TABLE_MESSAGES = "messages";
    public static final String COL_M_ID = "_id";
    public static final String COL_M_LEARNER_ID = "learner_id";
    public static final String COL_M_LEARNER_NAME = "learner_name";
    public static final String COL_M_PARENT_NAME = "parent_name";
    public static final String COL_M_PHONE = "phone_number";
    public static final String COL_M_CONTENT = "content";
    public static final String COL_M_DATETIME = "date_time";
    public static final String COL_M_DIRECTION = "direction";
    public static final String COL_M_STATUS = "status";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LEARNERS + " ("
                + COL_L_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_L_FULL_NAME + " TEXT NOT NULL,"
                + COL_L_FIRST_NAME + " TEXT,"
                + COL_L_PARENT_NAME + " TEXT,"
                + COL_L_PHONE + " TEXT,"
                + COL_L_CLASS + " TEXT,"
                + COL_L_CREATED + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_CALLS + " ("
                + COL_C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_C_LEARNER_ID + " INTEGER,"
                + COL_C_LEARNER_NAME + " TEXT,"
                + COL_C_PARENT_NAME + " TEXT,"
                + COL_C_PHONE + " TEXT,"
                + COL_C_DATETIME + " INTEGER,"
                + COL_C_DURATION + " INTEGER,"
                + COL_C_TYPE + " TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " ("
                + COL_M_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_M_LEARNER_ID + " INTEGER,"
                + COL_M_LEARNER_NAME + " TEXT,"
                + COL_M_PARENT_NAME + " TEXT,"
                + COL_M_PHONE + " TEXT,"
                + COL_M_CONTENT + " TEXT,"
                + COL_M_DATETIME + " INTEGER,"
                + COL_M_DIRECTION + " TEXT,"
                + COL_M_STATUS + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEARNERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    // ==================== LEARNER OPERATIONS ====================

    public long addLearner(Learner learner) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_L_FULL_NAME, learner.getFullName());
        cv.put(COL_L_FIRST_NAME, learner.getFirstName());
        cv.put(COL_L_PARENT_NAME, learner.getParentName());
        cv.put(COL_L_PHONE, learner.getParentPhone());
        cv.put(COL_L_CLASS, learner.getClassName());
        cv.put(COL_L_CREATED, System.currentTimeMillis());
        return db.insert(TABLE_LEARNERS, null, cv);
    }

    public int updateLearner(Learner learner) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_L_FULL_NAME, learner.getFullName());
        cv.put(COL_L_FIRST_NAME, learner.getFirstName());
        cv.put(COL_L_PARENT_NAME, learner.getParentName());
        cv.put(COL_L_PHONE, learner.getParentPhone());
        cv.put(COL_L_CLASS, learner.getClassName());
        return db.update(TABLE_LEARNERS, cv, COL_L_ID + "=?",
                new String[]{String.valueOf(learner.getId())});
    }

    public int deleteLearner(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_LEARNERS, COL_L_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Learner> getAllLearners() {
        List<Learner> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LEARNERS, null, null, null, null, null,
                COL_L_FULL_NAME + " ASC");
        if (c.moveToFirst()) {
            do { list.add(cursorToLearner(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Learner> searchLearners(String query) {
        List<Learner> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String q = "%" + query + "%";
        Cursor c = db.query(TABLE_LEARNERS, null,
                COL_L_FULL_NAME + " LIKE ? OR " + COL_L_PARENT_NAME + " LIKE ? OR " +
                        COL_L_CLASS + " LIKE ? OR " + COL_L_PHONE + " LIKE ?",
                new String[]{q, q, q, q}, null, null, COL_L_FULL_NAME + " ASC");
        if (c.moveToFirst()) {
            do { list.add(cursorToLearner(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public Learner getLearnerById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_LEARNERS, null, COL_L_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Learner l = null;
        if (c.moveToFirst()) l = cursorToLearner(c);
        c.close();
        return l;
    }

    public Learner getLearnerByPhone(String phone) {
        SQLiteDatabase db = getReadableDatabase();
        // Normalize: match last 9 digits
        String normalized = phone.replaceAll("[^0-9]", "");
        if (normalized.length() > 9) normalized = normalized.substring(normalized.length() - 9);
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_LEARNERS +
                " WHERE " + COL_L_PHONE + " LIKE ?",
                new String[]{"%" + normalized});
        Learner l = null;
        if (c.moveToFirst()) l = cursorToLearner(c);
        c.close();
        return l;
    }

    private Learner cursorToLearner(Cursor c) {
        Learner l = new Learner();
        l.setId(c.getLong(c.getColumnIndexOrThrow(COL_L_ID)));
        l.setFullName(c.getString(c.getColumnIndexOrThrow(COL_L_FULL_NAME)));
        l.setFirstName(c.getString(c.getColumnIndexOrThrow(COL_L_FIRST_NAME)));
        l.setParentName(c.getString(c.getColumnIndexOrThrow(COL_L_PARENT_NAME)));
        l.setParentPhone(c.getString(c.getColumnIndexOrThrow(COL_L_PHONE)));
        l.setClassName(c.getString(c.getColumnIndexOrThrow(COL_L_CLASS)));
        l.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(COL_L_CREATED)));
        return l;
    }

    // ==================== CALL LOG OPERATIONS ====================

    public long addCallLog(CallLog log) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_C_LEARNER_ID, log.getLearnerId());
        cv.put(COL_C_LEARNER_NAME, log.getLearnerName());
        cv.put(COL_C_PARENT_NAME, log.getParentName());
        cv.put(COL_C_PHONE, log.getPhoneNumber());
        cv.put(COL_C_DATETIME, log.getDateTime());
        cv.put(COL_C_DURATION, log.getDuration());
        cv.put(COL_C_TYPE, log.getCallType());
        return db.insert(TABLE_CALLS, null, cv);
    }

    public List<CallLog> getAllCallLogs() {
        List<CallLog> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CALLS, null, null, null, null, null, COL_C_DATETIME + " DESC");
        if (c.moveToFirst()) {
            do { list.add(cursorToCallLog(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<CallLog> getCallLogsForLearner(long learnerId) {
        List<CallLog> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CALLS, null, COL_C_LEARNER_ID + "=?",
                new String[]{String.valueOf(learnerId)}, null, null, COL_C_DATETIME + " DESC");
        if (c.moveToFirst()) {
            do { list.add(cursorToCallLog(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private CallLog cursorToCallLog(Cursor c) {
        CallLog log = new CallLog();
        log.setId(c.getLong(c.getColumnIndexOrThrow(COL_C_ID)));
        log.setLearnerId(c.getLong(c.getColumnIndexOrThrow(COL_C_LEARNER_ID)));
        log.setLearnerName(c.getString(c.getColumnIndexOrThrow(COL_C_LEARNER_NAME)));
        log.setParentName(c.getString(c.getColumnIndexOrThrow(COL_C_PARENT_NAME)));
        log.setPhoneNumber(c.getString(c.getColumnIndexOrThrow(COL_C_PHONE)));
        log.setDateTime(c.getLong(c.getColumnIndexOrThrow(COL_C_DATETIME)));
        log.setDuration(c.getLong(c.getColumnIndexOrThrow(COL_C_DURATION)));
        log.setCallType(c.getString(c.getColumnIndexOrThrow(COL_C_TYPE)));
        return log;
    }

    // ==================== MESSAGE OPERATIONS ====================

    public long addMessage(Message msg) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_M_LEARNER_ID, msg.getLearnerId());
        cv.put(COL_M_LEARNER_NAME, msg.getLearnerName());
        cv.put(COL_M_PARENT_NAME, msg.getParentName());
        cv.put(COL_M_PHONE, msg.getPhoneNumber());
        cv.put(COL_M_CONTENT, msg.getContent());
        cv.put(COL_M_DATETIME, msg.getDateTime());
        cv.put(COL_M_DIRECTION, msg.getDirection());
        cv.put(COL_M_STATUS, msg.getStatus());
        return db.insert(TABLE_MESSAGES, null, cv);
    }

    public List<Message> getAllMessages() {
        List<Message> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MESSAGES, null, null, null, null, null, COL_M_DATETIME + " DESC");
        if (c.moveToFirst()) {
            do { list.add(cursorToMessage(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Message> getMessagesForLearner(long learnerId) {
        List<Message> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_MESSAGES, null, COL_M_LEARNER_ID + "=?",
                new String[]{String.valueOf(learnerId)}, null, null, COL_M_DATETIME + " ASC");
        if (c.moveToFirst()) {
            do { list.add(cursorToMessage(c)); } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private Message cursorToMessage(Cursor c) {
        Message m = new Message();
        m.setId(c.getLong(c.getColumnIndexOrThrow(COL_M_ID)));
        m.setLearnerId(c.getLong(c.getColumnIndexOrThrow(COL_M_LEARNER_ID)));
        m.setLearnerName(c.getString(c.getColumnIndexOrThrow(COL_M_LEARNER_NAME)));
        m.setParentName(c.getString(c.getColumnIndexOrThrow(COL_M_PARENT_NAME)));
        m.setPhoneNumber(c.getString(c.getColumnIndexOrThrow(COL_M_PHONE)));
        m.setContent(c.getString(c.getColumnIndexOrThrow(COL_M_CONTENT)));
        m.setDateTime(c.getLong(c.getColumnIndexOrThrow(COL_M_DATETIME)));
        m.setDirection(c.getString(c.getColumnIndexOrThrow(COL_M_DIRECTION)));
        m.setStatus(c.getString(c.getColumnIndexOrThrow(COL_M_STATUS)));
        return m;
    }

    // ==================== STATS ====================

    public int getLearnerCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LEARNERS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getCallCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CALLS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getMessageCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MESSAGES, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }
}
