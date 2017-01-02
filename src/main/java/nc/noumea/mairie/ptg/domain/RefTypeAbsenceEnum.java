package nc.noumea.mairie.ptg.domain;

public enum RefTypeAbsenceEnum {

	CONCERTEE(1), NON_CONCERTEE(2), IMMEDIATE(3), GREVE(4);

	private Integer type;

	private RefTypeAbsenceEnum(Integer _type) {
		type = _type;
	}

	@Override
	public String toString() {
		return this.name();
	}

	public Integer getValue() {
		return type;
	}

	public static RefTypeAbsenceEnum getRefTypeAbsenceEnum(Integer type) {

		if (type == null)
			return null;

		switch (type) {
			case 1:
				return CONCERTEE;
			case 2:
				return NON_CONCERTEE;
			case 3:
				return IMMEDIATE;
			case 4:
				return GREVE;
			default:
				return null;
		}
	}
}
