package com.jpa.mapping.entities.onetomanybi;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "post_bi")
@Entity
@Data
public class PostBI implements Serializable {
    private static final long serialVersionUID = 5298653874723926912L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostCommentBI> postCommentBIS;

}