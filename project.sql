DROP DATABASE IF EXISTS project;
CREATE DATABASE project;
USE project;

-- account table
DROP TABLE IF EXISTS account;
CREATE TABLE account (
    account_id INT(11) NOT NULL AUTO_INCREMENT, 
    username VARCHAR(45) NOT NULL,
    password VARCHAR(150) NOT NULL,
    user_type ENUM('student','professor','public') NOT NULL,
    PRIMARY KEY (account_id),
    UNIQUE KEY username_UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- student table (account_id is PK and FK to account)
DROP TABLE IF EXISTS student;
CREATE TABLE student (
    account_id INT(11) NOT NULL,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    gpa DECIMAL(3,2) DEFAULT NULL,
    PRIMARY KEY (account_id),
    UNIQUE KEY email_UNIQUE (email),
    CONSTRAINT fk_student_account FOREIGN KEY (account_id)
        REFERENCES account(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- professor table (account_id is PK and FK to account)
DROP TABLE IF EXISTS professor;
CREATE TABLE professor (
    account_id INT(11) NOT NULL,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    building_number VARCHAR(5) DEFAULT NULL,
    office_number VARCHAR(5) DEFAULT NULL,
    email VARCHAR(45) NOT NULL,
    PRIMARY KEY (account_id),
    UNIQUE KEY email_UNIQUE (email),
    CONSTRAINT fk_prof_account FOREIGN KEY (account_id)
        REFERENCES account(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- public table (account_id is PK and FK to account)
DROP TABLE IF EXISTS public;
CREATE TABLE public (
    account_id INT(11) NOT NULL,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
    PRIMARY KEY (account_id),
    UNIQUE KEY email_UNIQUE (email),
    CONSTRAINT fk_public_account FOREIGN KEY (account_id)
        REFERENCES account(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- keyword table
DROP TABLE IF EXISTS keyword;
CREATE TABLE keyword (
    keyword_id INT(11) NOT NULL AUTO_INCREMENT,
    keyword VARCHAR(25) NOT NULL,
    PRIMARY KEY (keyword_id),
    UNIQUE KEY keyword_UNIQUE (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- professor_keyword junction
DROP TABLE IF EXISTS professor_keyword;
CREATE TABLE professor_keyword (
    account_id INT(11) NOT NULL,
    keyword_id INT(11) NOT NULL,
    PRIMARY KEY (account_id, keyword_id),
    CONSTRAINT fk_prof_key_account FOREIGN KEY (account_id)
        REFERENCES professor(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_prof_key_keyword FOREIGN KEY (keyword_id)
        REFERENCES keyword(keyword_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- student_keyword junction
DROP TABLE IF EXISTS student_keyword;
CREATE TABLE student_keyword (
    account_id INT(11) NOT NULL,
    keyword_id INT(11) NOT NULL,
    PRIMARY KEY (account_id, keyword_id),
    CONSTRAINT fk_student_key_account FOREIGN KEY (account_id)
        REFERENCES student(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_student_key_keyword FOREIGN KEY (keyword_id)
        REFERENCES keyword(keyword_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- public_keyword junction (optional)
DROP TABLE IF EXISTS public_keyword;
CREATE TABLE public_keyword (
    account_id INT(11) NOT NULL,
    keyword_id INT(11) NOT NULL,
    PRIMARY KEY (account_id, keyword_id),
    CONSTRAINT fk_public_key_account FOREIGN KEY (account_id)
        REFERENCES public(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_public_key_keyword FOREIGN KEY (keyword_id)
        REFERENCES keyword(keyword_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- abstract table
DROP TABLE IF EXISTS abstract;
CREATE TABLE abstract (
    abstract_id INT(11) NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    abstract_text VARCHAR(800) NOT NULL,
    PRIMARY KEY (abstract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- professor_abstract junction (multiple authors)
DROP TABLE IF EXISTS professor_abstract;
CREATE TABLE professor_abstract (
    account_id INT(11) NOT NULL,
    abstract_id INT(11) NOT NULL,
    PRIMARY KEY (account_id, abstract_id),
    CONSTRAINT fk_prof_abstract_account FOREIGN KEY (account_id)
        REFERENCES professor(account_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_prof_abstract_abstract FOREIGN KEY (abstract_id)
        REFERENCES abstract(abstract_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
