# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table article (
  id                            bigint auto_increment not null,
  code                          varchar(255),
  url                           varchar(255),
  title                         varchar(255),
  remark                        clob,
  category_id                   bigint,
  details                       clob,
  created_by_id                 bigint,
  updated_by_id                 bigint,
  created_on                    timestamp,
  updated_on                    timestamp,
  visited                       integer not null,
  status                        integer not null,
  write_flag                    boolean default false not null,
  single                        boolean default false not null,
  constraint uq_article_code unique (code),
  constraint uq_article_url unique (url),
  constraint pk_article primary key (id)
);

create table article_category (
  id                            bigint auto_increment not null,
  code                          varchar(255),
  name                          varchar(255),
  remark                        varchar(255),
  created_by_id                 bigint,
  created_on                    varchar(255),
  constraint uq_article_category_code unique (code),
  constraint pk_article_category primary key (id)
);

create table article_comment (
  id                            bigint auto_increment not null,
  code                          varchar(255),
  article_id                    bigint,
  parent_id                     bigint,
  details                       clob,
  created_by                    varchar(255),
  created_on                    timestamp,
  constraint uq_article_comment_code unique (code),
  constraint pk_article_comment primary key (id)
);

create table article_draft (
  id                            bigint auto_increment not null,
  article_id                    bigint not null,
  category_id                   bigint,
  title                         varchar(255),
  remark                        clob,
  details                       clob,
  constraint uq_article_draft_article_id unique (article_id),
  constraint pk_article_draft primary key (id)
);

create table blog (
  id                            bigint auto_increment not null,
  code                          varchar(255),
  title                         varchar(255),
  remark                        varchar(255),
  category_id                   bigint,
  details                       clob,
  created_by                    varchar(255),
  created_on                    varchar(255),
  constraint uq_blog_code unique (code),
  constraint pk_blog primary key (id)
);

create table blog_category (
  id                            bigint auto_increment not null,
  code                          varchar(255),
  name                          varchar(255),
  remark                        varchar(255),
  created_by                    varchar(255),
  created_on                    varchar(255),
  constraint uq_blog_category_code unique (code),
  constraint pk_blog_category primary key (id)
);

create table page (
  id                            bigint auto_increment not null,
  template_id                   bigint,
  constraint uq_page_template_id unique (template_id),
  constraint pk_page primary key (id)
);

create table page_template (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  constraint pk_page_template primary key (id)
);

create table roles (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  code                          varchar(255),
  constraint uq_roles_name unique (name),
  constraint pk_roles primary key (id)
);

create table users (
  id                            bigint auto_increment not null,
  username                      varchar(255),
  password                      varchar(255),
  email                         varchar(255),
  nickname                      varchar(255),
  constraint uq_users_username unique (username),
  constraint uq_users_email unique (email),
  constraint uq_users_nickname unique (nickname),
  constraint pk_users primary key (id)
);

create index ix_article_category_id on article (category_id);
alter table article add constraint fk_article_category_id foreign key (category_id) references article_category (id) on delete restrict on update restrict;

create index ix_article_created_by_id on article (created_by_id);
alter table article add constraint fk_article_created_by_id foreign key (created_by_id) references users (id) on delete restrict on update restrict;

create index ix_article_updated_by_id on article (updated_by_id);
alter table article add constraint fk_article_updated_by_id foreign key (updated_by_id) references users (id) on delete restrict on update restrict;

create index ix_article_category_created_by_id on article_category (created_by_id);
alter table article_category add constraint fk_article_category_created_by_id foreign key (created_by_id) references users (id) on delete restrict on update restrict;

create index ix_article_comment_article_id on article_comment (article_id);
alter table article_comment add constraint fk_article_comment_article_id foreign key (article_id) references article (id) on delete restrict on update restrict;

create index ix_article_comment_parent_id on article_comment (parent_id);
alter table article_comment add constraint fk_article_comment_parent_id foreign key (parent_id) references article_comment (id) on delete restrict on update restrict;

alter table article_draft add constraint fk_article_draft_article_id foreign key (article_id) references article (id) on delete restrict on update restrict;

create index ix_article_draft_category_id on article_draft (category_id);
alter table article_draft add constraint fk_article_draft_category_id foreign key (category_id) references article_category (id) on delete restrict on update restrict;

create index ix_blog_category_id on blog (category_id);
alter table blog add constraint fk_blog_category_id foreign key (category_id) references blog_category (id) on delete restrict on update restrict;

alter table page add constraint fk_page_template_id foreign key (template_id) references page_template (id) on delete restrict on update restrict;


# --- !Downs

alter table article drop constraint if exists fk_article_category_id;
drop index if exists ix_article_category_id;

alter table article drop constraint if exists fk_article_created_by_id;
drop index if exists ix_article_created_by_id;

alter table article drop constraint if exists fk_article_updated_by_id;
drop index if exists ix_article_updated_by_id;

alter table article_category drop constraint if exists fk_article_category_created_by_id;
drop index if exists ix_article_category_created_by_id;

alter table article_comment drop constraint if exists fk_article_comment_article_id;
drop index if exists ix_article_comment_article_id;

alter table article_comment drop constraint if exists fk_article_comment_parent_id;
drop index if exists ix_article_comment_parent_id;

alter table article_draft drop constraint if exists fk_article_draft_article_id;

alter table article_draft drop constraint if exists fk_article_draft_category_id;
drop index if exists ix_article_draft_category_id;

alter table blog drop constraint if exists fk_blog_category_id;
drop index if exists ix_blog_category_id;

alter table page drop constraint if exists fk_page_template_id;

drop table if exists article;

drop table if exists article_category;

drop table if exists article_comment;

drop table if exists article_draft;

drop table if exists blog;

drop table if exists blog_category;

drop table if exists page;

drop table if exists page_template;

drop table if exists roles;

drop table if exists users;

