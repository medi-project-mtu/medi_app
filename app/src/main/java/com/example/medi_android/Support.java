package com.example.medi_android;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Support {
    private String subject;
    private String msg;
    private String dateTime = LocalDateTime.now().toString();
    private Boolean read = false;

    public Support() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
