----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

----------------------------------------------------------------
-- PTG_ETAT_PAYEUR
----------------------------------------------------------------

CREATE TABLE PTG_ETAT_PAYEUR
(
   ID_ETAT_PAYEUR NUMBER(38,0) not null,
   STATUT VARCHAR2(2) NOT NULL,
   ID_TYPE_POINTAGE NUMBER(38,0) not null,
   DATE_ETAT_PAYEUR DATE not null,
   LABEL VARCHAR2(100) not null, 
   FICHIER VARCHAR2(100) not null,
   ID_AGENT NUMBER(7,0) not null,
   DATE_EDITION TIMESTAMP not null,
   VERSION NUMBER default 0 not null,
   constraint PK_PTG_ETAT_PAYEUR
   primary key (ID_ETAT_PAYEUR),
   constraint FK2_PTG_TYPE_POINTAGE
   foreign key (ID_TYPE_POINTAGE)
   references PTG_REF_TYPE_POINTAGE (ID_REF_TYPE_POINTAGE)
)
TABLESPACE TS_PTG_DATA;

-- SEQUENCE
CREATE SEQUENCE PTG_S_ETAT_PAYEUR
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_ETAT_PAYEUR for PTG_S_ETAT_PAYEUR;
grant select on PTG_S_ETAT_PAYEUR to R_PTG_USR;

create public synonym PTG_ETAT_PAYEUR for PTG_ETAT_PAYEUR;
grant select, insert, update, delete on PTG_ETAT_PAYEUR to R_PTG_USR;
grant select on PTG_ETAT_PAYEUR to R_PTG_READ;

----------------------------------------------------------------
-- PTG_EXPORT_ETATS_PAYEUR_TASK
----------------------------------------------------------------
create sequence PTG_S_EXPORT_ETATS_PAYEUR_TASK
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_EXPORT_ETATS_PAYEUR_TASK for PTG_S_EXPORT_ETATS_PAYEUR_TASK;
grant select on PTG_S_EXPORT_ETATS_PAYEUR_TASK to R_PTG_USR;

CREATE TABLE PTG_EXPORT_ETATS_PAYEUR_TASK (
  ID_EXPORT_ETATS_PAYEUR_TASK NUMBER(38,0) not null,
  ID_AGENT NUMBER(7,0) not null,
  DATE_CREATION TIMESTAMP not null,
  DATE_EXPORT TIMESTAMP,
  TYPE_CHAINE_PAIE varchar2(3) not null,
  ID_VENTIL_DATE NUMBER(38,0) not null,
  TASK_STATUS varchar2(255),
  VERSION NUMBER default 0 not null,
  constraint PK_PTG_EXPORT_ETATS_TASK
   primary key (ID_EXPORT_ETATS_PAYEUR_TASK),
  foreign key (ID_VENTIL_DATE)
   references PTG_VENTIL_DATE (ID_VENTIL_DATE)
  )
TABLESPACE TS_PTG_DATA;

create public synonym PTG_EXPORT_ETATS_PAYEUR_TASK for PTG_EXPORT_ETATS_PAYEUR_TASK;
grant select, insert, update, delete on PTG_EXPORT_ETATS_PAYEUR_TASK to R_PTG_USR;
grant select, update, delete on PTG_EXPORT_ETATS_PAYEUR_TASK to R_PTG_JOBS_USR;
grant select on PTG_EXPORT_ETATS_PAYEUR_TASK to R_PTG_READ;
