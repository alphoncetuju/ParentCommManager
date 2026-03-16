package com.teacherapp.parentcomm.models;

public class CallLog {
    private long id;
    private long learnerId;
    private String learnerName;
    private String parentName;
    private String phoneNumber;
    private long dateTime;
    private long duration; // seconds
    private String callType; // OUTGOING, INCOMING, MISSED

    public CallLog() {}

    public CallLog(long learnerId, String learnerName, String parentName,
                   String phoneNumber, long dateTime, long duration, String callType) {
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.parentName = parentName;
        this.phoneNumber = phoneNumber;
        this.dateTime = dateTime;
        this.duration = duration;
        this.callType = callType;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getLearnerId() { return learnerId; }
    public void setLearnerId(long learnerId) { this.learnerId = learnerId; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public long getDateTime() { return dateTime; }
    public void setDateTime(long dateTime) { this.dateTime = dateTime; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public String getCallType() { return callType; }
    public void setCallType(String callType) { this.callType = callType; }

    public String getFormattedDuration() {
        if (duration <= 0) return "0s";
        long mins = duration / 60;
        long secs = duration % 60;
        if (mins > 0) return mins + "m " + secs + "s";
        return secs + "s";
    }
}
