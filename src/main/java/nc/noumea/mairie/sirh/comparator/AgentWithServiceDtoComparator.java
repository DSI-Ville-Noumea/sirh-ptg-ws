package nc.noumea.mairie.sirh.comparator;

import java.util.Comparator;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

public class AgentWithServiceDtoComparator implements Comparator<AgentWithServiceDto> {

	@Override
	public int compare(AgentWithServiceDto o1, AgentWithServiceDto o2) {
		return o1.getNom().toUpperCase().compareTo(o2.getNom().toUpperCase());
	}

}
