package nc.noumea.mairie.ptg.domain;

public enum TypeSaisieEnum {

	CASE_A_COCHER(1),
	NB_HEURES(2),
	NB_INDEMNITES(3),
	PERIODE_HEURES(4);
	
	private int saisie;
	
	private TypeSaisieEnum(int _saisie) {
		this.saisie = _saisie;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
}
