package com.hexagon.presentations.framework.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "text")
    private String text;

    @Column(name = "score")
    private Integer score;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}
