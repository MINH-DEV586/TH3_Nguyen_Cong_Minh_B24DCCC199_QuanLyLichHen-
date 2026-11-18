package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalCount;
    private TextView tvReminderCount;
    private TextView tvEmptyState;
    private TextView tvListHint;
    private RecyclerView rvAppointments;
    private Button btnAddNew;

    private DatabaseHelper dbHelper;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvListHint = findViewById(R.id.tvListHint);
        rvAppointments = findViewById(R.id.rvAppointments);
        btnAddNew = findViewById(R.id.btnAddNew);

        rvAppointments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Appointment appointment) {
                Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
                intent.putExtra("id", appointment.getId());
                startActivity(intent);
            }
        });
        rvAppointments.setAdapter(adapter);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAppointmentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * Tải lại danh sách lịch hẹn và thống kê từ cơ sở dữ liệu SQLite
     */
    private void loadData() {
        // Lấy thống kê
        int total = dbHelper.countAll();
        int reminders = dbHelper.countReminders();
        tvTotalCount.setText(String.valueOf(total));
        tvReminderCount.setText(String.valueOf(reminders));

        // Lấy danh sách lịch hẹn sắp xếp theo ngày giờ tăng dần
        List<Appointment> list = dbHelper.getAllAppointments();
        adapter.setAppointments(list);

        if (list == null || list.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            tvListHint.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            tvListHint.setVisibility(View.VISIBLE);
        }
    }
}
