package nc.noumea.mairie.ptg.service;

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
		
		StringBuilder newIdSb = new StringBuilder();
		newIdSb.append(adIdAgent.toString().substring(0, 2));
		newIdSb.append("0");
		newIdSb.append(adIdAgent.toString().substring(2, 6));
		
		return Integer.parseInt(newIdSb.toString());
	}

}
