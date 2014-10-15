package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.ISaisieService;
import nc.noumea.mairie.ptg.service.NotAMondayException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaisieService implements ISaisieService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IVentilationRepository ventilationRepository;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private IPointageService pointageService;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IPointageDataConsistencyRules ptgDataCosistencyRules;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Override
	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDto fichePointageDto) {
		return saveFichePointage(idAgentOperator, fichePointageDto, false);
	}

	@Override
	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDto fichePointageDto,
			boolean approveModifiedPointages) {

		Date dateLundi = fichePointageDto.getDateLundi();

		if (!helperService.isDateAMonday(dateLundi))
			throw new NotAMondayException();

		ReturnMessageDto result = new ReturnMessageDto();
		if (!approveModifiedPointages)
			result = ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(result, dateLundi);
		if (!result.getErrors().isEmpty())
			return result;

		Integer idAgent = fichePointageDto.getAgent().getIdAgent();

		List<Pointage> originalAgentPointages = pointageService.getLatestPointagesForSaisieForAgentAndDateMonday(
				idAgent, dateLundi);

		List<Pointage> finalPointages = new ArrayList<Pointage>();
		boolean isPointageAbsenceModifie = false;
		boolean isPointageHSupModifie = false;

		for (JourPointageDto jourDto : fichePointageDto.getSaisies()) {

			for (AbsenceDto abs : jourDto.getAbsences()) {

				if (abs.isaSupprimer())
					continue;

				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointages, abs);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChanged(ptg, abs)) {
					continue;
				}

				// Only if it has changed, process this pointage
				ptg = pointageService.getOrCreateNewPointage(idAgentOperator, abs.getIdPointage(), idAgent, dateLundi,
						helperService.getCurrentDate());
				if (null != abs.getIdRefTypeAbsence()) {
					ptg.setRefTypeAbsence(pointageRepository.getEntity(RefTypeAbsence.class, abs.getIdRefTypeAbsence()));
				}
				ptg.setDateDebut(abs.getHeureDebut());
				ptg.setDateFin(abs.getHeureFin());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.ABSENCE.getValue()));

				crudComments(ptg, abs.getMotif(), abs.getCommentaire());

				isPointageAbsenceModifie = true;
				finalPointages.add(ptg);
			}

			for (HeureSupDto hs : jourDto.getHeuresSup()) {

				if (hs.isaSupprimer())
					continue;

				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointages, hs);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChanged(ptg, hs)) {
					continue;
				}

				// Only if it has changed, process this pointage
				ptg = pointageService.getOrCreateNewPointage(idAgentOperator, hs.getIdPointage(), idAgent, dateLundi,
						helperService.getCurrentDate());
				//cas de la DMP #11622
				//cas de la DPM #11622
				SirhWsServiceDto service = sirhWsConsumer.getAgentDirection(idAgent);
				if(service.getSigle().toUpperCase().equals("DPM")){
					ptg.setHeureSupRecuperee(true);
				}else{
					ptg.setHeureSupRecuperee(hs.getRecuperee());
				}
				ptg.setHeureSupRappelService(hs.getRappelService());
				ptg.setDateDebut(hs.getHeureDebut());
				ptg.setDateFin(hs.getHeureFin());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue()));

				crudComments(ptg, hs.getMotif(), hs.getCommentaire());

				isPointageHSupModifie = true;
				finalPointages.add(ptg);
			}

			for (PrimeDto prime : jourDto.getPrimes()) {

				// if the new pointage has no idPointage, null qte, null
				// datedebut and datefin, leave it (it is a template)
				if (prime.isaSupprimer() || prime.getIdPointage() == null && prime.getHeureFin() == null
						&& (prime.getQuantite() == null || prime.getQuantite().equals(0))) {
					continue;
				}

				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointages, prime);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChanged(ptg, prime)) {
					continue;
				}

				// Only if it has changed, process this pointage
				ptg = pointageService.getOrCreateNewPointage(idAgentOperator, prime.getIdPointage(), idAgent,
						dateLundi, helperService.getCurrentDate(), prime.getIdRefPrime());
				ptg.setDateDebut(prime.getHeureDebut() == null ? jourDto.getDate() : prime.getHeureDebut());
				ptg.setDateFin(prime.getHeureFin());
				ptg.setQuantite(prime.getQuantite());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue()));

				crudComments(ptg, prime.getMotif(), prime.getCommentaire());

				finalPointages.add(ptg);
			}
		}

		// calling data consistency
		ptgDataCosistencyRules.processDataConsistency(result, idAgent, dateLundi, finalPointages);

		// If any blocking error, return the list of problems and do not save
		if (result.getErrors().size() != 0)
			return result;

		// si une heure sup ou absence est modifiee,
		// on repasse toutes les hsup et absence a APPROUVE si celles-ci etaient
		// en VALIDE ou JOURNALISE
		// pour qu elles soient reprises en compte dans la ventilation
		if (isPointageAbsenceModifie || isPointageHSupModifie) {

			originalAgentPointages = pointageService.getLatestPointagesForSaisieForAgentAndDateMonday(idAgent,
					dateLundi);

			Date currentDateEtat = getCurrentDateEtat(idAgent, dateLundi);

			for (JourPointageDto jourDto : fichePointageDto.getSaisies()) {
				for (AbsenceDto abs : jourDto.getAbsences()) {

					if (abs.isaSupprimer())
						continue;

					// Try to retrieve in the existing original pointages if it
					// exists
					Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointages, abs);

					if (null != ptg
							&& !isPointageDejaModifie(finalPointages, ptg)
							&& (EtatPointageEnum.VALIDE.equals(ptg.getLatestEtatPointage().getEtat()) || EtatPointageEnum.JOURNALISE
									.equals(ptg.getLatestEtatPointage().getEtat()))) {
						// Only if it has changed, process this pointage
						ptg = pointageService.getOrCreateNewPointage(idAgentOperator, abs.getIdPointage(), idAgent,
								dateLundi, helperService.getCurrentDate());
						if (null != abs.getIdRefTypeAbsence()) {
							ptg.setRefTypeAbsence(pointageRepository.getEntity(RefTypeAbsence.class,
									abs.getIdRefTypeAbsence()));
						}
						ptg.setType(pointageRepository.getEntity(RefTypePointage.class,
								RefTypePointageEnum.ABSENCE.getValue()));

						crudComments(ptg, abs.getMotif(), abs.getCommentaire());

						if (!approveModifiedPointages)
							pointageService.addEtatPointage(ptg, EtatPointageEnum.APPROUVE, idAgentOperator,
									currentDateEtat);

						finalPointages.add(ptg);
					}
				}

				for (HeureSupDto hs : jourDto.getHeuresSup()) {

					if (hs.isaSupprimer())
						continue;

					// Try to retrieve in the existing original pointages if it
					// exists
					Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointages, hs);

					if (null != ptg
							&& !isPointageDejaModifie(finalPointages, ptg)
							&& (EtatPointageEnum.VALIDE.equals(ptg.getLatestEtatPointage().getEtat()) || EtatPointageEnum.JOURNALISE
									.equals(ptg.getLatestEtatPointage().getEtat()))) {
						// Only if it has changed, process this pointage
						ptg = pointageService.getOrCreateNewPointage(idAgentOperator, hs.getIdPointage(), idAgent,
								dateLundi, helperService.getCurrentDate());
						ptg.setType(pointageRepository.getEntity(RefTypePointage.class,
								RefTypePointageEnum.H_SUP.getValue()));

						crudComments(ptg, hs.getMotif(), hs.getCommentaire());

						if (!approveModifiedPointages)
							pointageService.addEtatPointage(ptg, EtatPointageEnum.APPROUVE, idAgent, currentDateEtat);

						finalPointages.add(ptg);
					}
				}
			}
		}

		// If called with the approvedModifidPointages parameter, we need to
		// mark all the modified pointages directly as APPROUVE
		if (approveModifiedPointages) {
			markPointagesAsApproved(finalPointages, dateLundi, idAgent, idAgentOperator);
		}

		savePointages(finalPointages);
		deletePointages(idAgentOperator, originalAgentPointages);

		return result;
	}

	protected boolean isPointageDejaModifie(List<Pointage> finalPointages, Pointage ptg) {
		boolean isPointageDejaModifie = false;
		for (Pointage p : finalPointages) {
			if ((null != p.getIdPointage() && p.getIdPointage().equals(ptg.getIdPointage()))
					|| (null != p.getPointageParent() && null != p.getPointageParent().getIdPointage() && p
							.getPointageParent().getIdPointage().equals(ptg.getIdPointage()))) {
				isPointageDejaModifie = true;
				break;
			}
		}
		return isPointageDejaModifie;
	}

	protected Date getCurrentDateEtat(Integer idAgent, Date dateLundi) {
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent),
				dateLundi);
		VentilDate currentVentilation = ventilationRepository.getLatestVentilDate(
				helperService.getTypeChainePaieFromStatut(carr.getStatutCarriere()), false);
		return currentVentilation == null ? helperService.getCurrentDate() : currentVentilation.getDateVentilation();
	}

	protected void markPointagesAsApproved(List<Pointage> pointages, Date dateLundi, Integer idAgent,
			Integer idAgentOperator) {

		Date currentDateEtat = getCurrentDateEtat(idAgent, dateLundi);

		for (Pointage ptg : pointages) {

			// If the pointage was already APPROUVE (it hasnt been modified),
			// dont change its Etat
			if (ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.APPROUVE)
				continue;

			// Otherwise, since we're from SIRH, we need to mark it as APPROUVE
			// and set its DateEtat to the VentilationDate if a current one
			// exists
			EtatPointage ep = new EtatPointage();
			ep.setDateEtat(currentDateEtat);
			ep.setDateMaj(helperService.getCurrentDate());
			ep.setEtat(EtatPointageEnum.APPROUVE);
			ep.setIdAgent(idAgentOperator);
			ep.setPointage(ptg);
			ptg.getEtats().add(ep);
		}

	}

	private void savePointages(List<Pointage> finalPointages) {
		for (Pointage ptg : finalPointages)
			pointageRepository.savePointage(ptg);
	}

	/**
	 * Given a list of Pointage, delete each of them when in SAISI
	 * 
	 * @param pointagesToDelete
	 */
	protected void deletePointages(Integer idAgentOperator, List<Pointage> pointagesToDelete) {
		// Delete anything that was not updated from the saving process
		for (Pointage pointageToDelete : pointagesToDelete) {

			// If the Pointage was SAISI, simply remove it
			if (pointageToDelete.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI) {
				pointageRepository.removeEntity(pointageToDelete);
				continue;
			}
		}
	}

	/**
	 * Responsible for creating / updating / deleting comments based on what a
	 * user entered
	 * 
	 * @param ptg
	 * @param motif
	 * @param commentaire
	 */
	protected void crudComments(Pointage ptg, String motif, String commentaire) {

		PtgComment motifPtgComment = ptg.getMotif();
		if (motif != null && !motif.equals("")) {
			if (motifPtgComment != null)
				motifPtgComment.setText(motif);
			else
				ptg.setMotif(new PtgComment(motif));
		} else if (motifPtgComment != null) {
			pointageRepository.removeEntity(motifPtgComment);
			ptg.setMotif(null);
		}

		PtgComment commentPtgComment = ptg.getCommentaire();
		if (commentaire != null && !commentaire.equals("")) {
			if (commentPtgComment != null)
				commentPtgComment.setText(commentaire);
			else
				ptg.setCommentaire(new PtgComment(commentaire));
		} else if (commentPtgComment != null) {
			pointageRepository.removeEntity(commentPtgComment);
			ptg.setCommentaire(null);
		}
	}

	protected Pointage findPointageAndRemoveFromOriginals(List<Pointage> originalAgentPointages, PointageDto dto) {

		Pointage ptg = null;

		for (Pointage p : originalAgentPointages) {
			if (p.getIdPointage().equals(dto.getIdPointage())) {
				ptg = p;
				break;
			}
		}

		if (ptg != null)
			originalAgentPointages.remove(ptg);

		return ptg;
	}

	protected boolean hasPointageChanged(Pointage ptg, PrimeDto prime) {

		if (ptg.getQuantite() != null)
			return (!ptg.getQuantite().equals(prime.getQuantite()) || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
					&& hasTextChanged(ptg, prime));

		boolean dateDebutHasChanged = !((ptg.getDateDebut() == null && prime.getHeureDebut() == null) || (ptg
				.getDateDebut() != null && prime.getHeureDebut() != null && ptg.getDateDebut().getTime() == prime
				.getHeureDebut().getTime()));
		boolean dateFinHasChanged = !((ptg.getDateFin() == null && prime.getHeureFin() == null) || (ptg.getDateFin() != null
				&& prime.getHeureFin() != null && ptg.getDateFin().getTime() == prime.getHeureFin().getTime()));

		return (dateDebutHasChanged || dateFinHasChanged || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
				&& hasTextChanged(ptg, prime));
	}

	protected boolean hasPointageChanged(Pointage ptg, AbsenceDto absence) {

		boolean hasBeenModified = !((null == ptg.getRefTypeAbsence() || ptg.getRefTypeAbsence().getIdRefTypeAbsence()
				.equals(absence.getIdRefTypeAbsence()))
				&& ptg.getDateDebut().getTime() == absence.getHeureDebut().getTime() && ptg.getDateFin().getTime() == absence
				.getHeureFin().getTime());

		return (hasBeenModified || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
				&& hasTextChanged(ptg, absence));
	}

	protected boolean hasPointageChanged(Pointage ptg, HeureSupDto hSup) {

		boolean hasBeenModified = !(ptg.getHeureSupRecuperee().equals(hSup.getRecuperee())
				&& ptg.getHeureSupRappelService().equals(hSup.getRappelService())
				&& ptg.getDateDebut().getTime() == hSup.getHeureDebut().getTime() && ptg.getDateFin().getTime() == hSup
				.getHeureFin().getTime());

		return (hasBeenModified || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
				&& hasTextChanged(ptg, hSup));
	}

	protected boolean hasTextChanged(Pointage ptg, PointageDto pointageDto) {

		boolean motifHasChanged = (ptg.getMotif() == null && pointageDto.getMotif() != null
				&& !pointageDto.getMotif().equals("") || (ptg.getMotif() != null && !ptg.getMotif().getText()
				.equals(pointageDto.getMotif())));

		boolean commentHasChanged = (ptg.getCommentaire() == null && pointageDto.getCommentaire() != null
				&& !pointageDto.getCommentaire().equals("") || (ptg.getCommentaire() != null && !ptg.getCommentaire()
				.getText().equals(pointageDto.getCommentaire())));

		return motifHasChanged || commentHasChanged;
	}
}
