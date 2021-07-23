package com.jpa.mapping.entities.onetoonebi;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "user_bi")
@Entity
@Data
public class UserBI implements Serializable {

    private static final long serialVersionUID = 2716626453441931131L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @OneToOne
    @JoinColumn(name = "user_profile_id")
    private UserProfileBI userProfileBI;
}