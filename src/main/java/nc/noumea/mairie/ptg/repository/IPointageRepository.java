package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

public interface IPointageRepository {

	List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut);

	List<Pointage> getPointagesForAgentAndDateOrderByIdDesc(int idAgent, Date dateLundi);

	List<Pointage> getListPointages(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType);
	
	List<Pointage> getPointageArchives(Integer idPointage);

	List<Pointage> getListPointagesForVentilationByDateEtat(Integer idAgent, Date fromDate, Date toDate, RefTypePointageEnum pointageType);

	VentilDate getLatestVentilDate(TypeChainePaieEnum chainePaie, boolean isPaid);

	void removeVentilationsForDateAgentAndType(VentilDate ventilDate, Integer idAgent, RefTypePointageEnum typePointage);
	
	void removePointageCalculesForDateAgent(Integer idAgent, Date from, Date to);
	
	void savePointage(Pointage ptg);

	<T> T getEntity(Class<T> Tclass, Object Id);
}
