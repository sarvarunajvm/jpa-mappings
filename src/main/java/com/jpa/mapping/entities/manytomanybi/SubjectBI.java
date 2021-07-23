package com.jpa.mapping.entities.manytomanybi;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "subject_bi")
@Entity
@Data
public class SubjectBI implements Serializable {
    private static final long serialVersionUID = -7643718736515595344L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "subjectBIS")
    private List<StudentBI> studentBIS;

}