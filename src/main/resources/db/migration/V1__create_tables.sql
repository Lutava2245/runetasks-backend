CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE avatars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL UNIQUE,
    icon_name VARCHAR(255) NOT NULL UNIQUE,
    price INT NOT NULL
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    avatar_id BIGINT NOT NULL,
    total_xp INT DEFAULT 0,
    total_coins INT DEFAULT 0,
    level INT DEFAULT 1,
    progress_xp INT DEFAULT 0,
    created_at DATE NOT NULL,
    
    CONSTRAINT fk_users_current_avatar FOREIGN KEY (avatar_id) REFERENCES avatars(id)
);

CREATE TABLE user_avatars (
    user_id BIGINT NOT NULL,
    avatar_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, avatar_id),

    CONSTRAINT fk_ua_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ua_avatar FOREIGN KEY (avatar_id) REFERENCES avatars(id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon_name VARCHAR(255) NOT NULL,
    total_xp INT DEFAULT 0,
    level INT DEFAULT 1,
    progress_xp INT DEFAULT 0,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_skills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    difficulty VARCHAR(20) NOT NULL,
    date DATE NOT NULL,
    repeat_type VARCHAR(20) DEFAULT 'NONE',
    user_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,

    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

CREATE TABLE rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price INT NOT NULL,
    status VARCHAR(20),
    user_id BIGINT NOT NULL,
    
    CONSTRAINT fk_rewards_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);