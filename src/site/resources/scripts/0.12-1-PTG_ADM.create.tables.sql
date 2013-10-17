----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

CREATE TABLE PTG_ETAT_PAYEUR
(
   ID_ETAT_PAYEUR NUMBER(38,0) not null,
   STATUT VARCHAR2(2) NOT NULL,
   ID_TYPE_POINTAGE NUMBER(38,0) not null,
   DATE_ETAT_PAYEUR DATE not null,
   LABEL VARCHAR2(100) not null, 
   FICHIER VARCHAR2(100) not null,
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

COMMIT;