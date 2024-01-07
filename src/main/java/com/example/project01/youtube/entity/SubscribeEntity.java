package com.example.project01.youtube.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subscribe")
@IdClass(SubscribeEntity.SubscribeEntityId.class)
public class SubscribeEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private ChannelEntity channel;

    @NoArgsConstructor
    @EqualsAndHashCode
    public static class SubscribeEntityId implements Serializable {
        private String user;
        private String channel;
    }
}
