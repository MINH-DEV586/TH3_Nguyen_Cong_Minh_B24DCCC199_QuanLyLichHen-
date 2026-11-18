package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Bảng appointments
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COLUMN_APP_ID = "id";
    public static final String COLUMN_APP_TITLE = "title";
    public static final String COLUMN_APP_LOCATION = "location";
    public static final String COLUMN_APP_DATE = "date";
    public static final String COLUMN_APP_TIME = "time";
    public static final String COLUMN_APP_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT NOT NULL"
                + ");";
        db.execSQL(createUsersTable);

        // Tạo bảng appointments
        String createAppointmentsTable = "CREATE TABLE " + TABLE_APPOINTMENTS + " ("
                + COLUMN_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_APP_TITLE + " TEXT NOT NULL, "
                + COLUMN_APP_LOCATION + " TEXT NOT NULL, "
                + COLUMN_APP_DATE + " TEXT NOT NULL, "
                + COLUMN_APP_TIME + " TEXT NOT NULL, "
                + COLUMN_APP_REMINDER + " INTEGER NOT NULL"
                + ");";
        db.execSQL(createAppointmentsTable);

        // Chèn tài khoản mẫu: admin / 123456
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_USERNAME, "admin");
        userValues.put(COLUMN_USER_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Chèn 3 lịch hẹn mẫu
        insertSampleAppointment(db, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1);
        insertSampleAppointment(db, "Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", 1);
        insertSampleAppointment(db, "Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", 0);
    }

    private void insertSampleAppointment(SQLiteDatabase db, String title, String location, String date, String time, int reminder) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_TITLE, title);
        values.put(COLUMN_APP_LOCATION, location);
        values.put(COLUMN_APP_DATE, date);
        values.put(COLUMN_APP_TIME, time);
        values.put(COLUMN_APP_REMINDER, reminder);
        db.insert(TABLE_APPOINTMENTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Kiểm tra thông tin đăng nhập từ bảng users bằng câu truy vấn có tham số
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS
                    + " WHERE " + COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
            cursor = db.rawQuery(query, new String[]{username, password});
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Thêm tài khoản mới vào bảng users
     */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Thêm lịch hẹn mới, trả về id vừa thêm
     */
    public long insertAppointment(Appointment a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_TITLE, a.getTitle());
        values.put(COLUMN_APP_LOCATION, a.getLocation());
        values.put(COLUMN_APP_DATE, a.getDate());
        values.put(COLUMN_APP_TIME, a.getTime());
        values.put(COLUMN_APP_REMINDER, a.getReminder());
        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    /**
     * Lấy toàn bộ lịch hẹn, ORDER BY date ASC, time ASC
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + COLUMN_APP_ID + ", "
                    + COLUMN_APP_TITLE + ", "
                    + COLUMN_APP_LOCATION + ", "
                    + COLUMN_APP_DATE + ", "
                    + COLUMN_APP_TIME + ", "
                    + COLUMN_APP_REMINDER
                    + " FROM " + TABLE_APPOINTMENTS
                    + " ORDER BY " + COLUMN_APP_DATE + " ASC, " + COLUMN_APP_TIME + " ASC";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_ID);
                int titleIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_TITLE);
                int locationIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_LOCATION);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_DATE);
                int timeIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_TIME);
                int reminderIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_REMINDER);

                do {
                    Appointment app = new Appointment(
                            cursor.getInt(idIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(locationIndex),
                            cursor.getString(dateIndex),
                            cursor.getString(timeIndex),
                            cursor.getInt(reminderIndex)
                    );
                    list.add(app);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Lấy một lịch hẹn theo id
     */
    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT " + COLUMN_APP_ID + ", "
                    + COLUMN_APP_TITLE + ", "
                    + COLUMN_APP_LOCATION + ", "
                    + COLUMN_APP_DATE + ", "
                    + COLUMN_APP_TIME + ", "
                    + COLUMN_APP_REMINDER
                    + " FROM " + TABLE_APPOINTMENTS
                    + " WHERE " + COLUMN_APP_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor != null && cursor.moveToFirst()) {
                return new Appointment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APP_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APP_REMINDER))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Cập nhật trạng thái nhắc nhở
     */
    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_REMINDER, on ? 1 : 0);
        return db.update(TABLE_APPOINTMENTS, values, COLUMN_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Xóa lịch hẹn theo id
     */
    public int deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_APPOINTMENTS, COLUMN_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Tổng số lịch hẹn: COUNT(*)
     */
    public int countAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * Số lịch hẹn đang bật nhắc nhở: COUNT với reminder = 1
     */
    public int countReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APP_REMINDER + " = 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
}
