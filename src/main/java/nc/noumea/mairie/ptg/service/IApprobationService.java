package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.ConsultPointageDto;

public interface IApprobationService {

	List<ConsultPointageDto> getPointages(Integer idAgent, Date from, Date to,
			String codeService, Integer agent, Integer idRefEtat,
			Integer idRefType);
	
	List<ConsultPointageDto> getPointagesArchives(Integer idAgent, Integer idPointage);
}
