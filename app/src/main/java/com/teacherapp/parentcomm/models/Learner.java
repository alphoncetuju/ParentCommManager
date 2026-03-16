package com.teacherapp.parentcomm.models;

public class Learner {
    private long id;
    private String fullName;
    private String firstName;
    private String parentName;
    private String parentPhone;
    private String className;
    private long createdAt;

    public Learner() {}

    public Learner(String fullName, String parentName, String parentPhone, String className) {
        this.fullName = fullName;
        this.firstName = extractFirstName(fullName);
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.className = className;
        this.createdAt = System.currentTimeMillis();
    }

    public static String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.firstName = extractFirstName(fullName);
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return fullName + " (" + className + ")";
    }
}
