DROP DATABASE IF EXISTS  project; 

CREATE DATABASE project;
USE project;

-- users

DROP TABLE IF EXISTS account
CREATE TABLE account(
    account_id INT(11) NOT NULL AUTO_INCREMENT, -- PK for account
    username VARCHAR(45) NOT NULL,
    password VARCHAR(150) NOT NULL,
    student_id INT(11) DEFAULT NULL, -- FK to student
    professor_id INT(11) DEFAULT NULL, -- FK to professor
    public_id INT(11) DEFAULT NULL,  -- FK to public
    PRIMARY KEY (account_id).
    UNIQUE KEY username_UNIQUE (username), 
    UNIQUE KEY student_id_UNIQUE (student_id), -- one student maps to one account
    UNIQUE KEY professor_id_UNIQUE (professor_id), -- one professor maps to one account
    UNIQUE KEY public_id_UNIQUE (public_id), -- one public maps to one account
    CONSTRAINT fk_account_student FOREIGN KEY (student_id)
        REFERENCES student(student_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_account_professor FOREIGN KEY (professor_id)
        REFERENCES professor(profesor_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_account_public FOREIGN KEY (public_id)
        REFERENCES public(public_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS professor
CREATE TABLE professor(
    professor_id INT(11) NOT NULL AUTO_INCREMENT, -- PK for professor
    first_name VARCHAR(45) NOT NULL, 
    last_name VARCHAR(45) NOT NULL,
    building_number VARCHAR(5) DEFAULT NULL,
    office_number VARCHAR(5) DEFAULT NULL,
    email VARCHAR(45) NOT NULL,
    PRIMARY KEY (profesor_id),
    UNIQUE KEY email_UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS student
CREATE TABLE student(
    student_id INT(11) NOT NULL AUTO_INCREMENT, -- PK for student
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    gpa DECIMAL(3, 2) DEFAULT NULL,
    PRIMARY KEY (student_id),
    UNIQUE KEY email_UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS public
CREATE TABLE public(
    public_id INT(11) NOT NULL AUTO_INCREMENT, -- PK for public
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    PRIMARY KEY (public_id),
    UNIQUE KEY email_UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- keywords
DROP TABLE IF EXISTS keyword
CREATE TABLE keyword(
    keyword_id INT(11) NOT NULL AUTO_INCREMENT, -- PK for keyword
    keyword VARCHAR(25) NOT NULL,
    PRIMARY KEY (keyword_id),
    UNIQUE KEY keyword_UNIQUE (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- abstract
DROP TABLE IF EXISTS abstract
CREATE TABLE abstract(
    abstract_id INT(11) NOT NULL AUTO_INCREMENT, -- PK for abstract
    title VARCHAR(100) NOT NULL,
    abstract VARCHAR(800) NOT NULL,
    PRIMARY KEY (abstract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- junction tables
DROP TABLE IF EXISTS professor_keyword
CREATE TABLE professor_keyword(
    profesor_id INT(11) NOT NULL,
    keyword_id INT(11) NOT NULL,
    PRIMARY KEY (professor_id, keyword_id),
    CONSTRAINT fk_prof_key_prof_id FOREIGN KEY (professor_id)
        REFERENCES professor(professor_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_prof_key_key_id FOREIGN KEY (keyword_id)
        REFERENCES keyword(keyword_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS student_keyword
CREATE TABLE student_keyword(
    student_id INT(11) NOT NULL,
    keyword_id INT(11) NOT NULL,
    PRIMARY KEY (student_id, keyword_id),
    CONSTRAINT fk_student_key_prof_id FOREIGN KEY (student_id)
        REFERENCES student(student_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_student_key_key_id FOREIGN KEY (keyword_id)
        REFERENCES keyword(keyword_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS professor_abstract
CREATE TABLE professor_abstract(
    professor_id INT(11) NOT NULL, -- PK/FK
    abstract_id INT(11) NOT NULL, -- PK/FK
    PRIMARY KEY (professor_id, abstract_id),
        CONSTRAINT fk_prof_abstract_prof_id FOREIGN KEY (professor_id)
        REFERENCES professor(professor_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT fk_prof_abstract_abstract_id FOREIGN KEY (abstract_id)
        REFERENCES abstract(abstract_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
