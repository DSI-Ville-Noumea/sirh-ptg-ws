package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.sirh.domain.PrimePointage;

public interface IPointageRepository {

	List<Integer> getIdPointagesParents(Pointage pointage);

	List<PrimePointage> getPrimePointagesByAgent(Integer idAgent, Date date);
	
	List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut);
	
	List<Pointage> getPointagesForAgentAndDateOrderByIdDesc(int idAgent, Date dateLundi);

	void savePointage(Pointage ptg);
}
