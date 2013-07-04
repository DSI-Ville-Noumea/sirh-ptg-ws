----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

-- converting DATE to TIMESTAMP in PTG_ETAT_POINTAGE (for sql query precision purposes)
alter table PTG_ETAT_POINTAGE modify (DATE_ETAT TIMESTAMP(0));
