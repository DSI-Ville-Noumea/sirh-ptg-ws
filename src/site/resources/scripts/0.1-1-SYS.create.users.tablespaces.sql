----------------------------------------------------------------
-- connecte en SYS
----------------------------------------------------------------
-- creation des roles et users

create role R_PTG_ADM;
create role R_PTG_USR;
create role R_PTG_READ;

grant connect, create session, create table, create sequence, create public synonym to R_PTG_ADM;
grant unlimited tablespace to R_PTG_ADM;
grant connect, create session to R_PTG_USR;
grant connect, create session to R_PTG_READ;

create user PTG_ADM identified by PASSWORD_SECRET_SIE;
create user PTG_USR identified by PASSWORD_SECRET_SIE_2;
create user PTG_READ identified by PASSWORD_DONNER_AU_SED;

grant R_PTG_ADM to PTG_ADM;
grant R_PTG_USR to PTG_USR;
grant R_PTG_READ to PTG_READ;


----------------------------------------------------------------
-- Creation des tablespaces : finaliser les nomns de fichiers par le SIE

-- petit, prevoir des extends de 20 Mo, initial 20 Mo
CREATE TABLESPACE TS_PTG_PARAM DATAFILE
'E:\oradata\ORADEV\dbfusers\ORADEV_ts_dev.dbf'
SIZE 20M AUTOEXTEND ON NEXT 20M MAXSIZE 100M
LOGGING
ONLINE
PERMANENT
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 512K
BLOCKSIZE 8K
SEGMENT SPACE MANAGEMENT AUTO
FLASHBACK OFF;


-- prevoir des extends de 100 Mo, initial 50 Mo
CREATE TABLESPACE TS_PTG_DATA DATAFILE
'E:\oradata\ORADEV\dbfusers\ORADEV_ts_dev.dbf'
SIZE 50M AUTOEXTEND ON NEXT 100M MAXSIZE 2000M
LOGGING
ONLINE
PERMANENT
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 512K
BLOCKSIZE 8K
SEGMENT SPACE MANAGEMENT AUTO
FLASHBACK OFF;

-- tablespace à part pour la table des pointages qui va beaucoup grossir indépendamment des autres
-- prevoir des extends de 100 Mo, initial 50 Mo
CREATE TABLESPACE TS_PTG_BIG_DATA DATAFILE
'E:\oradata\ORADEV\dbfusers\ORADEV_ts_dev.dbf'
SIZE 50M AUTOEXTEND ON NEXT 100M MAXSIZE 2000M
LOGGING
ONLINE
PERMANENT
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 512K
BLOCKSIZE 8K
SEGMENT SPACE MANAGEMENT AUTO
FLASHBACK OFF;

-- moyen, prevoir des extends de 100 Mo, initial 20 Mo
CREATE TABLESPACE TS_PTG_INDEX DATAFILE
'E:\oradata\ORADEV\dbfusers\ORADEV_ts_dev.dbf'
SIZE 20M AUTOEXTEND ON NEXT 100M MAXSIZE 2000M
LOGGING
ONLINE
PERMANENT
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 512K
BLOCKSIZE 8K
SEGMENT SPACE MANAGEMENT AUTO
FLASHBACK OFF;


-- le plus petit possible, pas d'extend, bloque
CREATE TABLESPACE TS_PTG_DEFAULT DATAFILE
'E:\oradata\ORADEV\dbfusers\ORADEV_ts_dev.dbf'
SIZE 10M AUTOEXTEND OFF MAXSIZE 2000M
LOGGING
ONLINE
PERMANENT
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 512K
BLOCKSIZE 8K
SEGMENT SPACE MANAGEMENT AUTO
FLASHBACK OFF;

alter tablespace TS_PTG_DEFAULT read only;


-- on redirige par defaut sur le tablespace USERS pour flagger les mises en recette sauvages...
alter user PTG_ADM default tablespace TS_PTG_DEFAULT;
