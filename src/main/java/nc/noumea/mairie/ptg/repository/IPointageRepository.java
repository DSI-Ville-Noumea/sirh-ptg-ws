package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;

public interface IPointageRepository {

	List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut);

	List<RefPrime> getRefPrimesListForAgent(AgentStatutEnum statut) ;
	
	List<RefPrime> getRefPrimesList() ;
	
	List<Pointage> getPointagesForAgentAndDateOrderByIdDesc(int idAgent, Date dateLundi);

	List<Pointage> getListPointages(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType);
	List<Pointage> getListPointagesNative(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType);

	List<Pointage> getPointageArchives(Integer idPointage);

	void removePointageCalculesForDateAgent(Integer idAgent, Date dateLundi);

	void savePointage(Pointage ptg);

	<T> T getEntity(Class<T> Tclass, Object Id);
}
