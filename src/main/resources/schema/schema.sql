# TODO schema 적용이 안되는 문제 해결하기
create table if not exists refresh_token
(
    user_id VARCHAR(50) UNIQUE,
    refresh_token VARCHAR(200) NOT NULL
);

create table if not exists youtube_content
(
    id int primary key auto_increment,
    title varchar(200),
    channel_name varchar(200),
    channel_id varchar(100),
    video_id varchar(100),
    view_count bigint,
    like_count bigint,
    dislike_count bigint,
    published_time datetime,
    comments TEXT,
    user_id varchar(100)
);

create table if not exists user
(
    id int primary key auto_increment,
    user_id varchar(100) not null
);