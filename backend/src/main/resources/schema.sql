-- 기존 테이블 삭제
DROP TABLE IF EXISTS members;

-- 회원 테이블 생성
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    kakao_id BIGINT NOT NULL UNIQUE,
    email VARCHAR(100),
    nickname VARCHAR(50),
    profile_image_url VARCHAR(500),
    thumbnail_image_url VARCHAR(500),
    is_default_image BOOLEAN,
    is_default_nickname BOOLEAN,
    created_at TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_members_kakao_id ON members(kakao_id);
CREATE INDEX idx_members_email ON members(email);