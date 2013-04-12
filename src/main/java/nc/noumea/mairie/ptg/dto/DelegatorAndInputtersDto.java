package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.List;

public class DelegatorAndInputtersDto implements IJSONSerialize {

	private AgentDto delegataire;
	private List<AgentDto> saisisseurs;
	
	public DelegatorAndInputtersDto() {
		saisisseurs = new ArrayList<AgentDto>();
	}
	
	@Override
	public String serializeInJSON() {
		// TODO Auto-generated method stub
		return null;
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
