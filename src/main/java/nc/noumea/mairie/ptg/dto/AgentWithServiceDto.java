package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import flexjson.JSONDeserializer;

public class AgentWithServiceDto extends AgentDto implements IJSONSerialize, IJSONDeserialize<AgentWithServiceDto> {

	private String service;
	private Integer idServiceADS;
	private String statut;
	private String sigleService;

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
	
	public Integer getIdServiceADS() {
		return idServiceADS;
	}

	public void setIdServiceADS(Integer idServiceADS) {
		this.idServiceADS = idServiceADS;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	@Override
	public String serializeInJSON() {
		return null;
	}

	@Override
	public AgentWithServiceDto deserializeFromJSON(String json) {
		return new JSONDeserializer<AgentWithServiceDto>().deserializeInto(json, this);
	}

	public String getSigleService() {
		return sigleService;
	}

	public void setSigleService(String sigleService) {
		this.sigleService = sigleService;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
