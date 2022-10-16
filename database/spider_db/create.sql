create table if not exists project01.youtube_content
(
    id              int auto_increment primary key,
    title           varchar(200) null,
    channel_name    varchar(200) null,
    channel_id      varchar(100) null,
    video_id        varchar(100) null,
    view_count      bigint       null,
    like_count      bigint       null,
    dislike_count   bigint       null,
    published_time  datetime     null,
    comments        text         null,
    user_id         varchar(100) null,
    percent         double       null,
    keywords        varchar(200) null,
    subscribe_count bigint       null
) engine=spider
comment='wrapper "mysql", table "youtube_content"'
partition by key(id) (
 partition key1 comment = 'srv "master_db1"',
 partition key2 comment = 'srv "master_db2"'
);