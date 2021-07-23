package com.jpa.mapping.entities.onetomanyuni;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "post")
@Entity
@Data
public class Post implements Serializable {
    private static final long serialVersionUID = 5298653874723926912L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostComment> postComments;

}