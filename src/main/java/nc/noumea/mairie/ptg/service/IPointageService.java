package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;

public interface IPointageService {

	FichePointageDto getFilledFichePointageForAgent(int idAgent, Date dateLundi);
	
	List<RefEtatDto> getRefEtats();

	List<RefTypePointageDto> getRefTypesPointage();

	Pointage getOrCreateNewPointage(Integer idAgentCreator, Pointage pointage);
	Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage);
	Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi);
	Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi, Integer idRefPrime);

	FichePointageListDto getFichesPointageForUsers(String csvIdAgents, Date date);
	
	/**
	 * This method searches through the list of Pointages given a set of filters (agentId, from and to dates
	 * for dateDebut of a Pointage and type (H_SUP, PRIME, ABSENCE).
	 * In the case a Pointage has multiple records (to keep history on its different statuses)
	 * Only the latest version will be returned.
	 * Then we filter on the Etat (status) of the Pointage.
	 * This means that if a Pointage's old version satisfies all the filters but its new version doesn't
	 * this Pointage will not be returned at all.
	 * @param idAgent
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @param etats
	 * @return
	 */
	List<Pointage> getLatestPointagesForAgentAndDates(Integer idAgent, Date fromDate, Date toDate, RefTypePointageEnum type, List<EtatPointageEnum> etats);
	
	/**
	 * This method searches through the list of Pointages given a set of filters (idAgents, from and to dates
	 * for dateDebut of a Pointage and type (H_SUP, PRIME, ABSENCE).
	 * In the case a Pointage has multiple records (to keep history on its different statuses)
	 * Only the latest version will be returned.
	 * Then we filter on the Etat (status) of the Pointage.
	 * This means that if a Pointage's old version satisfies all the filters but its new version doesn't
	 * this Pointage will not be returned at all.
	 * @param idAgents
	 * @param fromDate
	 * @param toDate
	 * @param type
	 * @param etats
	 * @return
	 */
	List<Pointage> getLatestPointagesForAgentsAndDates(List<Integer> idAgents, Date fromDate, Date toDate, RefTypePointageEnum type, List<EtatPointageEnum> etats);
}
