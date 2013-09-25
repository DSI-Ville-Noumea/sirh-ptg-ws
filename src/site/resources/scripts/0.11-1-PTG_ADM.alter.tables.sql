----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

ALTER TABLE PTG_POINTAGE_CALCULE ADD (ID_VENTIL_DATE NUMBER(38,0));
ALTER TABLE PTG_POINTAGE_CALCULE ADD CONSTRAINT FK_PTG_CALCULE_VENTIL_DATE 
  foreign key (ID_VENTIL_DATE)
  references PTG_VENTIL_DATE (ID_VENTIL_DATE);
  
ALTER TABLE PTG_REF_PRIME ADD (AIDE NVARCHAR2(300));
