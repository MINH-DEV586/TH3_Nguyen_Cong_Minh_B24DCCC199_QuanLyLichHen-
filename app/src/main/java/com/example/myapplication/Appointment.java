package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
    private int id;
    private String title;
    private String location;
    private String date; // Lưu dạng yyyy-MM-dd
    private String time; // Lưu dạng HH:mm
    private int reminder; // 1: bật nhắc nhở, 0: tắt

    public Appointment() {
    }

    public Appointment(String title, String location, String date, String time, int reminder) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Appointment(int id, String title, String location, String date, String time, int reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public boolean isReminderOn() {
        return reminder == 1;
    }

    public void setReminderOn(boolean on) {
        this.reminder = on ? 1 : 0;
    }

    /**
     * Chuyển đổi ngày từ yyyy-MM-dd sang dd/MM/yyyy
     */
    public String getFormattedDate() {
        if (date == null) return "";
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d = dbFormat.parse(date);
            if (d != null) {
                return displayFormat.format(d);
            }
        } catch (ParseException e) {
            // Trường hợp dữ liệu cũ hoặc không theo định dạng chuẩn
        }
        return date;
    }

    /**
     * Trả về chuỗi ngày giờ theo định dạng dd/MM/yyyy · HH:mm
     */
    public String getFormattedDateTime() {
        return getFormattedDate() + " · " + (time != null ? time : "");
    }
}
