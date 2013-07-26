package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.Spacti;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.service.IExportAbsencePaieAbsenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportAbsencePaieService implements IExportAbsencePaieAbsenceService {

	@Autowired
	private IExportPaieRepository exportPaieRepository;
	
	@Override
	public List<Sppact> exportAbsencesToPaie(List<Pointage> pointagesOrderedByDateAsc) {
		List<Sppact> modifiedOrAddedSppact = new ArrayList<Sppact>();
		
		for (Pointage ptg : pointagesOrderedByDateAsc) {
			
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;
			
			String codeActi = ptg.getAbsenceConcertee() ? Spacti.CODE_ACTIVITE_ABS_CONCERTEE : Spacti.CODE_ACTIVITE_ABS_NON_CONCERTEE;
			
			Sppact act = exportPaieRepository.getSppactForDayAndAgent(ptg.getIdAgent(), ptg.getDateDebut(), codeActi);
			
			if (act == null) {
				act = new Sppact();
			}
			
		}
		
		return modifiedOrAddedSppact;
	}
	
}
