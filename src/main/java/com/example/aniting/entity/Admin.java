package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ADMIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @Column(name = "ADMIN_ID", nullable = false, length = 255)
    private String adminId;

    @Column(name = "PASSWD", nullable = false, length = 255)
    private String passwd;
}


