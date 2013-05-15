package nc.noumea.mairie.ptg.domain;

public enum TypePointageEnum {

	ABSENCE(1), H_SUP(2), PRIME(3);

	private int type;

	private TypePointageEnum(int _type) {
		this.type = _type;
	}

	public static TypePointageEnum getTypePointageEnum(int type) {
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
