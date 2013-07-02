----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

----------------------------------------------------------------
-- PTG_VENTIL_HSUP
----------------------------------------------------------------
create sequence PTG_S_VENTIL_HSUP 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_VENTIL_HSUP for PTG_S_VENTIL_HSUP;
grant select on PTG_S_VENTIL_HSUP to R_PTG_USR;

CREATE TABLE PTG_VENTIL_HSUP (
  ID_VENTIL_HSUP NUMBER(38,0) NOT NULL,
  ID_AGENT NUMBER(8,0) NOT NULL,
  DATE_LUNDI DATE NOT NULL, 
  M_ABS NUMBER(5,0) DEFAULT 0,
  M_HORS_CONTRAT NUMBER(5,0) DEFAULT 0,
  M_SUP NUMBER(5,0) DEFAULT 0,
  M_SUP_25 NUMBER(5,0) DEFAULT 0,
  M_SUP_50 NUMBER(5,0) DEFAULT 0,
  M_DJF NUMBER(5,0) DEFAULT 0,
  M_DJF_25 NUMBER(5,0) DEFAULT 0,
  M_DJF_50 NUMBER(5,0) DEFAULT 0,
  M_1_MAI NUMBER(5,0) DEFAULT 0,
  M_NUIT NUMBER(5,0) DEFAULT 0,
  M_NORMALES NUMBER(5,0) DEFAULT 0,
  M_COMPLEMENTAIRES NUMBER(5,0) DEFAULT 0,
  M_SIMPLES NUMBER(5,0) DEFAULT 0,
  M_COMPOSEES NUMBER(5,0) DEFAULT 0,
  ETAT NUMBER(2,0), 
  VERSION NUMBER(38,0) DEFAULT 0,
  constraint PK_PTG_VENTIL_HSUP
   primary key (ID_VENTIL_HSUP)
  )
TABLESPACE TS_PTG_BIG_DATA;

create public synonym PTG_VENTIL_HSUP for PTG_VENTIL_HSUP;
grant select, insert, update, delete on PTG_VENTIL_HSUP to R_PTG_USR;
grant select on PTG_VENTIL_HSUP to R_PTG_READ;


----------------------------------------------------------------
-- PTG_VENTIL_PRIME
----------------------------------------------------------------
create sequence PTG_S_VENTIL_PRIME 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_VENTIL_PRIME for PTG_S_VENTIL_PRIME;
grant select on PTG_S_VENTIL_PRIME to R_PTG_USR;

CREATE TABLE PTG_VENTIL_PRIME (
  ID_VENTIL_PRIME NUMBER(38,0) NOT NULL,
  ID_AGENT NUMBER(8,0) NOT NULL,
  DATE_DEBUT_MOIS DATE NOT NULL, 
  ID_REF_PRIME NUMBER(38,0) NOT NULL,
  ETAT NUMBER(2,0), 
  QUANTITE NUMBER(7,2) DEFAULT 0,
  VERSION NUMBER(38,0) DEFAULT 0,
  constraint PK_PTG_VENTIL_PRIME
   primary key (ID_VENTIL_PRIME),
  constraint FK_PTG_VENTIL_PRIME_REF_PRIME
   foreign key (ID_REF_PRIME)
   references PTG_REF_PRIME (ID_REF_PRIME)
  )
TABLESPACE TS_PTG_BIG_DATA;

create public synonym PTG_VENTIL_PRIME for PTG_VENTIL_PRIME;
grant select, insert, update, delete on PTG_VENTIL_PRIME to R_PTG_USR;
grant select on PTG_VENTIL_PRIME to R_PTG_READ;

----------------------------------------------------------------
-- PTG_VENTIL_ABSENCE
----------------------------------------------------------------
create sequence PTG_S_VENTIL_ABSENCE 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_VENTIL_ABSENCE for PTG_S_VENTIL_ABSENCE;
grant select on PTG_S_VENTIL_ABSENCE to R_PTG_USR;

