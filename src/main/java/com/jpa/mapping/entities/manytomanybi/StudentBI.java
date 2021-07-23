package com.jpa.mapping.entities.manytomanybi;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "student_bi")
@Entity
@Data
public class StudentBI implements Serializable {
    private static final long serialVersionUID = 2120521838900311799L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToMany
    @JoinTable(name = "STUDENT_SUBJECT",
            joinColumns = @JoinColumn(name = "STUDENT_id"),
            inverseJoinColumns = @JoinColumn(name = "SUBJECT_id"))
    private List<SubjectBI> subjectBIS;

}