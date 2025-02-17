drop table if exists result;
drop table if exists answer;

create table result (
    id varchar(36) NOT NULL,
    account_id varchar(36) NOT NULL,
	survey_id varchar(36) NOT NULL,
	start_time datetime(6),
	end_time datetime(6),
	duration BIGINT,
	total_points int,
	correct_answers int,
	incorrect_answers int,
    created_at datetime(6),
    primary key (id),
    CONSTRAINT FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT FOREIGN KEY (survey_id) REFERENCES survey (id)
) engine=InnoDB;

create table answer (
    id varchar(36) NOT NULL,
    account_id varchar(36) NOT NULL,
    question_id varchar(36) NOT NULL,
    result_id varchar(36) NOT NULL,
    user_answers text,
    is_correct tinyint(1),
    points int,
    created_at datetime(6),
    primary key (id),
    CONSTRAINT FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT FOREIGN KEY (question_id) REFERENCES question (id),
    CONSTRAINT FOREIGN KEY (result_id) REFERENCES result (id)
) engine=InnoDB;