package nc.noumea.mairie.domain;

/**
 * SPPPRM contient les primes dont les calculs se font à la journée.
 * SPPRIM contient les primes dont les calculs se font sur le mois entier.
 * 
 * @author teo
 *
 */
public enum MairiePrimeTableEnum {
	SPPPRM("SPPPRM"), 
	SPPRIM("SPPRIM");
	
	private String primeTable;
	
	private MairiePrimeTableEnum(String primeTable) {
		this.primeTable = primeTable;
	}
	
	@Override
	public String toString() {
		return primeTable;
	}
}
