package com.example.taskmanager;

import android.widget.CheckBox;

import java.util.Date;

public class Task {

    private String taskId;
    private String tag;
    private String description;
    private Date deadline;
    private boolean status;
    private String userId;

    public Task() {
    }

    public Task(String taskId, String tag, String description, Date deadline, boolean status, String userId) {
        this.taskId = taskId;
        this.tag = tag;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }
}
