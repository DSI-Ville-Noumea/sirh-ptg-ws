package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;

public interface IApprobationService {

	List<ConsultPointageDto> getPointages(Integer idAgent, Date from, Date to,
			String codeService, Integer agent, Integer idRefEtat,
			Integer idRefType);
	
	List<ConsultPointageDto> getPointagesArchives(Integer idAgent, Integer idPointage);
	
	SaisieReturnMessageDto setPointagesEtat(Integer idAgent, List<PointagesEtatChangeDto> dto);
}
