package com.example.project01.youtube.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "related_keyword")
@IdClass(RelatedKeywordEntity.RelatedKeywordEntityId.class)
@Getter
public class RelatedKeywordEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "keyword", referencedColumnName = "keyword")
    private KeywordEntity keyword;

    @Id
    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id")
    private YoutubeContentEntity youtubeContent;

    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RelatedKeywordEntityId implements Serializable {
        private String keyword;
        private String youtubeContent;
    }
}
