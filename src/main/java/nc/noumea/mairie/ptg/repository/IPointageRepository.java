package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

public interface IPointageRepository {

	List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut);

	List<RefPrime> getRefPrimesCalculees(List<Integer> noRubrList, AgentStatutEnum statut);

	List<RefPrime> getRefPrimesListForAgent(AgentStatutEnum statut);

	List<RefPrime> getRefPrimesList();

	List<Pointage> getPointagesForAgentAndDateOrderByIdDesc(int idAgent, Date dateLundi);

	List<Pointage> getListPointages(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType);

	List<Pointage> getPointagesVentilesForAgent(Integer idAgent, Integer idVentilDate);

	List<PointageCalcule> getPointagesCalculesVentilesForAgent(Integer idAgent, Integer idVentilDate);

	List<Pointage> getPointageArchives(Integer idPointage);

	int removePointageCalculesForDateAgent(Integer idAgent, Date dateLundi);

	void savePointage(Pointage ptg);

	List<RefPrime> getRefPrimesListWithNoRubr(Integer noRubr);

	<T> T getEntity(Class<T> Tclass, Object Id);

	void persisEntity(Object entity);

	void removeEntity(Object entity);

	boolean isPrimeSurPointageouPointageCalcule(Integer idAgent, Integer idRefPrime);

	List<RefEtat> findAllRefEtats();

	List<RefTypePointage> findAllRefTypePointages();

	List<Integer> listAllDistinctIdAgentPointage();

	List<RefTypeAbsence> findAllRefTypeAbsence();

	List<Integer> getListApprobateursPointagesSaisiesJourDonne();

	List<MotifHeureSup> findAllMotifHeureSup();

	List<Pointage> getListPointagesVerification(Integer idAgent, Date fromDate, Date toDate, Integer idRefType);
}
