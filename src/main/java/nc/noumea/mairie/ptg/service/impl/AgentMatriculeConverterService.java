package nc.noumea.mairie.ptg.service.impl;

import nc.noumea.mairie.ptg.service.AgentMatriculeConverterServiceException;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;

import org.springframework.stereotype.Service;

@Service
public class AgentMatriculeConverterService implements IAgentMatriculeConverterService {

	@Override
	public Integer fromADIdAgentToSIRHIdAgent(Integer adIdAgent) throws AgentMatriculeConverterServiceException {

		if (adIdAgent.toString().length() != 6)
			throw new AgentMatriculeConverterServiceException(String.format(
					"Impossible de convertir le matricule '%d' en matricule SIRH.", adIdAgent));

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

	@Override
	public Integer tryConvertIdAgentToNomatr(Integer idAgent) throws AgentMatriculeConverterServiceException {

		if (idAgent.toString().length() != 7)
			throw new AgentMatriculeConverterServiceException(String.format(
					"Impossible de convertir l'idAgent '%d' en matricule(nomatr) MAIRIE.", idAgent));

		return removeDigit(idAgent);
	}

	private Integer removeDigit(Integer idAgent) {

		return Integer.parseInt(idAgent.toString().substring(3, 7));
	}

}
