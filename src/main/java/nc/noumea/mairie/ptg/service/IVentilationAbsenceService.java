package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilAbsence;

public interface IVentilationAbsenceService {

	VentilAbsence processAbsenceAgent(Integer idAgent, List<Pointage> pointages, Date dateLundi, 
			List<Pointage> pointagesVentilesRejetes, List<Pointage> pointagesJournalisesRejetes);
}
