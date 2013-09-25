package nc.noumea.mairie.ptg.service;

/**
 * This is the interface for Prime service
 *
 * @author C. Levointurier
 */
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;

public interface IPrimeService {

	public List<RefPrimeDto> getPrimeListForAgent(AgentStatutEnum statutAgent);

	public List<RefPrimeDto> getPrimeList();

	public RefPrimeDto getPrimeWithNorubr(Integer noRubr);

	public RefPrimeDto getPrimeById(Integer idRefPrime);

	public List<RefPrimeDto> getPrimesByNorubr(Integer noRubr);

	public List<Integer> getPrimesId(Integer noRubr);
}
