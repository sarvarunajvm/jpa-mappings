package com.jpa.mapping.entities.onetomanyuni;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "post_comment")
@Entity
@Data
public class PostComment implements Serializable {
    private static final long serialVersionUID = 8857607191367864042L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "review")
    private String review;

}