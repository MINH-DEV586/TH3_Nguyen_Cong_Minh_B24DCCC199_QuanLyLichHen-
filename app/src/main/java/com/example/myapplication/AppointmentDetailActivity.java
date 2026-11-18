package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageView btnBackHeader;
    private TextView tvDetailTitle;
    private TextView tvDetailLocation;
    private TextView tvDetailTime;
    private TextView tvReminderState;
    private SwitchCompat switchDetailReminder;
    private Button btnBackList;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private int appointmentId = -1;
    private Appointment currentAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        dbHelper = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvReminderState = findViewById(R.id.tvReminderState);
        switchDetailReminder = findViewById(R.id.switchDetailReminder);
        btnBackList = findViewById(R.id.btnBackList);
        btnDelete = findViewById(R.id.btnDelete);

        appointmentId = getIntent().getIntExtra("id", -1);
        if (appointmentId == -1) {
            Toast.makeText(this, "Không tìm thấy lịch hẹn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAppointmentDetails();

        btnBackHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnBackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switchDetailReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (currentAppointment != null) {
                    dbHelper.updateReminder(appointmentId, isChecked);
                    currentAppointment.setReminderOn(isChecked);
                    updateReminderText(isChecked);
                    Toast.makeText(AppointmentDetailActivity.this,
                            isChecked ? "Đã bật nhắc nhở" : "Đã tắt nhắc nhở",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadAppointmentDetails() {
        currentAppointment = dbHelper.getAppointmentById(appointmentId);
        if (currentAppointment == null) {
            Toast.makeText(this, "Không tìm thấy lịch hẹn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle.setText(currentAppointment.getTitle());
        tvDetailLocation.setText(currentAppointment.getLocation());
        tvDetailTime.setText(currentAppointment.getFormattedDateTime());

        boolean isReminderOn = currentAppointment.isReminderOn();
        switchDetailReminder.setChecked(isReminderOn);
        updateReminderText(isReminderOn);
    }

    private void updateReminderText(boolean isReminderOn) {
        if (isReminderOn) {
            tvReminderState.setText("Đang bật");
        } else {
            tvReminderState.setText("Đang tắt");
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lịch hẹn này?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rows = dbHelper.deleteAppointment(appointmentId);
                        if (rows > 0) {
                            Toast.makeText(AppointmentDetailActivity.this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AppointmentDetailActivity.this, "Không thể xóa lịch hẹn!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