CREATE TABLE PTG_VENTIL_ABSENCE (
  ID_VENTIL_ABSENCE NUMBER(38,0) NOT NULL,
  ID_AGENT NUMBER(8,0) NOT NULL,
  DATE_DEBUT_MOIS DATE NOT NULL,
  ETAT NUMBER(2,0), 
  MINUTES_CONCERTEE NUMBER(5,0) DEFAULT 0,
  MINUTES_NON_CONCERTEE NUMBER(5,0) DEFAULT 0,
  VERSION NUMBER(38,0) DEFAULT 0,
  constraint PK_PTG_VENTIL_ABSENCE
   primary key (ID_VENTIL_ABSENCE)
  )
TABLESPACE TS_PTG_BIG_DATA;

create public synonym PTG_VENTIL_ABSENCE for PTG_VENTIL_ABSENCE;
grant select, insert, update, delete on PTG_VENTIL_ABSENCE to R_PTG_USR;
grant select on PTG_VENTIL_ABSENCE to R_PTG_READ;

--==============================================================
-- Table: PTG_POINTAGE_CALCULE
--==============================================================
create sequence PTG_S_POINTAGE_CALCULE
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_POINTAGE_CALCULE for PTG_S_POINTAGE_CALCULE;
grant select on PTG_S_POINTAGE_CALCULE to R_PTG_USR;

create table PTG_POINTAGE_CALCULE
(
    ID_POINTAGE_CALCULE NUMBER(38,0) not null,
    ID_AGENT NUMBER(7,0) not null,
    DATE_LUNDI DATE not null,
    DATE_DEBUT DATE not null,
    DATE_FIN DATE,
    QUANTITE INTEGER,
	ID_REF_TYPE_POINTAGE NUMBER(38,0) not null,
	ID_REF_PRIME NUMBER(38,0),
	ETAT NUMBER(2,0), 
    VERSION NUMBER default 0 not null,
    constraint PK_PTG_POINTAGE_CALCULE
    primary key (ID_POINTAGE_CALCULE),
    constraint FK_PTG_CALCULE_TYPE_POINTAGE
    foreign key (ID_REF_TYPE_POINTAGE)
    references PTG_REF_TYPE_POINTAGE (ID_REF_TYPE_POINTAGE),
	constraint FK_PTG_CALCULE_REF_PRIME
    foreign key (ID_REF_PRIME)
    references PTG_REF_PRIME (ID_REF_PRIME)
)
TABLESPACE TS_PTG_BIG_DATA;

create public synonym PTG_POINTAGE_CALCULE for PTG_POINTAGE_CALCULE;
grant select, insert, update, delete on PTG_POINTAGE_CALCULE to R_PTG_USR;
grant select on PTG_POINTAGE_CALCULE to R_PTG_READ;


----------------------------------------------------------------
-- PTG_VENTIL_DATE
----------------------------------------------------------------
create sequence PTG_S_VENTIL_DATE 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_VENTIL_DATE for PTG_S_VENTIL_DATE;
grant select on PTG_S_VENTIL_DATE to R_PTG_USR;

CREATE TABLE PTG_VENTIL_DATE (
  ID_VENTIL_DATE NUMBER(38,0) NOT NULL,
  DATE_VENTIL DATE NOT NULL,
  IS_PAYE NUMBER(1,0) DEFAULT 0 NOT NULL,
  TYPE_CHAINE_PAIE VARCHAR2(3) NOT NULL,
  VERSION NUMBER(38,0) DEFAULT 0 NOT NULL,
  constraint PK_PTG_VENTIL_DATE
   primary key (ID_VENTIL_DATE)
  )
TABLESPACE TS_PTG_DATA;

create public synonym PTG_VENTIL_DATE for PTG_VENTIL_DATE;
grant select, insert, update, delete on PTG_VENTIL_DATE to R_PTG_USR;
grant select on PTG_VENTIL_DATE to R_PTG_READ;
