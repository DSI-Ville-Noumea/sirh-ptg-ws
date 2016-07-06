----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

-- MAJ des types de saisies (et donc leur mode de déversement) pour les primes calculées à déverser dans la Paie
UPDATE PTG_REF_PRIME set TYPE_SAISIE = 1 WHERE NORUBR IN (7711, 7712);
UPDATE PTG_REF_PRIME set TYPE_SAISIE = 2 WHERE NORUBR IN (7713);

-- MAJ du mode de saisie des primes
UPDATE PTG_REF_PRIME set IS_CALCULEE = 0, TYPE_SAISIE = 1 WHERE NORUBR IN (7720, 7721, 7722);
UPDATE PTG_REF_PRIME SET IS_CALCULEE = 0, TYPE_SAISIE = 0 WHERE NORUBR IN (7650, 7651, 7652);

commit;

-- Effacement de la prime 7701 de la base (pointages, ventilations passées, etc...)
DELETE FROM PTG_VENTIL_PRIME WHERE ID_REF_PRIME in (SELECT ID_REF_PRIME FROM PTG_REF_PRIME WHERE NORUBR = 7701);
DELETE FROM PTG_POINTAGE_VENTIL_DATE WHERE ID_POINTAGE in (SELECT ID_POINTAGE FROM PTG_POINTAGE WHERE ID_REF_PRIME in (SELECT ID_REF_PRIME FROM PTG_REF_PRIME WHERE NORUBR = 7701));
DELETE FROM PTG_ETAT_POINTAGE WHERE ID_POINTAGE in (SELECT ID_POINTAGE FROM PTG_POINTAGE WHERE ID_REF_PRIME in (SELECT ID_REF_PRIME FROM PTG_REF_PRIME WHERE NORUBR = 7701));
DELETE FROM PTG_POINTAGE WHERE ID_REF_PRIME in (SELECT ID_REF_PRIME FROM PTG_REF_PRIME WHERE NORUBR = 7701);

DELETE FROM PTG_REF_PRIME WHERE NORUBR IN (7701);

commit;

UPDATE PTG_REF_PRIME SET AIDE = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse rutrum augue nec congue imperdiet. In orci augue, elementum quis tristique at, egestas volutpat massa. Phasellus lacinia condimentum fringilla. Quisque porttitor congue erat ultricies iaculis. Donec dapibus ultrices laoreet nullam.' WHERE IS_CALCULEE = 0;

commit;
