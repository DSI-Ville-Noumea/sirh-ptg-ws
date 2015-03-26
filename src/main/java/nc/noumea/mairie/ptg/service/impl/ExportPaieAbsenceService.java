package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spacti;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.SppactId;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IExportPaieAbsenceService;
import nc.noumea.mairie.repository.IMairieRepository;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportPaieAbsenceService implements IExportPaieAbsenceService {

	@Autowired
	private IExportPaieRepository exportPaieRepository;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IPointageRepository pointageRepository;

	@Override
	public List<Sppact> exportAbsencesToPaie(List<Pointage> pointagesOrderedByDateAsc) {
		List<Sppact> modifiedOrAddedSppact = new ArrayList<Sppact>();

		for (Pointage ptg : pointagesOrderedByDateAsc) {

			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;

			// Fetch or create Sppact
			Sppact act = findOrCreateSppactRecord(modifiedOrAddedSppact, ptg.getIdAgent(), ptg.getDateDebut(), ptg
					.getRefTypeAbsence().getIdRefTypeAbsence());

			Period p = new Period(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()));
			
			// #14681 attention, dans l AS400, il ne peut y avoir qu une seule ligne
			// mais dans le nouveau systeme, l agent peut saisir 2 absences
			// on cherche une 2e absence sur le meme jour
			DateTime dateTimeDebutPtg = new DateTime(ptg.getDateDebut());
			Period periodBis = null; 
			for(Pointage ptgBis : pointagesOrderedByDateAsc) {
				if (ptgBis.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
					continue;
				
				if(ptg.getIdPointage().equals(ptgBis.getIdPointage()))
					continue;
				
				if(!ptg.getRefTypeAbsence().getIdRefTypeAbsence().equals(ptgBis.getRefTypeAbsence().getIdRefTypeAbsence()))
					continue;
				
				DateTime dateTimeDebutPtgBis = new DateTime(ptgBis.getDateDebut());
				if(dateTimeDebutPtg.getDayOfYear() == dateTimeDebutPtgBis.getDayOfYear()) {
					periodBis = new Period(new DateTime(ptgBis.getDateDebut()), new DateTime(ptgBis.getDateFin()));
				}
			}
			int minutesTotalOfDay = p.toStandardMinutes().getMinutes() 
					+ (periodBis != null ? periodBis.toStandardMinutes().getMinutes() : 0);
			act.setNbHeures(helperService.convertMinutesToMairieNbHeuresFormat(minutesTotalOfDay));

			if (act.getNbHeures() == 0d) {
				modifiedOrAddedSppact.remove(act);
				mairieRepository.removeEntity(act);
			}
		}

		return modifiedOrAddedSppact;
	}

	protected Sppact findOrCreateSppactRecord(List<Sppact> existingRecords, Integer idAgent, Date dateJour,
			Integer refTypeAbsence) {

		Sppact act = null;

		Integer nomatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Integer dateJourMairie = helperService.getIntegerDateMairieFromDate(dateJour);
		String codeActi = "";
		switch (RefTypeAbsenceEnum.getRefTypeAbsenceEnum(refTypeAbsence)) {
			case CONCERTEE:
				codeActi = Spacti.CODE_ACTIVITE_ABS_CONCERTEE;
				break;
			case NON_CONCERTEE:
				codeActi = Spacti.CODE_ACTIVITE_ABS_NON_CONCERTEE;
				break;
			case IMMEDIATE:
				codeActi = Spacti.CODE_ACTIVITE_ABS_IMMEDIATE;
				break;
		}

		Spacti activite = mairieRepository.getEntity(Spacti.class, codeActi);

		// First search through existing spacti
		for (Sppact a : existingRecords) {
			if (a.getId().getNomatr().equals(nomatr) && a.getId().getDateJour().equals(dateJourMairie)
					&& a.getId().getActivite().equals(activite)) {
				return a;
			}
		}

		// Then Look for an exising record already existing in the DB
		act = exportPaieRepository.getSppactForDayAndAgent(idAgent, dateJour, codeActi);

		// At last create a new record
		if (act == null) {
			act = new Sppact();
			act.setId(new SppactId(nomatr, dateJourMairie, activite));
		}

		existingRecords.add(act);

		return act;
	}
	
	@Override
	public void deleteSppactFromAbsencesRejetees(List<Pointage> listPointageRejetesVentilesOrderedByDateAsc) {
		
		for (Pointage ptg : listPointageRejetesVentilesOrderedByDateAsc) {

			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;

			String codeActi = "";
			switch (RefTypeAbsenceEnum.getRefTypeAbsenceEnum(ptg.getRefTypeAbsence().getIdRefTypeAbsence())) {
				case CONCERTEE:
					codeActi = Spacti.CODE_ACTIVITE_ABS_CONCERTEE;
					break;
				case NON_CONCERTEE:
					codeActi = Spacti.CODE_ACTIVITE_ABS_NON_CONCERTEE;
					break;
				case IMMEDIATE:
					codeActi = Spacti.CODE_ACTIVITE_ABS_IMMEDIATE;
					break;
			}

			// Then delete for an exising record already existing in the DB
			exportPaieRepository.deleteSppactForDayAndAgent(ptg.getIdAgent(), ptg.getDateDebut(), codeActi);
		}
	}
}
