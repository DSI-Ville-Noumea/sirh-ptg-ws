----------------------------------------------------------------
-- connecte en SYS
----------------------------------------------------------------
-- creation de role et user pour SIRH-JOBS

create role R_PTG_JOBS_USR;
grant connect, create session to R_PTG_JOBS_USR;

-- remplacer avec un vrai mot de passe
create user PTG_JOBS_USR identified by PASSWORD_SECRET_SIE;

grant R_PTG_JOBS_USR to PTG_JOBS_USR;

alter user PTG_JOBS_USR default tablespace TS_PTG_DEFAULT;
