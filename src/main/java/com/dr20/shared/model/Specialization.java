package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "specializations")
public class Specialization {

    @Id
    private String id;

    private String name;        // e.g. "Cardiology"
    private String icon;        // emoji or image URL
    private String description;
}
