package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilPrime;

public interface IVentilationPrimeService {

	List<VentilPrime> processPrimesAgent(Integer idAgent, List<Pointage> pointages, Date dateDebutMois);
	List<VentilPrime> generatePrimesAgent(Integer idAgent, List<Pointage> pointagres, Date dateDebutMois);
}
