package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

public interface IVentilationRepository {

	List<Date> getDistinctDatesOfPointages(Integer idAgent, Date fromDate, Date toDate);
	
	List<Pointage> getListPointagesAbsenceAndHSupForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate, Date dateLundi);
	
	List<Pointage> getListPointagesPrimeForVentilation(Integer idAgent, Date fromEtatDate, Date toEtatDate, Date dateDebutMois);
	
	List<Pointage> getListPointagesForPrimesCalculees(Integer idAgent, Date fromEtatDate, Date toEtatDate, Date dateLundi);
	
	List<PointageCalcule> getListPointagesCalculesPrimeForVentilation(Integer idAgent, Date dateDebutMois);
	
	List<Integer> getListIdAgentsForVentilationByDateAndEtat(Date fromDate, Date toDate);
	
	VentilDate getLatestVentilDate(TypeChainePaieEnum chainePaie, boolean isPaid);

	void removeVentilationsForDateAgentAndType(VentilDate ventilDate, Integer idAgent, RefTypePointageEnum typePointage);
	
	List<Pointage> getPointagesVentilesForAgent(Integer idAgent, Integer idVentilDate);
}
