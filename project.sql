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

USE project;

-- accounts
INSERT INTO account (username, password, user_type) VALUES
('student1', 'pass123', 'student'),
('student2', 'pass123', 'student'),
('prof1', 'pass123', 'professor'),
('prof2', 'pass123', 'professor'),
('prof3', 'pass123', 'professor'),
('public1', 'pass123', 'public');

-- students
INSERT INTO student (account_id, first_name, last_name, email, gpa) VALUES
(1, 'Alice', 'Smith', 'alice@example.com', 3.8),
(2, 'Bob', 'Johnson', 'bob@example.com', 3.5);

-- professors
INSERT INTO professor (account_id, first_name, last_name, building_number, office_number, email) VALUES
(3, 'Dr.', 'Anderson', 'B1', '101', 'anderson@example.com'),
(4, 'Dr.', 'Brown', 'B2', '102', 'brown@example.com'),
(5, 'Dr.', 'Clark', 'B3', '103', 'clark@example.com');

-- public users
INSERT INTO public (account_id, first_name, last_name, email) VALUES
(6, 'Charlie', 'Davis', 'charlie@example.com');

-- keywords
INSERT INTO keyword (keyword) VALUES
('Java'),
('C'),
('Statistics'),
('Python'),
('SQL'),
('HTML'),
('Machine Learning'),
('Networking'),
('Security');

-- student keywords
INSERT INTO student_keyword (account_id, keyword_id) VALUES
(1, 1), -- Alice: Java
(1, 5), -- Alice: SQL
(2, 4), -- Bob: Python
(2, 5); -- Bob: SQL

-- professor keywords
INSERT INTO professor_keyword (account_id, keyword_id) VALUES
(3, 1), -- Anderson: Java
(3, 7), -- Anderson: ML
(4, 4), -- Brown: Python
(4, 5), -- Brown: SQL
(5, 5), -- Clark: SQL
(5, 6); -- Clark: HTML

-- abstracts
INSERT INTO abstract (title, abstract_text) VALUES
('Neural Network Optimization Techniques', 'Optimizing neural networks.'),
('Secure Distributed Database Systems', 'Securing distributed DBs.'),
('Machine Learning for Network Intrusion Detection', 'ML for intrusion detection.');

-- abstract keywords
INSERT INTO abstract_keyword (abstract_id, keyword_id) VALUES
(1, 1), -- Neural Net: Java
(1, 2), -- Neural Net: C
(1, 3), -- Neural Net: Stats
(2, 4), -- Secure DB: Python
(2, 5), -- Secure DB: SQL
(2, 6), -- Secure DB: HTML
(3, 1), -- ML Intrusion: Java
(3, 5), -- ML Intrusion: SQL
(3, 7); -- ML Intrusion: ML

-- professor abstracts
INSERT INTO professor_abstract (account_id, abstract_id) VALUES
(3, 1), -- Anderson: Neural Net
(4, 2), -- Brown: Secure DB
(5, 3); -- Clark: ML Intrusion
