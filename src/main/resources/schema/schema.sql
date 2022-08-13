# TODO schema 적용이 안되는 문제 해결하기
CREATE TABLE IF NOT EXISTS refresh_token
(
    user_id VARCHAR(50) UNIQUE,
    refresh_token VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS youtube_content
(
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    channel_name VARCHAR(100) NOT NULL,
    channel_id VARCHAR(100) NOT NULL,
    video_id VARCHAR(100) NOT NULL,
    refresh_token VARCHAR(50) NOT NULL
);