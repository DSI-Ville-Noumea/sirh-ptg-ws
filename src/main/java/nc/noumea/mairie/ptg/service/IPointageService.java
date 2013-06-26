package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;

public interface IPointageService {


	public FichePointageDto getFilledFichePointageForAgent(int idAgent, Date dateLundi);
	
	public List<RefEtatDto> getRefEtats();

	public List<RefTypePointageDto> getRefTypesPointage();

	public Pointage getOrCreateNewPointage(Integer idAgentCreator, Pointage pointage);
	public Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage);
	public Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi);
	public Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi, Integer idRefPrime);

	public FichePointageListDto getFichesPointageForUsers(String csvIdAgents, Date date);
	
	public List<Pointage> getLatestPointagesForAgentAndDates(Integer idAgent, Date fromDate, Date toDate, List<EtatPointageEnum> etats);

}
