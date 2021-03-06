package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IApprobationService {

	List<ConsultPointageDto> getPointages(Integer idAgent, Date from, Date to, Integer idServiceAds, Integer agent, Integer idRefEtat, Integer idRefType, String typeHS);

	List<ConsultPointageDto> getPointagesSIRH(Date from, Date to, List<Integer> idAgents, Integer idRefEtat, Integer idRefType, String typeHS, Date dateEtat);

	List<ConsultPointageDto> getPointagesArchives(Integer idAgent, Integer idPointage);

	List<ConsultPointageDto> getPointagesArchives(Integer idPointage);

	ReturnMessageDto setPointagesEtat(Integer idAgent, List<PointagesEtatChangeDto> dto);

	ReturnMessageDto setPointagesEtatSIRH(Integer idAgent, List<PointagesEtatChangeDto> dto);

	List<AgentDto> listerTousAgentsPointages();

	void addRecuperationToAgent(EtatPointageEnum targetEtat, Pointage ptg);
}
