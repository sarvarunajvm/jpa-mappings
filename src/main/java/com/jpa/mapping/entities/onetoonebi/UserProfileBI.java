package com.jpa.mapping.entities.onetoonebi;

import com.jpa.mapping.entities.enums.Gender;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Table(name = "user_profile_bi")
@Entity
@Data
public class UserProfileBI implements Serializable {

    private static final long serialVersionUID = 2716626453441931131L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @OneToOne(mappedBy = "userProfileBI", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private UserBI userBI;
}