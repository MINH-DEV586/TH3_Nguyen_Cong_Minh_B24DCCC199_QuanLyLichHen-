package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etTitle;
    private EditText etLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat switchReminder;
    private Button btnSave;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        dbHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        switchReminder = findViewById(R.id.switchReminder);
        btnSave = findViewById(R.id.btnSave);

        timePicker.setIs24HourView(true);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });
    }

    private void saveAppointment() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Kiểm tra không để trống tên lịch hẹn
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Vui lòng nhập tên lịch hẹn");
            etTitle.requestFocus();
            Toast.makeText(this, "Tên lịch hẹn không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra không để trống địa điểm
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Vui lòng nhập địa điểm");
            etLocation.requestFocus();
            Toast.makeText(this, "Địa điểm không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ngày tháng năm từ DatePicker
        int year = datePicker.getYear();
        int month = datePicker.getMonth(); // 0-based
        int dayOfMonth = datePicker.getDayOfMonth();

        // Lấy giờ phút từ TimePicker
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        // Kiểm tra thời điểm hẹn phải sau thời điểm hiện tại
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.set(year, month, dayOfMonth, hour, minute, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();

        if (!selectedCal.after(now)) {
            Toast.makeText(this, "Thời điểm hẹn (ngày và giờ) phải sau thời điểm hiện tại!", Toast.LENGTH_LONG).show();
            return;
        }

        // Định dạng ngày theo yyyy-MM-dd để lưu SQLite và sắp xếp chuẩn
        String dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
        // Định dạng giờ theo HH:mm
        String timeStr = String.format(Locale.US, "%02d:%02d", hour, minute);

        int reminderVal = switchReminder.isChecked() ? 1 : 0;

        Appointment appointment = new Appointment(title, location, dateStr, timeStr, reminderVal);
        long result = dbHelper.insertAppointment(appointment);

        if (result != -1) {
            Toast.makeText(this, "Lưu lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu lịch hẹn vào cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
        }
    }
}
