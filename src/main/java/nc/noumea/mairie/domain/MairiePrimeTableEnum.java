package nc.noumea.mairie.domain;

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
