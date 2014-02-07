package nc.noumea.mairie.ptg.service.impl;

import nc.noumea.mairie.ptg.service.AgentMatriculeConverterServiceException;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;

import org.springframework.stereotype.Service;

@Service
public class AgentMatriculeConverterService implements IAgentMatriculeConverterService {

	@Override
	public Integer fromADIdAgentToSIRHIdAgent(Integer adIdAgent) throws AgentMatriculeConverterServiceException {
		
		if (adIdAgent.toString().length() != 6)
			throw new AgentMatriculeConverterServiceException(String.format("Impossible de convertir le matricule '%d' en matricule SIRH.", adIdAgent));
		
		return addMissingDigit(adIdAgent);
	}

	@Override
	public Integer tryConvertFromADIdAgentToSIRHIdAgent(Integer adIdAgent) {

		if (adIdAgent == null || adIdAgent.toString().length() != 6)
			return adIdAgent;
		
		return addMissingDigit(adIdAgent);
	}

	private Integer addMissingDigit(Integer adIdAgent) {

		return Integer.parseInt(adIdAgent.toString().substring(0, 2) + "0" + adIdAgent.toString().substring(2, 6));
	}

}
