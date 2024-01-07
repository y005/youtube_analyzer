package com.example.project01.youtube.repository.impl;

import com.example.project01.youtube.dto.YoutubeContentSearchParam;
import com.example.project01.youtube.entity.*;
import com.example.project01.youtube.repository.YoutubeContentQdslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.List;


@Repository
public class YoutubeContentQdslRepositoryImpl implements YoutubeContentQdslRepository {
    private final JPAQueryFactory queryFactory;
    private final static QSubscribeEntity subscribeEntity = QSubscribeEntity.subscribeEntity;
    private final static QRelatedKeywordEntity relatedKeywordEntity = QRelatedKeywordEntity.relatedKeywordEntity;
    private final static QYoutubeContentEntity youtubeContentEntity = QYoutubeContentEntity.youtubeContentEntity;

    public YoutubeContentQdslRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.queryFactory = new JPAQueryFactory(entityManagerFactory.createEntityManager());
    }

    @Override
    public List<YoutubeContentEntity> findByParam(YoutubeContentSearchParam searchParam, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // 유저가 구독한 채널의 컨텐츠 조회
        if (searchParam.getUserId() != null) {
            booleanBuilder.and(youtubeContentEntity.channel.id.in(
                    queryFactory.select(subscribeEntity.channel.id)
                            .from(subscribeEntity).where(subscribeEntity.user.id.eq(searchParam.getUserId()))));
        }
        // 키워드와 관련된 컨텐츠 조회
        if (searchParam.getKeyword() != null) {
            booleanBuilder.and(youtubeContentEntity.id.in(
                    queryFactory.select(relatedKeywordEntity.youtubeContent.id)
                            .from(relatedKeywordEntity).where(relatedKeywordEntity.keyword.keyword.eq(searchParam.getKeyword()))));
        }
        JPAQuery<YoutubeContentEntity> query = queryFactory.selectFrom(youtubeContentEntity)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        return query.fetch();
    }
}
