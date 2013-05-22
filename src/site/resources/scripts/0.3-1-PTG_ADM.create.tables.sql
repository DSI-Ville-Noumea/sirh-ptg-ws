----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

----------------------------------------------------------------
-- PTG_DROIT
----------------------------------------------------------------
create sequence PTG_S_DROIT 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_DROIT for PTG_S_DROIT;
grant select on PTG_S_DROIT to R_PTG_USR;

create table PTG_DROIT
(
  ID_DROIT NUMBER(38,0) not null,
  ID_AGENT NUMBER(7,0) NOT NULL, 
  CODE_SERVICE VARCHAR2(10 BYTE), 
  DATE_MODIFICATION DATE NOT NULL, 
  IS_APPROBATEUR NUMBER(1,0) DEFAULT 0 NOT NULL, 
  IS_OPERATEUR NUMBER(1,0) DEFAULT 0 NOT NULL, 
  ID_AGENT_DELEGATAIRE NUMBER(7,0), 
  ID_DROIT_APPROBATEUR NUMBER(38,0), 
  VERSION NUMBER(38,0) DEFAULT 0 NOT NULL,
  constraint PK_PTG_DROIT
   primary key (ID_DROIT),
  foreign key (ID_DROIT_APPROBATEUR)
   references PTG_DROIT (ID_DROIT)  
)
TABLESPACE TS_PTG_DATA;

create public synonym PTG_DROIT for PTG_DROIT;
grant select on PTG_DROIT to R_PTG_USR;
grant select on PTG_DROIT to R_PTG_READ;

----------------------------------------------------------------
-- PTG_DROITS_AGENT;
----------------------------------------------------------------
-- drop existing table to recreate it
drop table PTG_DROITS_AGENT;

-- create new version
create table PTG_DROITS_AGENT
(
  ID_DROITS_AGENT NUMBER(38,0) not null,
  ID_DROIT NUMBER(38,0) NOT NULL, 
  ID_AGENT NUMBER(7,0), 
  CODE_SERVICE VARCHAR2(10 BYTE), 
  LIBELLE_SERVICE VARCHAR2(10 BYTE), 
  DATE_MODIFICATION DATE NOT NULL, 
  VERSION NUMBER(38,0) DEFAULT 0 NOT NULL,
  constraint PK_PTG_DROITS_AGENT
   primary key (ID_DROITS_AGENT),
  foreign key (ID_DROIT)
   references PTG_DROIT (ID_DROIT)
)
TABLESPACE TS_PTG_DATA;

grant select on PTG_DROITS_AGENT to R_PTG_USR;
grant select on PTG_DROITS_AGENT to R_PTG_READ;

