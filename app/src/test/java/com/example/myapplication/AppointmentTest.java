package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppointmentTest {

    @Test
    public void testAppointmentCreationAndFormatting() {
        Appointment app = new Appointment(1, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1);
        assertEquals(1, app.getId());
        assertEquals("Họp nhóm đồ án", app.getTitle());
        assertEquals("Phòng B203", app.getLocation());
        assertEquals("2026-10-25", app.getDate());
        assertEquals("09:00", app.getTime());
        assertEquals(1, app.getReminder());
        assertTrue(app.isReminderOn());

        assertEquals("25/10/2026", app.getFormattedDate());
        assertEquals("25/10/2026 · 09:00", app.getFormattedDateTime());
    }

    @Test
    public void testReminderToggle() {
        Appointment app = new Appointment("Khám răng", "Nha khoa", "2026-10-26", "14:30", 0);
        assertFalse(app.isReminderOn());
        app.setReminderOn(true);
        assertTrue(app.isReminderOn());
        assertEquals(1, app.getReminder());
    }
}
