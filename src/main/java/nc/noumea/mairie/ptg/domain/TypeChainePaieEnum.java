package nc.noumea.mairie.ptg.domain;

public enum TypeChainePaieEnum {

	CC("CC"), HCC("HCC");

	private String typeChainePaie;

	private TypeChainePaieEnum(String _typeChainePaie) {
		typeChainePaie = _typeChainePaie;
	}

	@Override
	public String toString() {
		return typeChainePaie;
	}
}
