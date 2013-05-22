package nc.noumea.mairie.ptg.domain;

public enum RefTypePointageEnum {

	ABSENCE(1), H_SUP(2), PRIME(3);

	private RefTypePointageEnum(int _type) {
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

}
