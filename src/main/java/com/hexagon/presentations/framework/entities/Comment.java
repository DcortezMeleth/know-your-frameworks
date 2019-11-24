package com.hexagon.presentations.framework.entities;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private Post post;
}
