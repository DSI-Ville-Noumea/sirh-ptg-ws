----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

----------------------------------------------------------------
-- PTG_POINTAGE_VENTIL_DATE
----------------------------------------------------------------

CREATE TABLE PTG_POINTAGE_VENTIL_DATE (
  ID_POINTAGE NUMBER(38,0) not null,
  ID_VENTIL_DATE NUMBER(38,0) not null,
  constraint PK_POINTAGE_VENTIL_DATE
   primary key (ID_POINTAGE, ID_VENTIL_DATE),
  foreign key (ID_POINTAGE)
   references PTG_POINTAGE (ID_POINTAGE) ,
    foreign key (ID_VENTIL_DATE)
   references PTG_VENTIL_DATE (ID_VENTIL_DATE)
  )
TABLESPACE TS_PTG_DATA;

create public synonym PTG_POINTAGE_VENTIL_DATE for PTG_POINTAGE_VENTIL_DATE;
grant select, insert, update, delete on PTG_POINTAGE_VENTIL_DATE to R_PTG_USR;
grant select on PTG_POINTAGE_VENTIL_DATE to R_PTG_READ;

