----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

----------------------------------------------------------------
-- PTG_POINTAGE_VENTIL_DATE
----------------------------------------------------------------
create sequence PTG_S_VENTIL_TASK 
start with 1 
increment by 1 
nomaxvalue;

create public synonym PTG_S_VENTIL_TASK for PTG_S_VENTIL_TASK;
grant select on PTG_S_VENTIL_TASK to R_PTG_USR;

CREATE TABLE PTG_VENTIL_TASK (
  ID_VENTIL_TASK NUMBER(38,0) not null,
  ID_AGENT NUMBER(7,0) not null,
  ID_AGENT_CREATION NUMBER(7,0) not null,
  DATE_CREATION DATE not null,
  TYPE_CHAINE_PAIE varchar2(3) not null,
  ID_TYPE_POINTAGE NUMBER(38,0),
  ID_VENTIL_DATE_FROM NUMBER(38,0) not null,
  ID_VENTIL_DATE_TO NUMBER(38,0) not null,
  DATE_VENTILATION DATE,
  TASK_STATUS varchar2(255),
  VERSION NUMBER default 0 not null,
  constraint PK_PTG_VENTIL_TASK
   primary key (ID_VENTIL_TASK),
  foreign key (ID_TYPE_POINTAGE)
   references PTG_REF_TYPE_POINTAGE (ID_REF_TYPE_POINTAGE),
  foreign key (ID_VENTIL_DATE_FROM)
   references PTG_VENTIL_DATE (ID_VENTIL_DATE),
  foreign key (ID_VENTIL_DATE_TO)
   references PTG_VENTIL_DATE (ID_VENTIL_DATE)
  )
TABLESPACE TS_PTG_DATA;

create public synonym PTG_VENTIL_TASK for PTG_VENTIL_TASK;
grant select, insert, update, delete on PTG_VENTIL_TASK to R_PTG_USR;
grant select, update, delete on PTG_VENTIL_TASK to R_PTG_JOBS_USR;
grant select on PTG_VENTIL_TASK to R_PTG_READ;

