package com.example.project01.youtube.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "channel")
@Getter
public class ChannelEntity {
    @Id
    private String id;
    @Column
    private String channelName;
    @Column
    private BigInteger subscribeCount;

    @OneToMany(mappedBy = "channel")
    private List<SubscribeEntity> subscribeEntityList;
}
