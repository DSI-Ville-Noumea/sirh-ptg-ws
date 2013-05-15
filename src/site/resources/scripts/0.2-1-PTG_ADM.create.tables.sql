----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

create table PTG_REF_PRIME
(
  NORUBR NUMBER(38,0) not null,
  LIBELLE VARCHAR2(25),
  DESCRIPTION VARCHAR2(255),
  TYPE_SAISIE NUMBER(2,0),
  IS_CALCULEE NUMBER(1,0) DEFAULT 0 NOT NULL,
  VERSION NUMBER(38,0) DEFAULT 0 NOT NULL,
  constraint PK_PTG_REF_PRIME
  primary key (NORUBR)
)
TABLESPACE TS_PTG_DATA;

create public synonym PTG_REF_PRIME for PTG_REF_PRIME;
grant select on PTG_REF_PRIME to R_PTG_USR;
grant select on PTG_REF_PRIME to R_PTG_READ;
