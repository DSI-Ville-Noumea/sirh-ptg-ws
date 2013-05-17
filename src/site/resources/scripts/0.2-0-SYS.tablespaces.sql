----------------------------------------------------------------
-- connecte en SYS
----------------------------------------------------------------

----------------------------------------------------------------
-- Creation des tablespaces
-- /!\ LES NOMS DE FICHIERS SONT A DEFINIR PAR LE SIE /!\
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
-- ATTENTION, BIEN RENOMMER LE NOM DU DATAFILE AFIN
--  QU'IL SOIT COHERENT AVEC LA BASE (eg :
-- RECETTE : ORADEV_TS_DEV.dbf -> SIRHR_TS_PARAM.dbf
-- PROD : ORADEV_TS_DEV.dbf -> SIRHP_TS_PARAM.dbf
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
----------------------------------------------------------------

-- tablespace à part pour la table des commentaires qui va beaucoup grossir indépendamment des autres
-- prevoir des extends de 100 Mo, initial 50 Mo
CREATE TABLESPACE TS_PTG_COMMENT DATAFILE
'E:\oradata\ORADEV\dbfusers\ORADEV_ts_dev.dbf'
SIZE 50M AUTOEXTEND ON NEXT 100M MAXSIZE 2000M
LOGGING
ONLINE
PERMANENT
EXTENT MANAGEMENT LOCAL UNIFORM SIZE 512K
BLOCKSIZE 8K
SEGMENT SPACE MANAGEMENT AUTO
FLASHBACK OFF;
