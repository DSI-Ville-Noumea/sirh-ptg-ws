package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class DelegatorAndOperatorsDto implements IJSONSerialize, IJSONDeserialize<DelegatorAndOperatorsDto> {

	private AgentWithServiceDto delegataire;
	private List<AgentWithServiceDto> saisisseurs;
	
	public DelegatorAndOperatorsDto() {
		saisisseurs = new ArrayList<AgentWithServiceDto>();
	}
	
	@Override
	public String serializeInJSON() {
		return new JSONSerializer()
			.exclude("*.class")
			.include("delegataire")
			.include("saisisseurs.*")
			.serialize(this);
	}

	@Override
	public DelegatorAndOperatorsDto deserializeFromJSON(String json) {
		return new JSONDeserializer<DelegatorAndOperatorsDto>().deserializeInto(json, this);
	}

	public AgentWithServiceDto getDelegataire() {
		return delegataire;
	}

	public void setDelegataire(AgentWithServiceDto delegataire) {
		this.delegataire = delegataire;
	}

	public List<AgentWithServiceDto> getSaisisseurs() {
		return saisisseurs;
	}

	public void setSaisisseurs(List<AgentWithServiceDto> saisisseurs) {
		this.saisisseurs = saisisseurs;
	}

	
}
