CREATE TABLE user (
    ID INT PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE profile(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT default 0,
    description TEXT,
    species_ID INT default 1
);

CREATE TABLE profile_trait(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    profile_ID INT NOT NULL ,
    FOREIGN KEY (profile_ID) REFERENCES profile(ID),
    trait_ID INT NOT NULL,
    trait_specialization VARCHAR(50) DEFAULT ''
);

CREATE TABLE profile_mastery(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    profile_ID INT NOT NULL ,
    FOREIGN KEY (profile_ID) REFERENCES profile(ID),
    mastery_ID INT NOT NULL,
    level INT default 1
);

CREATE TABLE profile_mastery_specialization(
    ID INT PRIMARY KEY AUTO_INCREMENT,
    profile_mastery_ID INT NOT NULL,
    FOREIGN KEY (profile_mastery_ID) REFERENCES profile_mastery(ID),
    mastery_specialization_ID INT NOT NULL
);