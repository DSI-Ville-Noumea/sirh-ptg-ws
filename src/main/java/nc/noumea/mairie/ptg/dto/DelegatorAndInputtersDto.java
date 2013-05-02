package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.List;

import flexjson.JSONSerializer;

public class DelegatorAndInputtersDto implements IJSONSerialize {

	private AgentDto delegataire;
	private List<AgentDto> saisisseurs;
	
	public DelegatorAndInputtersDto() {
		saisisseurs = new ArrayList<AgentDto>();
	}
	
	@Override
	public String serializeInJSON() {
		return new JSONSerializer()
			.include("delegataire")
			.include("saisisseurs.*")
			.exclude("*.class")
			.serialize(this);
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
