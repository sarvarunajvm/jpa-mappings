package com.jpa.mapping.entities.manytomanyuni;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "subject")
@Entity
@Data
public class Subject implements Serializable {
    private static final long serialVersionUID = -7643718736515595344L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

}