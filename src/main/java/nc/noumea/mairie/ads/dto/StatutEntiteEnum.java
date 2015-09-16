package nc.noumea.mairie.ads.dto;

import java.util.ArrayList;
import java.util.List;

public enum StatutEntiteEnum {

	PREVISION(0, "Prévision"), ACTIF(1, "Actif"), TRANSITOIRE(2, "Transitoire"), INACTIF(3, "Inactif");

	private int idRefStatutEntite;
	private String libStatutEntite;

	StatutEntiteEnum(int _value, String libelle) {
		idRefStatutEntite = _value;
		libStatutEntite = libelle;
	}

	public int getIdRefStatutEntite() {
		return idRefStatutEntite;
	}

	public static StatutEntiteEnum getStatutEntiteEnum(Integer idRefStatutEntite) {

		if (idRefStatutEntite == null)
			return null;

		switch (idRefStatutEntite) {
			case 0:
				return PREVISION;
			case 1:
				return ACTIF;
			case 2:
				return TRANSITOIRE;
			case 3:
				return INACTIF;
			default:
				return null;
		}
	}

	public static List<StatutEntiteEnum> getAllStatutEntiteEnum() {
		List<StatutEntiteEnum> result = new ArrayList<StatutEntiteEnum>();
		result.add(PREVISION);
		result.add(ACTIF);
		result.add(TRANSITOIRE);
		result.add(INACTIF);
		return result;
	}

	public String getLibStatutEntite() {
		return libStatutEntite;
	}
}
