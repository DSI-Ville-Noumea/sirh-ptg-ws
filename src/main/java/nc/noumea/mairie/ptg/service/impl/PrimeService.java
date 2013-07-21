package nc.noumea.mairie.ptg.service.impl;


/**
 * This is the class for Prime service 
 * @author C. Levointurier
 */
import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IFichesService;
import nc.noumea.mairie.ptg.service.IPrimeService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrimeService implements IPrimeService {
	
	@Autowired
	private IPointageRepository pointageRepository;

		
	public List<RefPrimeDto> getPrimeListForAgent(AgentStatutEnum statutAgent) {
		
		ArrayList<RefPrimeDto> res= new ArrayList<>();
		List<RefPrime> result = pointageRepository.getRefPrimesListForAgent(statutAgent);
		for (RefPrime p : result) {
			res.add(new RefPrimeDto(p));
		}
		return res;
	}

}
