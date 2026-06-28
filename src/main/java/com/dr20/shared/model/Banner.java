package com.dr20.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "banners")
public class Banner {

    @Id
    private String id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private boolean active = true;
}
