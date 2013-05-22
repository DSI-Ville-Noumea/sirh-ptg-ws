package nc.noumea.mairie.ptg.domain;

public enum RefTypePointageEnum {

	ABSENCE(1), H_SUP(2), PRIME(3);

	private int type;
	
	private RefTypePointageEnum(int _type) {
		type = _type;
	}

	public int getValue() {
		return type;
	}
	
	public static RefTypePointageEnum getRefTypePointageEnum(int type) {
		switch (type) {
		case 1:
			return ABSENCE;
		case 2:
			return H_SUP;
		case 3:
			return PRIME;
		default:
			return null;
		}
	}
		
	public RefTypePointage getRefTypePointage() {
		return RefTypePointage.findRefTypePointage(this.getValue());
	}

}
