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
  H_ABS NUMBER(7,5) DEFAULT 0,
  H_HORS_CONTRAT NUMBER(7,5) DEFAULT 0,
  H_SUP NUMBER(7,5) DEFAULT 0,
  H_SUP_25 NUMBER(7,5) DEFAULT 0,
  H_SUP_50 NUMBER(7,5) DEFAULT 0,
  H_DJF NUMBER(7,5) DEFAULT 0,
  H_DJF_25 NUMBER(7,5) DEFAULT 0,
  H_DJF_50 NUMBER(7,5) DEFAULT 0,
  H_1_MAI NUMBER(7,5) DEFAULT 0,
  H_NUIT NUMBER(7,5) DEFAULT 0,
  H_NORMALES NUMBER(7,5) DEFAULT 0,
  H_COMPLEMENTAIRES NUMBER(7,5) DEFAULT 0,
  H_SIMPLES NUMBER(7,5) DEFAULT 0,
  H_COMPOSEES NUMBER(7,5) DEFAULT 0,
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
