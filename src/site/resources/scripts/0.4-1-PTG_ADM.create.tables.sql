----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

grant insert, update, delete on PTG_DROIT to R_PTG_USR;
grant insert, update, delete on PTG_DROITS_AGENT to R_PTG_USR;
grant insert, update, delete on PTG_REF_PRIME to R_PTG_USR;
grant insert, update, delete on PTG_COMMENT to R_PTG_USR;
grant insert, update, delete on PTG_REF_ETAT to R_PTG_USR;
grant insert, update, delete on PTG_COMMENT to R_PTG_USR;
grant insert, update, delete on PTG_COMMENT to R_PTG_USR;

----------------------------------------------------------------
-- PTG_DROIT_DROITS_AGENT
----------------------------------------------------------------

CREATE TABLE PTG_DROIT_DROITS_AGENT (
  ID_DROIT NUMBER(38,0) not null,
  ID_DROITS_AGENT NUMBER(38,0) not null,
  constraint PK_DROIT_DROITS_AGENT
   primary key (ID_DROIT, ID_DROITS_AGENT),
  foreign key (ID_DROIT)
   references PTG_DROIT (ID_DROIT) ,
    foreign key (ID_DROITS_AGENT)
   references PTG_DROITS_AGENT (ID_DROITS_AGENT)
  )
TABLESPACE TS_PTG_DATA;

create public synonym PTG_DROIT_DROITS_AGENT for PTG_DROIT_DROITS_AGENT;
grant select, insert, update, delete on PTG_DROIT_DROITS_AGENT to R_PTG_USR;
grant select on PTG_DROIT_DROITS_AGENT to R_PTG_READ;
