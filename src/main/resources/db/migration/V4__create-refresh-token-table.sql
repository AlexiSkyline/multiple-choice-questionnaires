drop table if exists refresh_token;

create table refresh_token (
    id varchar(36) NOT NULL,
    token varchar(255) NOT NULL,
    expiry_date datetime(6) NOT NULL,
    account_id varchar(36) NOT NULL,
    primary key (id),
    CONSTRAINT FOREIGN KEY (account_id) REFERENCES account (id)
) engine=InnoDB;