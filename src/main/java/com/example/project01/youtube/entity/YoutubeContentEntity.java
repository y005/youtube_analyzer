package com.example.project01.youtube.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "youtube_content")
@Getter
public class YoutubeContentEntity {
    @Id
    private String id;
    @Column
    private String title;
    @Column
    private BigInteger viewCount;
    @Column
    private BigInteger likeCount;
    @Column
    private BigInteger dislikeCount;
    @Column
    private Date publishedTime;
    @Column
    private String comments;
    @Column
    private Double percent;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private ChannelEntity channel;

    @OneToMany(mappedBy = "youtubeContent")
    private List<RelatedKeywordEntity> relatedKeywordEntityList;
}
