package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "availability")
@CompoundIndex(name = "doctor_date", def = "{'doctorId': 1, 'date': 1}", unique = true)
public class Availability {

    @Id
    private String id;
    private String doctorId;
    private String date;
    private List<TimeSlot> slots = new ArrayList<>();
}
