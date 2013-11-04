----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

-- MAJ du libellé de la prime 7121
UPDATE PTG_REF_PRIME set LIBELLE = 'INDEMNITE D''ASTREINTE CFM' WHERE NORUBR IN (7121);


commit;
