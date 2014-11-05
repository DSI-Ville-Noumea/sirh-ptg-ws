package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.domain.VentilTask;

public interface IVentilationRepository {

	List<Date> getDistinctDatesOfPointages(Integer idAgent, Date fromDate, Date toDate);

	List<Pointage> getListPointagesAbsenceAndHSupForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate,
			Date dateLundi);

	List<Pointage> getListPointagesPrimeForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate,
			Date dateDebutMois);

	List<Pointage> getListPointagesForPrimesCalculees(Integer idAgent, Date fromEtatDate, Date toEtatDate,
			Date dateLundi);

	List<PointageCalcule> getListPointagesCalculesPrimeForVentilation(Integer idAgent, Date dateDebutMois);

	List<Integer> getListIdAgentsForVentilationByDateAndEtat(Date fromDate, Date toDate);

	List<Integer> getListIdAgentsForExportPaie(Integer idVentilDate);

	VentilDate getLatestVentilDate(TypeChainePaieEnum chainePaie, boolean isPaid);

	void removeVentilationsForDateAgentAndType(VentilDate ventilDate, Integer idAgent, RefTypePointageEnum typePointage);

	boolean canStartVentilation(TypeChainePaieEnum chainePaie);

	List<VentilAbsence> getListOfVentilAbsenceForDateAgent(Integer ventilDateId, List<Integer> agentIds);

	List<VentilAbsence> getListOfVentilAbsenceForDateAgentAllVentilation(Integer ventilDateId, List<Integer> agentIds);

	List<VentilPrime> getListOfVentilPrimeForDateAgent(Integer ventilDateId, List<Integer> agentIds,
			boolean isShowVentilation);

	List<VentilPrime> getListOfVentilPrimeForDateAgentAllVentilation(Integer ventilDateId, List<Integer> agentIds,
			boolean isShowVentilation);

	List<VentilHsup> getListOfVentilHSForDateAgent(Integer ventilDateId, List<Integer> agentIds);

	List<VentilHsup> getListOfVentilHSForDateAgentAllVentilation(Integer ventilDateId, List<Integer> agentIds);

	List<VentilHsup> getListVentilHSupForAgentAndVentilDateOrderByDateAsc(Integer idAgent, Integer idVentilDate);

	List<VentilPrime> getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(Integer idAgent, Integer idVentilDate);

	VentilAbsence getPriorVentilAbsenceForAgentAndDate(Integer idAgent, Date dateLundi,
			VentilAbsence latestVentilAbsence);

	VentilHsup getPriorVentilHSupAgentAndDate(Integer idAgent, Date dateLundi, VentilHsup latestVentilAbsence);

	VentilPrime getPriorVentilPrimeForAgentAndDate(Integer idAgent, Date dateDebMois, VentilPrime latestVentilAbsence);

	void persistEntity(Object entity);

	List<VentilTask> getListOfVentilTaskErreur(TypeChainePaieEnum chainePaie, VentilDate ventilDateTo);

	List<VentilAbsence> getListOfVentilAbsenceForAgentBeetweenDate(Integer mois, Integer annee, Integer idAgent);

	List<VentilAbsence> getListOfVentilAbsenceForAgentBeetweenDateAllVentilation(Integer mois, Integer annee,
			Integer idAgent);

	List<VentilHsup> getListOfVentilHSForAgentBeetweenDate(Integer mois, Integer annee, Integer idAgent);

	List<VentilHsup> getListOfVentilHSForAgentBeetweenDateAllVentilation(Integer mois, Integer annee, Integer idAgent);

	List<Integer> getListAgentsForShowVentilationPrimesForDate(Integer ventilDateId, Integer agentMin,
			Integer agentMax, boolean allVentilation);

	List<Integer> getListAgentsForShowVentilationAbsencesForDate(Integer ventilDateId, Integer agentMin,
			Integer agentMax, boolean allVentilation);

	List<Integer> getListAgentsForShowVentilationHeuresSupForDate(Integer ventilDateId, Integer agentMin,
			Integer agentMax, boolean allVentilation);

	List<VentilAbsence> getListVentilAbsencesForAgentAndVentilDate(Integer idAgent, Integer idVentilDate);

	List<VentilHsup> getListOfOldVentilHSForAgentAndDateLundi(Integer idAgent, Date dateLundi, Integer ventilDateId);

	List<VentilAbsence> getListOfVentilAbsenceWithDateForEtatPayeur(Integer idVentilDate);

	List<VentilHsup> getListOfVentilHeuresSupWithDateForEtatPayeur(Integer idVentilDate);

	List<VentilPrime> getListOfVentilPrimeWithDateForEtatPayeur(Integer idVentilDate);

	List<VentilPrime> getListOfOldVentilPrimeForAgentAndDateDebutMois(Integer idAgent, Date dateDebutMois,
			Integer ventilDateId);

	List<Pointage> getListPointagesPrimeValideByMoisAndRefPrime(Integer idAgent, Date dateMois, Integer idRefPrime);

	List<Integer> getListIdAgentsWithPointagesValidatedAndRejetes(Integer idVentilDate);

	List<VentilHsup> getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(
			Integer idAgent, Integer idVentilDate);
}
