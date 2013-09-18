----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

RENAME PTG_ETAT_POINTAGE TO TMP_PTG_ETAT_POINTAGE;

CREATE TABLE PTG_ETAT_POINTAGE
(
   ID_ETAT_POINTAGE NUMBER(38,0) not null,
   ID_POINTAGE NUMBER(38,0) not null,
   DATE_ETAT TIMESTAMP(0) not null,
   DATE_MAJ TIMESTAMP(0) not null,
   ETAT NUMBER(2,0) not null, 
   ID_AGENT NUMBER(38,0) not null,
   VERSION NUMBER default 0 not null,
   constraint PK2_PTG_ETAT_POINTAGE
   primary key (ID_ETAT_POINTAGE),
   constraint FK2_PTG_POINTAGE_ETAT_ID
   foreign key (ID_POINTAGE)
   references PTG_POINTAGE (ID_POINTAGE)
)
TABLESPACE TS_PTG_DATA;

-- SEQUENCE
CREATE SEQUENCE PTG_S_ETAT_POINTAGE
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_ETAT_POINTAGE for PTG_S_ETAT_POINTAGE;
grant select on PTG_S_ETAT_POINTAGE to R_PTG_USR;

-- COPIE DES DONNEES
CREATE TABLE TMP_PTG_ETAT AS SELECT
   ID_POINTAGE,
   DATE_ETAT,
   DATE_ETAT as DATE_MAJ,
   ETAT,
   ID_AGENT,
   VERSION
FROM TMP_PTG_ETAT_POINTAGE
ORDER BY DATE_ETAT ASC;

INSERT INTO PTG_ETAT_POINTAGE
SELECT
   PTG_S_ETAT_POINTAGE.NEXTVAL,
   ID_POINTAGE,
   DATE_ETAT,
   DATE_MAJ,
   ETAT,
   ID_AGENT,
   VERSION
FROM TMP_PTG_ETAT;

COMMIT;

-- DROP ANCIENNE TABLE
DROP TABLE TMP_PTG_ETAT_POINTAGE;
DROP TABLE TMP_PTG_ETAT;
