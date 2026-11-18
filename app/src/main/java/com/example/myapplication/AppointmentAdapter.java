package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    private List<Appointment> appointmentList = new ArrayList<>();
    private final OnItemClickListener listener;

    public AppointmentAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAppointments(List<Appointment> list) {
        this.appointmentList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment item = appointmentList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvLocation;
        private final TextView tvDateTime;
        private final TextView tvReminderBadge;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvLocation = itemView.findViewById(R.id.tvItemLocation);
            tvDateTime = itemView.findViewById(R.id.tvItemDateTime);
            tvReminderBadge = itemView.findViewById(R.id.tvItemReminderBadge);
        }

        public void bind(final Appointment appointment, final OnItemClickListener listener) {
            tvTitle.setText(appointment.getTitle());
            tvLocation.setText("Địa điểm: " + appointment.getLocation());
            tvDateTime.setText(appointment.getFormattedDateTime());

            if (appointment.isReminderOn()) {
                tvReminderBadge.setText("Có nhắc");
                tvReminderBadge.setBackgroundResource(R.drawable.bg_pill_reminder_on);
                tvReminderBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pill_reminder_on_text));
            } else {
                tvReminderBadge.setText("Không nhắc");
                tvReminderBadge.setBackgroundResource(R.drawable.bg_pill_reminder_off);
                tvReminderBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pill_reminder_off_text));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(appointment);
                    }
                }
            });
        }
    }
}
