----------------------------------------------------------------
-- connecte en PTG_ADM
----------------------------------------------------------------

update PTG_REF_PRIME set AIDE = 'Cocher la case chaque jour où l''agent a droit à cette prime' where NORUBR = 7120;
update PTG_REF_PRIME set AIDE = 'Cocher la case chaque jour où l''agent a droit à cette prime' where NORUBR = 7121;
update PTG_REF_PRIME set AIDE = 'Cocher la case chaque jour où l''agent a droit à cette prime' where NORUBR = 7123;
update PTG_REF_PRIME set AIDE = 'Cocher la case à partir de 2heures minimums et consécutives travaillées entre 21h et 4h. Merci de renseigner les horaires dans le motif sous peine d''annulation par la DRH.' where NORUBR = 7650;
update PTG_REF_PRIME set AIDE = 'Cocher la case à partir de 2heures minimums et consécutives travaillées entre 21h et 4h, un dimanche ou un jour férié.  Merci de renseigner les horaires dans le motif sous peine d''annulation par la DRH.' where NORUBR = 7651;
update PTG_REF_PRIME set AIDE = 'Cocher la case à partir de 2heures minimums et consécutives travaillées un dimanche ou un jour férié.  Merci de renseigner les horaires dans le motif sous peine d''annulation par la DRH.' where NORUBR = 7652;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''indemnités par jour (maximum 2)' where NORUBR = 7704;
update PTG_REF_PRIME set AIDE = 'Cocher la case chaque jour où l''agent a droit à cette prime' where NORUBR = 7705;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''heures d''astreinte effectuées par jour' where NORUBR = 7708;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''indemnités par jour' where NORUBR = 7709;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''indemnités par jour' where NORUBR = 7710;
update PTG_REF_PRIME set AIDE = 'Saisir l''heure de début et l''heure de fin du roulement' where NORUBR = 7715;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''heures effectuées par jour' where NORUBR = 7720;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''heures effectuées par jour' where NORUBR = 7721;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''heures effectuées par jour' where NORUBR = 7722;
update PTG_REF_PRIME set AIDE = 'Saisir le nombre d''heures effectuées par jour' where NORUBR = 7731;
update PTG_REF_PRIME set AIDE = 'Cocher la case chaque jour où l''agent a droit à cette prime' where NORUBR = 7750;
commit;

