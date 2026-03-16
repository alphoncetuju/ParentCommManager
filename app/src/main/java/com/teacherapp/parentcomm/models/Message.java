package com.teacherapp.parentcomm.models;

public class Message {
    private long id;
    private long learnerId;
    private String learnerName;
    private String parentName;
    private String phoneNumber;
    private String content;
    private long dateTime;
    private String direction; // SENT, RECEIVED
    private String status;    // SENT, FAILED, DELIVERED

    public Message() {}

    public Message(long learnerId, String learnerName, String parentName,
                   String phoneNumber, String content, String direction) {
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.parentName = parentName;
        this.phoneNumber = phoneNumber;
        this.content = content;
        this.direction = direction;
        this.dateTime = System.currentTimeMillis();
        this.status = "SENT";
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

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getDateTime() { return dateTime; }
    public void setDateTime(long dateTime) { this.dateTime = dateTime; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
