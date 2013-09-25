package nc.noumea.mairie.ptg.service.impl;

/**
 * This is the class for Prime service
 *
 * @author C. Levointurier
 */
import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IPrimeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrimeService implements IPrimeService {

	@Autowired
	private IPointageRepository pointageRepository;

	public List<RefPrimeDto> getPrimeListForAgent(AgentStatutEnum statutAgent) {

		ArrayList<RefPrimeDto> res = new ArrayList<>();
		List<RefPrime> result = pointageRepository.getRefPrimesListForAgent(statutAgent);
		for (RefPrime p : result) {
			res.add(new RefPrimeDto(p));
		}
		return res;
	}

	public List<RefPrimeDto> getPrimeList() {

		ArrayList<RefPrimeDto> res = new ArrayList<>();
		List<RefPrime> result = pointageRepository.getRefPrimesList();
		for (RefPrime p : result) {
			res.add(new RefPrimeDto(p));
		}
		return res;
	}

	@Override
	public RefPrimeDto getPrimeWithNorubr(Integer noRubr) {
		
		List<RefPrime> result = pointageRepository.getRefPrimesListWithNoRubr(noRubr);
		
		if (result.size() == 0)
			return null;
		
		return new RefPrimeDto(result.get(0));
	}

	@Override
	public RefPrimeDto getPrimeById(Integer idRefPrime) {
		
		RefPrime result = pointageRepository.getEntity(RefPrime.class, idRefPrime);
		
		if (result == null)
			return null;
		
		return new RefPrimeDto(result);
	}

	@Override
	public List<RefPrimeDto> getPrimesByNorubr(Integer noRubr) {

		ArrayList<RefPrimeDto> res = new ArrayList<>();
		List<RefPrime> result = pointageRepository.getRefPrimesListWithNoRubr(noRubr);
		for (RefPrime p : result) {
			res.add(new RefPrimeDto(p));
		}
		return res;
	}

	@Override
	public List<Integer> getPrimesId(Integer noRubr) {
		List<Integer> res = new ArrayList<>();
		for (RefPrimeDto p : getPrimesByNorubr(noRubr)) {
			res.add(p.getIdRefPrime());
		}
		return res;
	}
}
