package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import flexjson.JSONDeserializer;

public class AgentWithServiceDto extends AgentDto implements IJSONSerialize, IJSONDeserialize<AgentWithServiceDto> {

	private String service;
	private String codeService;
	private String statut;

	public AgentWithServiceDto() {

	}

	public AgentWithServiceDto(AgentGeneriqueDto agent) {
		super(agent);
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getCodeService() {
		return codeService;
	}

	public void setCodeService(String codeService) {
		this.codeService = codeService;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	@Override
	public String serializeInJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgentWithServiceDto deserializeFromJSON(String json) {
		return new JSONDeserializer<AgentWithServiceDto>().deserializeInto(json, this);
	}
}
