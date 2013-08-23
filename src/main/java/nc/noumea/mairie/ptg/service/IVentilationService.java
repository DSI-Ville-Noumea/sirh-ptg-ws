package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IVentilationService {

	ReturnMessageDto processVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType);
        
  	List showVentilation(List<Integer> agents, Integer idDateVentil,  RefTypePointageEnum pointageType);
         
}
