package nc.noumea.mairie.domain;

public enum SpWfEtatEnum {

	PRET(0),
	ECRITURE_POINTAGES_EN_COURS(1),
	ECRITURE_POINTAGES_TERMINEE(2),
	CALCUL_SALAIRE_EN_COURS(3),
	CALCUL_SALAIRE_TERMINE(4),
	ETATS_PAYEUR_EN_COURS(5),
	ETATS_PAYEUR_TERMINES(6),
	JOURNAL_EN_COURS(7),
	JOURNAL_TERMINE(8),
	VALIDATION_PAIE_EN_COURS(9);
	
	private int codeEtat;
	
	private SpWfEtatEnum(int codeEtat) {
		this.codeEtat = codeEtat;
	}
	
	public int getCodeEtat() {
		return this.codeEtat;
	}
	
	public String toString() {
		return String.format("%s : %s", codeEtat, this.name());
	}
}
