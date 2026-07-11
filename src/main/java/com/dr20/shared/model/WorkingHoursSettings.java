package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "working_hours_settings")
public class WorkingHoursSettings {

    @Id
    private String id;
    private String doctorId;
    private List<String> workingDays;
    private String morningStart;
    private String morningEnd;
    private String eveningStart;
    private String eveningEnd;
    private Integer slotDurationMinutes = 30;
}
