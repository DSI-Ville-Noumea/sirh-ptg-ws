----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

ALTER TABLE PTG_ADM.PTG_ETAT_POINTAGE ADD (ID_AGENT NUMBER(38,0));
ALTER TABLE PTG_POINTAGE RENAME COLUMN IS_HSUP_PAYEE TO IS_HSUP_RECUPEREE;
