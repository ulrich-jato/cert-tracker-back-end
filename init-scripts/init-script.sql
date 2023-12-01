USE certificatetracker;
CREATE TABLE certificatetracker.roles IF NOT EXISTS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

INSERT INTO roles (name) VALUES ('ROLE_USER'),('ROLE_MODERATOR'), ('ROLE_ADMIN');