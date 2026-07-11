package com.dr20.patient.service;

import com.dr20.common.enums.AppointmentStatus;
import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.Review;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.ReviewRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public Review submit(String userId, String appointmentId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appt.getUserId().equals(userId)) {
            throw new BadRequestException("Not your appointment");
        }
        if (appt.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Can only review completed appointments");
        }
        if (reviewRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BadRequestException("Already reviewed this appointment");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = new Review();
        review.setDoctorId(appt.getDoctorId());
        review.setUserId(userId);
        review.setAppointmentId(appointmentId);
        review.setPatientName(user.getFirstName() != null
                ? user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "")
                : appt.getPatientName());
        review.setRating(rating);
        review.setComment(comment);
        Review saved = reviewRepository.save(review);

        updateDoctorRating(appt.getDoctorId());
        return saved;
    }

    public List<Review> getByDoctor(String doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    public Map<String, Object> getDoctorReviewSummary(String doctorId) {
        List<Review> reviews = getByDoctor(doctorId);
        Map<String, Object> summary = new HashMap<>();
        summary.put("reviews", reviews);
        summary.put("totalReviews", reviews.size());
        if (reviews.isEmpty()) {
            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            summary.put("averageRating", doctor != null ? doctor.getRating() : 0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            summary.put("averageRating", Math.round(avg * 10.0) / 10.0);
        }
        return summary;
    }

    private void updateDoctorRating(String doctorId) {
        List<Review> reviews = reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
        if (reviews.isEmpty()) return;
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        doctorRepository.findById(doctorId).ifPresent(d -> {
            d.setRating(Math.round(avg * 10.0) / 10.0);
            d.setTotalConsultations(reviews.size());
            doctorRepository.save(d);
        });
    }
}
