----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

alter table PTG_ADM.PTG_DROIT drop column CODE_SERVICE;
alter table PTG_ADM.PTG_DROITS_AGENT drop column ID_DROIT;
ALTER TABLE PTG_ADM.PTG_ETAT_POINTAGE ADD (ID_AGENT NUMBER(38,0));
