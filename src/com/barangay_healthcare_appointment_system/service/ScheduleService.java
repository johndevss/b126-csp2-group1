package com.barangay_healthcare_appointment_system.service;

import com.barangay_healthcare_appointment_system.repository.ServiceScheduleRepository;
import java.time.LocalDate;

public class ScheduleService {
    private final ServiceScheduleRepository scheduleRepository;

    public ScheduleService() {
        this.scheduleRepository = new ServiceScheduleRepository();
    }

    // Translates a calendar date into a day-of-the-week string to check service availability.
    public boolean isServiceAvailableOnDate(int serviceId, LocalDate date) {
        if (date == null) {
            return false;
        }

        // Extract day name from LocalDate object (e.g., "MONDAY", "TUESDAY")
        String dayOfWeek = date.getDayOfWeek().name();

        // Pass the structural values over to the repository table query filter
        return scheduleRepository.isServiceAvailableOnDay(serviceId, dayOfWeek);
    }
}