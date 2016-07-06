----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

----------------------------------------------------------------
-- connecte en PTG_REF_PRIME
----------------------------------------------------------------
create sequence PTG_S_REF_PRIME 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_REF_PRIME for PTG_S_REF_PRIME;
grant select on PTG_S_REF_PRIME to R_PTG_USR;

create table PTG_REF_PRIME
(
  ID_REF_PRIME NUMBER(38,0) not null,
  NORUBR NUMBER(8,0) not null,
  LIBELLE NVARCHAR2(50),
  DESCRIPTION NVARCHAR2(300),
  TYPE_SAISIE NUMBER(1,0),
  IS_CALCULEE NUMBER(1,0) DEFAULT 0 NOT NULL,
  STATUT VARCHAR2(2) NOT NULL,
  VERSION NUMBER(38,0) DEFAULT 0 NOT NULL,
  constraint PK_PTG_REF_PRIME
  primary key (ID_REF_PRIME)
)
TABLESPACE TS_PTG_DATA;

create public synonym PTG_REF_PRIME for PTG_REF_PRIME;
grant select on PTG_REF_PRIME to R_PTG_USR;
grant select on PTG_REF_PRIME to R_PTG_READ;

----------------------------------------------------------------
-- connecte en PTG_COMMENT
----------------------------------------------------------------
create sequence PTG_S_COMMENT 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_COMMENT for PTG_S_COMMENT;
grant select on PTG_S_COMMENT to R_PTG_USR;

create table PTG_COMMENT
(
  ID_COMMENT NUMBER(38,0) not null,
  TEXT CLOB,
  VERSION NUMBER(38,0) DEFAULT 0 NOT NULL,
  constraint PK_PTG_COMMENT
  primary key (ID_COMMENT)
)
TABLESPACE TS_PTG_COMMENT;

create public synonym PTG_COMMENT for PTG_COMMENT;
grant select on PTG_COMMENT to R_PTG_USR;
grant select on PTG_COMMENT to R_PTG_READ;

--==============================================================
-- Table: PTG_REF_ETAT
--==============================================================
create table PTG_REF_ETAT
(
  ID_REF_ETAT NUMBER(38,0) not null,
  LABEL nvarchar2(25),
  constraint PK_PTG_REF_ETAT
  primary key (ID_REF_ETAT)
)
TABLESPACE TS_PTG_DATA;

create public synonym PTG_REF_ETAT for PTG_REF_ETAT;
grant select on PTG_REF_ETAT to R_PTG_USR;
grant select on PTG_REF_ETAT to R_PTG_READ;