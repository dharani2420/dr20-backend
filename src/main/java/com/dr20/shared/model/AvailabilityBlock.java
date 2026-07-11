package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "availability_blocks")
public class AvailabilityBlock {

    @Id
    private String id;
    private String doctorId;
    private String date;
    private boolean fullDay = true;
    private String halfDayPeriod; // MORNING or EVENING when fullDay=false
}
