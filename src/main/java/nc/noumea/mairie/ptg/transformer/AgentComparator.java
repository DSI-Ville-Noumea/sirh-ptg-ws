package nc.noumea.mairie.ptg.transformer;

import java.util.Comparator;

import nc.noumea.mairie.ptg.dto.AgentDto;

/**
 * Created by @author teo
 * the 12/04/17 to sort list of agent by their name.
 */
public class AgentComparator implements Comparator<AgentDto> {

	@Override
	public int compare(AgentDto o1, AgentDto o2) {
		return o1.getNom().compareTo(o2.getNom());
	}

}
