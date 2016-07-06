package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class DelegatorAndOperatorsDto implements IJSONSerialize,
		IJSONDeserialize<DelegatorAndOperatorsDto> {

	private AgentDto delegataire;
	private List<AgentDto> saisisseurs;

	public DelegatorAndOperatorsDto() {
		saisisseurs = new ArrayList<AgentDto>();
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
		return new JSONDeserializer<DelegatorAndOperatorsDto>()
				.deserializeInto(json, this);
	}

	public AgentDto getDelegataire() {
		return delegataire;
	}

	public void setDelegataire(AgentDto delegataire) {
		this.delegataire = delegataire;
	}

	public List<AgentDto> getSaisisseurs() {
		return saisisseurs;
	}

	public void setSaisisseurs(List<AgentDto> saisisseurs) {
		this.saisisseurs = saisisseurs;
	}
}
