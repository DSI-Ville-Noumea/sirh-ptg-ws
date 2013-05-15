package nc.noumea.mairie.ptg.domain;

public enum TypeSaisieEnum {

	CASE_A_COCHER(1),
	NB_HEURES(2),
	NB_INDEMNITES(3),
	PERIODE_HEURES(4);
	
	private TypeSaisieEnum(int _saisie) {
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
}
