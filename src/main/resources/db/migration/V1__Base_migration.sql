CREATE TABLE user (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    minecraft_UUID VARCHAR(36) UNIQUE NOT NULL,
    minecraft_name VARCHAR(60) NOT NULL
);

CREATE TABLE profile(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    age INT default 0,
    description TEXT,
    species_ID INT default 1,
    user_ID INT,
    FOREIGN KEY (user_ID) REFERENCES user(ID)
);

ALTER TABLE user ADD active_profile_ID INT;
ALTER TABLE user ADD FOREIGN KEY (active_profile_ID) REFERENCES profile(ID);

CREATE TABLE profile_trait(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    profile_ID INT,
    FOREIGN KEY (profile_ID) REFERENCES profile(ID),
    trait_ID INT NOT NULL,
    trait_specialization VARCHAR(50) DEFAULT ''
);

CREATE TABLE profile_mastery(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    profile_ID INT UNIQUE,
    FOREIGN KEY (profile_ID) REFERENCES profile(ID),
    mastery_ID INT,
    level INT default 1
);