package nc.noumea.mairie.ptg.domain;

public enum EtatPointageEnum {

	SAISI(0),
	APPROUVE(1),
	REFUSE(2),
	REFUSE_DEFINITIVEMENT(3),
	VENTILE(4),
	REJETE(5),
	REJETE_DEFINITIVEMENT(6),
	VALIDE(7),
	EN_ATTENTE(8),
	JOURNALISE(9);
	
	private int codeEtat;
	
	EtatPointageEnum(int _value) {
		codeEtat = _value;
	}

	public int getCodeEtat() {
		return codeEtat;
	}
	
	@Override
	public String toString() {
		return String.valueOf(codeEtat);
	}
}
