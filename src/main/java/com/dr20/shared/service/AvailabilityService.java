package com.dr20.shared.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Availability;
import com.dr20.shared.model.TimeSlot;
import com.dr20.shared.repository.AvailabilityRepository;
import com.dr20.shared.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final MongoTemplate mongoTemplate;

    private static final List<String> DEFAULT_SLOTS = Arrays.asList(
            "09:00 AM", "10:00 AM", "11:00 AM", "02:00 PM", "03:00 PM", "04:00 PM",
            "08:30 PM", "09:00 PM", "09:30 PM");

    public List<String> getAvailableSlots(String doctorId, String dateStr) {
        LocalDate date = parseDate(dateStr);
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot view slots for past dates");
        }
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Availability avail = availabilityRepository.findByDoctorIdAndDate(doctorId, dateStr)
                .orElseGet(() -> createDefault(doctorId, dateStr));

        return avail.getSlots().stream()
                .filter(s -> !s.isBooked())
                .map(TimeSlot::getTime)
                .collect(Collectors.toList());
    }

    public boolean bookSlot(String doctorId, String dateStr, String time) {
        LocalDate date = parseDate(dateStr);
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book past dates");
        }

        if (!availabilityRepository.findByDoctorIdAndDate(doctorId, dateStr).isPresent()) {
            createDefault(doctorId, dateStr);
        }

        Query query = new Query(Criteria.where("doctorId").is(doctorId)
                .and("date").is(dateStr)
                .and("slots").elemMatch(Criteria.where("time").is(time).and("booked").is(false)));

        Update update = new Update().set("slots.$.booked", true);
        var result = mongoTemplate.updateFirst(query, update, Availability.class);
        return result.getModifiedCount() > 0;
    }

    public void releaseSlot(String doctorId, String dateStr, String time) {
        Query query = new Query(Criteria.where("doctorId").is(doctorId)
                .and("date").is(dateStr)
                .and("slots.time").is(time));
        Update update = new Update().set("slots.$.booked", false);
        mongoTemplate.updateFirst(query, update, Availability.class);
    }

    public void seedForDoctor(String doctorId, int days) {
        LocalDate start = LocalDate.now();
        for (int i = 0; i < days; i++) {
            String dateStr = start.plusDays(i).toString();
            if (availabilityRepository.findByDoctorIdAndDate(doctorId, dateStr).isEmpty()) {
                createDefault(doctorId, dateStr);
            }
        }
    }

    public Availability addSlots(String doctorId, String dateStr, List<String> times) {
        LocalDate date = parseDate(dateStr);
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot add slots for past dates");
        }

        Availability avail = availabilityRepository.findByDoctorIdAndDate(doctorId, dateStr)
                .orElseGet(() -> {
                    Availability a = new Availability();
                    a.setDoctorId(doctorId);
                    a.setDate(dateStr);
                    a.setSlots(new ArrayList<>());
                    return a;
                });

        for (String time : times) {
            boolean exists = avail.getSlots().stream().anyMatch(s -> s.getTime().equals(time));
            if (!exists) avail.getSlots().add(new TimeSlot(time, false));
        }
        return availabilityRepository.save(avail);
    }

    public List<Availability> getSchedule(String doctorId, String from, String to) {
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        List<Availability> result = new ArrayList<>();
        for (LocalDate d = fromDate; !d.isAfter(toDate); d = d.plusDays(1)) {
            availabilityRepository.findByDoctorIdAndDate(doctorId, d.toString())
                    .ifPresent(result::add);
        }
        return result;
    }

    private Availability createDefault(String doctorId, String dateStr) {
        Availability avail = new Availability();
        avail.setDoctorId(doctorId);
        avail.setDate(dateStr);
        avail.setSlots(DEFAULT_SLOTS.stream()
                .map(t -> new TimeSlot(t, false))
                .collect(Collectors.toList()));
        return availabilityRepository.save(avail);
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new BadRequestException("Invalid date format. Use YYYY-MM-DD");
        }
    }
}
