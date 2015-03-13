package nc.noumea.mairie.ptg.dto;

import flexjson.JSONDeserializer;

public class ApprobateurDto implements IJSONSerialize, IJSONDeserialize<ApprobateurDto> {

	private AgentWithServiceDto approbateur;
	private AgentDto delegataire;

	public ApprobateurDto() {

	}

	@Override
	public String serializeInJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApprobateurDto deserializeFromJSON(String json) {
		return new JSONDeserializer<ApprobateurDto>().deserializeInto(json, this);
	}

	public AgentDto getDelegataire() {
		return delegataire;
	}

	public void setDelegataire(AgentDto delegataire) {
		this.delegataire = delegataire;
	}

	public AgentWithServiceDto getApprobateur() {
		return approbateur;
	}

	public void setApprobateur(AgentWithServiceDto approbateur) {
		this.approbateur = approbateur;
	}
}
