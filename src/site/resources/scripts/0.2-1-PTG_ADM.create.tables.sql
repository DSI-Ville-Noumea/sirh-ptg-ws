----------------------------------------------------------------
-- connecte en PTG_ADM
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
