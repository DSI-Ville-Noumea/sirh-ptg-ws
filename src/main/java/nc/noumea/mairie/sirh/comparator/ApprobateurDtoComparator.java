package nc.noumea.mairie.sirh.comparator;

import java.util.Comparator;

import nc.noumea.mairie.ptg.dto.ApprobateurDto;

public class ApprobateurDtoComparator implements Comparator<ApprobateurDto> {

	@Override
	public int compare(ApprobateurDto o1, ApprobateurDto o2) {
		// tri par nom d'usage
		return o1.getApprobateur().getNom().toUpperCase().compareTo(o2.getApprobateur().getNom().toUpperCase());
	}

}
