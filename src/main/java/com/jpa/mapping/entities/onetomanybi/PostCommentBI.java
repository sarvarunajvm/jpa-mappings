package com.jpa.mapping.entities.onetomanybi;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "post_comment_bi")
@Entity
@Data
public class PostCommentBI implements Serializable {
    private static final long serialVersionUID = 8857607191367864042L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "review")
    private String review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_bi_id")
    private PostBI postBI;

}