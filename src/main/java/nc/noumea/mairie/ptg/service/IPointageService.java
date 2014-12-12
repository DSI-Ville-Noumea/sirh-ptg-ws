package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.MotifHeureSupDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IPointageService {

	FichePointageDto getFilledFichePointageForAgent(int idAgent, Date dateLundi);

	List<RefEtatDto> getRefEtats();

	List<RefTypePointageDto> getRefTypesPointage();

	/**
	 * Based on the user input, this code retrieves a Pointage if existing,
	 * creates a new one if not. It does NOT set its pointage characteristics
	 * (dateDebut, dateFin, quantite and booleans)
	 * 
	 * @param idAgentCreator
	 * @param idPointage
	 * @param idAgent
	 * @param dateLundi
	 * @param dateEtat
	 * @return
	 */
	Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi,
			Date dateEtat);

	/**
	 * Based on the user input, this code retrieves a Pointage if existing,
	 * creates a new one if not. It does NOT set its pointage characteristics
	 * (dateDebut, dateFin, quantite and booleans)
	 * 
	 * @param idAgentCreator
	 * @param idPointage
	 * @param idAgent
	 * @param dateLundi
	 * @param dateEtat
	 * @param idRefPrime
	 * @return
	 */
	Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi,
			Date dateEtat, Integer idRefPrime);

	/**
	 * Returns a list of FichePointageDto initialized with Agents assigned
	 * PRIMES at a given date
	 * 
	 * @param csvIdAgents
	 * @param date
	 * @return
	 */
	FichePointageListDto getFichesPointageForUsers(String csvIdAgents, Date date);

	/**
	 * This method searches through the list of Pointages given a set of filters
	 * (agentId, and dateLundi). In the case a Pointage has multiple records (to
	 * keep history on its different statuses) Only the latest version will be
	 * returned. This means that if a Pointage's old version satisfies all the
	 * filters but its new version doesn't this Pointage will not be returned at
	 * all.
	 * 
	 * @param idAgent
	 * @param dateMonday
	 * @return
	 */
	List<Pointage> getLatestPointagesForSaisieForAgentAndDateMonday(Integer idAgent, Date dateMonday);

	/**
	 * This method searches through the list of Pointages given a set of filters
	 * (agentId, from and to dates for dateDebut of a Pointage and type (H_SUP,
	 * PRIME, ABSENCE). In the case a Pointage has multiple records (to keep
	 * history on its different statuses) Only the latest version will be
	 * returned. Then we filter on the Etat (status) of the Pointage. This means
	 * that if a Pointage's old version satisfies all the filters but its new
	 * version doesn't this Pointage will not be returned at all.
	 * 
	 * @param idAgent
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @param etats
	 * @return
	 */
	List<Pointage> getLatestPointagesForAgentAndDates(Integer idAgent, Date fromDate, Date toDate,
			RefTypePointageEnum type, List<EtatPointageEnum> etats);

	/**
	 * This method searches through the list of Pointages given a set of filters
	 * (idAgents, from and to dates for dateDebut of a Pointage and type (H_SUP,
	 * PRIME, ABSENCE). In the case a Pointage has multiple records (to keep
	 * history on its different statuses) Only the latest version will be
	 * returned. Then we filter on the Etat (status) of the Pointage. This means
	 * that if a Pointage's old version satisfies all the filters but its new
	 * version doesn't this Pointage will not be returned at all.
	 * 
	 * @param idAgents
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @param etats
	 * @param typeHS
	 * @return
	 */
	List<Pointage> getLatestPointagesForAgentsAndDates(List<Integer> idAgents, Date fromDate, Date toDate,
			RefTypePointageEnum type, List<EtatPointageEnum> etats, String typeHS);

	/**
	 * Retrieves a list of Pointages that have been ventilated for a given agent
	 * 
	 * @param idAgent
	 * @param ventilDate
	 * @return
	 */
	List<Pointage> getPointagesVentilesForAgent(Integer idAgent, VentilDate ventilDate);

	/**
	 * Retrieves a list of Pointages Calcules that have been ventilated for a
	 * given agent
	 * 
	 * @param idAgent
	 * @param ventilDate
	 * @return
	 */
	List<PointageCalcule> getPointagesCalculesVentilesForAgent(Integer idAgent, VentilDate ventilDate);

	/**
	 * 
	 * @param idAgent
	 * @param refPrimes
	 * @return
	 */
	boolean isPrimeUtiliseePointage(Integer idAgent, List<Integer> refPrimes);

	/**
	 * Filters Pointages by removing parents from the list.
	 * 
	 * @param pointages
	 *            - It is mandatory that the pointages are sorted by idPointage
	 *            asc
	 * @param etats
	 *            - Optional list of Etats to keep. If null, no filter will be
	 *            applied
	 * @return
	 */
	List<Pointage> filterOldPointagesAndEtatFromList(List<Pointage> pointages, List<EtatPointageEnum> etats,
			String typeHS);

	List<RefTypeAbsenceDto> getRefTypeAbsence();

	List<MotifHeureSupDto> getMotifHeureSup();

	void addEtatPointage(Pointage ptg, EtatPointageEnum etat, Integer idAgentCreator, Date dateEtat);

	List<Pointage> getPointagesVentilesAndRejetesForAgent(Integer idAgent, VentilDate ventilDate);

	ReturnMessageDto setMotifHeureSup(MotifHeureSupDto motifHeureSupDto);
}
