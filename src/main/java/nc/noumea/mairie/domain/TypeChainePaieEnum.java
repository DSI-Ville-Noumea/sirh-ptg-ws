package nc.noumea.mairie.domain;

public enum TypeChainePaieEnum {

	SHC("SHC"), // Hors Conventions Collectives (Fonctionnaires & Contractuels)
	SCV("SCV"), // Conventions Collectives
	ALL("ALL");

	private String typeChainePaie;

	private TypeChainePaieEnum(String _typeChainePaie) {
		typeChainePaie = _typeChainePaie;
	}

	@Override
	public String toString() {
		return typeChainePaie;
	}
}
