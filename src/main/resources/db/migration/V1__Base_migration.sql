CREATE TABLE plugin_user (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    minecraft_UUID VARCHAR(36) UNIQUE NOT NULL,
    minecraft_name VARCHAR(60) NOT NULL
);

CREATE TABLE profile(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    age INT default 0,
    description TEXT,
    species_ID INT default 1,
    user_ID BIGINT,
    FOREIGN KEY (user_ID) REFERENCES plugin_user(ID)
);

ALTER TABLE plugin_user ADD active_profile_ID BIGINT;
ALTER TABLE plugin_user ADD FOREIGN KEY (active_profile_ID) REFERENCES profile(ID);

CREATE TABLE profile_trait(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_ID BIGINT,
    FOREIGN KEY (profile_ID) REFERENCES profile(ID),
    trait_ID INT NOT NULL,
    trait_specialization VARCHAR(50) DEFAULT ''
);

CREATE TABLE profile_mastery(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_ID BIGINT UNIQUE,
    FOREIGN KEY (profile_ID) REFERENCES profile(ID),
    mastery_ID INT,
    level INT default 1
);

CREATE TABLE profile_mastery_specialization(
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_mastery_ID BIGINT,
    FOREIGN KEY (profile_mastery_ID) REFERENCES profile_mastery(ID),
    specialization_ID INT
);