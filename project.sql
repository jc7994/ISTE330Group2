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

-- abstract_keyword junction (many-to-many for abstracts and keywords)
DROP TABLE IF EXISTS abstract_keyword;
CREATE TABLE abstract_keyword (
    abstract_id INT(11) NOT NULL,
    keyword_id INT(11) NOT NULL,
    PRIMARY KEY (abstract_id, keyword_id),
    CONSTRAINT fk_abs_key_abstract FOREIGN KEY (abstract_id)
        REFERENCES abstract(abstract_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_abs_key_keyword FOREIGN KEY (keyword_id)
        REFERENCES keyword(keyword_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
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

-- MOCK DATA

-- account creation
INSERT INTO account (username, password, user_type) VALUES
('mock_student_1', 'pass123', 'student'),
('mock_student_2', 'pass234', 'student'),
('mock_public', 'p@ss', 'public'),
('mock_prof_1', '123pass', 'professor'),
('mock_prof_2', '234pass', 'professor'),
('mock_prof_3', '345pass', 'professor');

-- student
INSERT INTO student (account_id, first_name, last_name, email, gpa) VALUES
(1, 'John', 'Doe', 'jdoe@uni.edu', 3.40),
(2, 'Jane', 'Matthews', 'jmat@uni.edu', 3.75);

-- professors
INSERT INTO professor (account_id, first_name, last_name, building_number, office_number, email) VALUES
(4, 'Alan', 'Smith', '12', '205', 'asmith@uni.edu'),
(5, 'Betty', 'Jones', '10', '314', 'bjones@uni.edu'),
(6, 'Carol', 'Lee', '22', '127', 'clee@uni.edu');

-- public
INSERT INTO public (account_id, first_name, last_name, email) VALUES
(3, 'random', 'person', 'rm@gmail.com');

-- keywords
INSERT INTO keyword (keyword) VALUES
('Java'),
('C'),
('Python'),
('C#'),
('SQL'),
('Machine Learning'),
('HTML'),
('Data Mining'),
('Statistics'),
('Neural Networks');

-- student keywords
INSERT INTO student_keyword (account_id, keyword_id) VALUES
(1, 1),
(1, 4),
(1, 8),
(2, 3),
(2, 5);

-- professor keywords
INSERT INTO professor_keyword (account_id, keyword_id) VALUES
(4, 1),
(4, 9),
(5, 3),
(5, 7),
(6, 5),
(6, 6);

INSERT INTO abstract (title, abstract_text) VALUES
('Neural Network Optimization Techniques',
 'This paper explores approaches for improving training time and accuracy of deep neural architectures.'),
('Secure Distributed Database Systems',
 'We present a new approach to designing distributed database storage with enhanced cyber protections.'),
('Machine Learning for Network Intrusion Detection',
 'Using ML models, this study analyzes live network data to predict potential security breaches.');

INSERT INTO abstract_keyword (abstract_id, keyword_id) VALUES
(1, 1),
(1, 2),
(1, 9);

INSERT INTO abstract_keyword (abstract_id, keyword_id) VALUES
(2, 3),
(2, 7),
(2, 5);

INSERT INTO abstract_keyword (abstract_id, keyword_id) VALUES
(3, 1),
(3, 5),
(3, 6);

-- FIX:
-- INSERT INTO professor_abstract (account_id, abstract_id) VALUES
-- (4, 1),
-- (6, 1),
-- (5, 2),
-- (6, 3),
-- (4, 3);