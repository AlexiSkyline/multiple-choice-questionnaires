drop table if exists user_role;
drop table if exists category;
drop table if exists role;
drop table if exists user;

create table user (
      id varchar(36) NOT NULL,
      first_name varchar(255),
      last_name varchar(255),
      username varchar(20),
      email varchar(255),
      password varchar(60),
      profile_image varchar(255),
      description text,
      is_active tinyint(1) DEFAULT 1,
      created_at datetime(6),
      updated_at datetime(6),
      primary key (id)
) engine=InnoDB;

create table category (
      id varchar(36) NOT NULL,
      title varchar(50),
      image varchar(255),
      description text,
      user_id varchar(36),
      is_active tinyint(1) DEFAULT 1,
      created_at datetime(6),
      updated_at datetime(6),
      primary key (id),
      FOREIGN KEY (user_id) REFERENCES user (id)
) engine=InnoDB;

create table role (
      id varchar(36) NOT NULL,
      name varchar(50),
      description text,
      is_active tinyint(1) DEFAULT 1,
      created_at datetime(6),
      updated_at datetime(6),
      primary key (id)
) engine=InnoDB;

create table user_role (
       user_id varchar(36) NOT NULL,
       role_id varchar(36) NOT NULL,
       primary key (user_id, role_id),
       constraint fk_user FOREIGN KEY (user_id) references user (id),
       constraint fk_role FOREIGN KEY (role_id) references role (id)
) engine=InnoDB;