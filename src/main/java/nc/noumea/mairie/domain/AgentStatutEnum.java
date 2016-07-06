package nc.noumea.mairie.domain;

public enum AgentStatutEnum {

	F("F"),
	C("C"),
	CC("CC");
	
	private String statut;
	
	private AgentStatutEnum(String _statut) {
		statut = _statut;
	}
	
	@Override
	public String toString() {
		return statut;
	}
}
