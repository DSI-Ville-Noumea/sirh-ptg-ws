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
	
	public static RefTypePointageEnum getRefTypePointageEnum(Integer type) {
		
		if (type == null)
			return null;
		
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
}
