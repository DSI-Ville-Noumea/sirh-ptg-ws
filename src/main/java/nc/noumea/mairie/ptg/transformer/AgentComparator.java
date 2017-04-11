package nc.noumea.mairie.ptg.transformer;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import nc.noumea.mairie.ptg.dto.AgentDto;

/**
 * Created by @author teo
 * the 12/04/17 to sort list of agent by their name.
 */
public class AgentComparator implements Comparator<AgentDto> {

	@Override
	public int compare(AgentDto o1, AgentDto o2) {
		if (StringUtils.isNotBlank(o1.getNom()) && StringUtils.isNotBlank(o2.getNom()))
			return o1.getNom().compareTo(o2.getNom());
		else
			return o1.getIdAgent().compareTo(o2.getIdAgent());
	}

}
