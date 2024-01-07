package com.example.project01.youtube.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "keyword")
@Getter
public class KeywordEntity {
    @Id
    private String keyword;

    @OneToMany(mappedBy = "keyword")
    private List<RelatedKeywordEntity> relatedKeywordEntities;
}
