----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

ALTER TABLE PTG_ADM.PTG_DROITS_AGENT DROP COLUMN ID_AGENT_APPROBATEUR;
ALTER TABLE PTG_ADM.PTG_DROITS_AGENT DROP COLUMN ID_DROITS_PROFIL;
ALTER TABLE PTG_DROITS_AGENT ADD (ID_DELEGATAIRE NUMBER(38,0));
ALTER TABLE PTG_ADM.PTG_DROITS_AGENT ADD (IS_APPROBATEUR NUMBER(1,0) default 0 not null);
ALTER TABLE PTG_ADM.PTG_DROITS_AGENT ADD (IS_OPERATEUR NUMBER(1,0) default 0 not null);
DROP TABLE PTG_ADM.PTG_DROITS_PROFIL;

ALTER TABLE PTG_ADM.PTG_ETAT_POINTAGE MODIFY ETAT NUMBER(2,0) default 1;

ALTER TABLE PTG_ADM.PTG_POINTAGE ADD ID_REF_PRIME NUMBER(38,0);
ALTER TABLE PTG_ADM.PTG_POINTAGE ADD CONSTRAINT FK_PTG_REF_PRIME
   foreign key (ID_REF_PRIME)
   references PTG_REF_PRIME (ID_REF_PRIME);