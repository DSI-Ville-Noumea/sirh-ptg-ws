package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spacti;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.SppactId;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.service.IExportAbsencePaieAbsenceService;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportAbsencePaieService implements IExportAbsencePaieAbsenceService {

	@Autowired
	private IExportPaieRepository exportPaieRepository;
	
	@Autowired
	private HelperService helperService;
	
	@Override
	public List<Sppact> exportAbsencesToPaie(List<Pointage> pointagesOrderedByDateAsc) {
		List<Sppact> modifiedOrAddedSppact = new ArrayList<Sppact>();
		
		for (Pointage ptg : pointagesOrderedByDateAsc) {
			
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;
			
			// Fetch or create Sppact
			Sppact act = findOrCreateSppactRecord(modifiedOrAddedSppact, ptg.getIdAgent(), ptg.getDateDebut(), ptg.getAbsenceConcertee());
			
			// Compute the time period in minutes, add them to potential existing minutes for that day, and convert them to Mairie Format
			Period p = new Period(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()));
			int nbMinutesAlreadySet = helperService.convertMairieNbHeuresFormatToMinutes(act.getNbHeures());
			act.setNbHeures(helperService.convertMinutesToMairieNbHeuresFormat(nbMinutesAlreadySet + p.getMinutes()));
			
			// List this Sppact as modified and prepare it in the list
			modifiedOrAddedSppact.add(act);
		}
		
		return modifiedOrAddedSppact;
	}
	
	protected Sppact findOrCreateSppactRecord(List<Sppact> existingRecords, Integer idAgent, Date dateJour, boolean isConcertee) {
		
		Sppact act = null;
		
		Integer nomatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Integer dateJourMairie = helperService.getIntegerDateMairieFromDate(dateJour);
		String codeActi = isConcertee ? Spacti.CODE_ACTIVITE_ABS_CONCERTEE : Spacti.CODE_ACTIVITE_ABS_NON_CONCERTEE;
		Spacti activite = Spacti.findSpacti(codeActi);
		
		// First search through existing spacti
		for (Sppact a : existingRecords) {
			if (a.getId().getNomatr().equals(nomatr) 
				&& a.getId().getDateJour().equals(dateJourMairie)
				&& a.getId().getActivite().equals(activite)) {
				act = a;
				break;
			}
		}

		// Then Look for an exising record already existing in the DB
		if (act == null) {
			act = exportPaieRepository.getSppactForDayAndAgent(idAgent, dateJour, codeActi);
		}
		
		// At last create a new record
		if (act == null) {
			act = new Sppact();
			act.setId(new SppactId(nomatr, dateJourMairie, activite));
		}
		
		return act;
	}
	
}
