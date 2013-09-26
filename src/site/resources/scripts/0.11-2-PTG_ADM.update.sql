----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

UPDATE PTG_REF_PRIME set TYPE_SAISIE = 1 WHERE NORUBR IN (7701);
UPDATE PTG_REF_PRIME set TYPE_SAISIE = 1 WHERE NORUBR IN (7711, 7712);
UPDATE PTG_REF_PRIME set TYPE_SAISIE = 2 WHERE NORUBR IN (7713);
UPDATE PTG_REF_PRIME set TYPE_SAISIE = 2 WHERE NORUBR IN (7720, 7721, 7722);

UPDATE PTG_REF_PRIME SET IS_CALCULEE = 0, TYPE_SAISIE = 0 WHERE NORUBR IN (7650, 7651, 7652);