----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

--==============================================================
-- Table: PTG_TYPE_POINTAGE
--==============================================================
create table PTG_TYPE_POINTAGE
(
  ID_TYPE_POINTAGE NUMBER(38,0) not null,
  LABEL nvarchar2(25),
  constraint PK_PTG_TYPE_POINTAGE
  primary key (ID_TYPE_POINTAGE)
)
TABLESPACE TS_PTG_DATA;

create public synonym PTG_TYPE_POINTAGE for PTG_TYPE_POINTAGE;
grant select, insert, update, delete on PTG_TYPE_POINTAGE to R_PTG_USR;
grant select on PTG_TYPE_POINTAGE to R_PTG_READ;


--==============================================================
-- Table: PTG_POINTAGE
--==============================================================
create sequence PTG_S_POINTAGE 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_POINTAGE for PTG_S_POINTAGE;
grant select on PTG_S_POINTAGE to R_PTG_USR;

create table PTG_POINTAGE
(
    ID_POINTAGE NUMBER(38,0) not null,
    ID_AGENT NUMBER(7,0) not null,
    ID_TYPE_POINTAGE NUMBER(38,0) not null,
    DATE_LUNDI DATE not null,
    DATE_DEBUT DATE not null,
    DATE_FIN DATE,
    QUANTITE INTEGER,
    ID_POINTAGE_PARENT NUMBER(38,0),
    VERSION NUMBER default 0 not null,
    constraint PK_PTG_POINTAGE
    primary key (ID_POINTAGE),
    constraint FK_PTG_POINTAGE_PARENT
    foreign key (ID_POINTAGE_PARENT)
    references PTG_POINTAGE (ID_POINTAGE),
    constraint FK_PTG_TYPE_POINTAGE
    foreign key (ID_TYPE_POINTAGE)
    references PTG_TYPE_POINTAGE (ID_TYPE_POINTAGE)
)
TABLESPACE TS_PTG_BIG_DATA;

create public synonym PTG_POINTAGE for PTG_POINTAGE;
grant select, insert, update, delete on PTG_POINTAGE to R_PTG_USR;
grant select on PTG_POINTAGE to R_PTG_READ;

--==============================================================
-- Table: PTG_ETAT_POINTAGE
--==============================================================
create table PTG_ETAT_POINTAGE
(
   ID_POINTAGE  NUMBER(38,0) not null,
   DATE_ETAT DATE not null,
   ETAT char(1) not null, 
   VERSION NUMBER default 0 not null,
   constraint PK_PTG_ETAT_POINTAGE
   primary key (ID_POINTAGE, DATE_ETAT),
   constraint FK_PTG_POINTAGE_ETAT_ID
   foreign key (ID_POINTAGE)
   references PTG_POINTAGE (ID_POINTAGE)
)
TABLESPACE TS_PTG_DATA;

create public synonym PTG_ETAT_POINTAGE for PTG_ETAT_POINTAGE;
grant select, insert, update, delete on PTG_ETAT_POINTAGE to R_PTG_USR;
grant select on PTG_ETAT_POINTAGE to R_PTG_READ;
