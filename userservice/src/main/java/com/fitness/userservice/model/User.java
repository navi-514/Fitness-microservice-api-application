package com.fitness.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.print.attribute.standard.DateTimeAtCreation;
import java.time.LocalDateTime;

@Entity
@Data
@Table (name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;

    private String keycloakId;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;

    private UserRole role = UserRole.USER;
    @CreationTimestamp
    private  LocalDateTime createdAt;
    @UpdateTimestamp
    private  LocalDateTime updatedAt;


}
