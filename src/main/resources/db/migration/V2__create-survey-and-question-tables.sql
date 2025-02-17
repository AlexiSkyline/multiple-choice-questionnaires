drop table if exists survey;
drop table if exists question;

create table survey (
    id varchar(36) NOT NULL,
    title varchar(50),
    image varchar(255) DEFAULT NULL,
    description text,
    max_points int,
    question_count int,
    category_id varchar(36) NOT NULL,
    is_active tinyint(1) DEFAULT 1,
    time_limit BIGINT,
    account_id varchar(36) NOT NULL,
    attempts int,
    is_public tinyint(1) DEFAULT 0,
    password varchar(60) DEFAULT NULL,
    created_at datetime(6),
    updated_at datetime(6),
    primary key (id),
    CONSTRAINT FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT FOREIGN KEY (account_id) REFERENCES account (id)
) engine=InnoDB;

create table question (
    id varchar(36) NOT NULL,
    content text,
    image varchar(255) null,
    points int,
    allowed_answers int,
    options text,
    correct_answers text,
    survey_id varchar(36) NOT NULL,
    created_at datetime(6),
    updated_at datetime(6),
    primary key (id),
    CONSTRAINT FOREIGN KEY (survey_id) REFERENCES survey (id)
) engine=InnoDB;