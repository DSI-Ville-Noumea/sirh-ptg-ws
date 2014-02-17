package nc.noumea.mairie.ptg.domain;


public enum TypeAbsenceEnum {

	CONCERTEE(1), NON_CONCERTEE(2), IMMEDIATE(3);
	
	private int type;
	
	private TypeAbsenceEnum(int _type) {
		type = _type;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
	public int getValue() {
		return type;
	}
	
	public static TypeAbsenceEnum getTypeAbsenceEnum(Integer type) {
		
		if (type == null)
			return null;
		
		switch (type) {
			case 1:
				return CONCERTEE;
			case 2:
				return NON_CONCERTEE;
			case 3:
				return IMMEDIATE;
			default:
				return null;
		}
	}
}
