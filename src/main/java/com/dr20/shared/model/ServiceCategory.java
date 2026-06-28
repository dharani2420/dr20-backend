package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "service_categories")
public class ServiceCategory {

    @Id
    private String id;
    private String name;
    private String icon;
    private String type; // CONSULTATION, CARE, SYMPTOM
}
