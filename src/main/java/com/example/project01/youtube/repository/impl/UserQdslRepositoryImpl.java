package com.example.project01.youtube.repository.impl;

import com.example.project01.youtube.dto.UserSearchParam;
import com.example.project01.youtube.entity.QUserEntity;
import com.example.project01.youtube.entity.UserEntity;
import com.example.project01.youtube.repository.UserQdslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public class UserQdslRepositoryImpl implements UserQdslRepository {
    private final JPAQueryFactory queryFactory;
    private final static QUserEntity userEntity = QUserEntity.userEntity;

    public UserQdslRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.queryFactory = new JPAQueryFactory(entityManagerFactory.createEntityManager());
    }

    @Override
    public List<UserEntity> findByParam(UserSearchParam searchParam, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (searchParam.getUserId() != null) {
            booleanBuilder.and(userEntity.id.eq(searchParam.getUserId()));
        }
        if (searchParam.getUserPassword() != null) {
            booleanBuilder.and(userEntity.password.eq(searchParam.getUserPassword()));
        }
        JPAQuery<UserEntity> query = queryFactory.selectFrom(userEntity)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        return query.fetch();
    }

    @Override
    public void save(UserEntity userEntity) {
    }
}
