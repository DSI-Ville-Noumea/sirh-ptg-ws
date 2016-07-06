package nc.noumea.mairie.domain;

public enum SpphreRecupEnum {

	P('P'),
	R('R');
	
	private char cdrecu;
	
	private SpphreRecupEnum(char _cdrecu) {
		cdrecu = _cdrecu;
	}
	
	@Override
	public String toString() {
		return Character.toString(cdrecu);
	}
	
}
