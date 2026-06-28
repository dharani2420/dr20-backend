package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "family_members")
public class FamilyMember {

    @Id
    private String id;
    private String userId;
    private String name;
    private String relation;
    private Integer age;
    private String gender;
    private String bloodGroup;
}
