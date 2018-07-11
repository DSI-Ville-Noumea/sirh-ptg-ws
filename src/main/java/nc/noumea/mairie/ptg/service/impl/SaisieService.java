package nc.noumea.mairie.ptg.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AbsenceDtoKiosque;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.HeureSupDtoKiosque;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.PointageDto;
import nc.noumea.mairie.ptg.dto.PointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.PrimeDtoKiosque;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IDpmService;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.ISaisieService;
import nc.noumea.mairie.ptg.service.NotAMondayException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
	private IDpmService dpmService;

	@Autowired
	private IAbsWsConsumer absWsConsumer;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IPointageDataConsistencyRules ptgDataCosistencyRules;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	@Qualifier("sirhAbsDateBlocagePointage")
	private String sirhAbsDateBlocagePointage;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm");
	
	private static String REGIMES_INDEMNITAIRES_DATE_FUTURE = "Les régimes indemnitaires ne peuvent être saisis dans le futur.";

	@Override
	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDtoKiosque fichePointageDto)
			throws ParseException {
		return saveFichePointageKiosque(idAgentOperator, fichePointageDto, false);
	}

	@Override
	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDto fichePointageDto,
			boolean approveModifiedPointages) throws ParseException {

		ReturnMessageDto result = new ReturnMessageDto();
		Date dateLundi = fichePointageDto.getDateLundi();

		// cf #15027 on bloque la saisie des pointages avant la date de MEP
		if (dateLundi.before(new SimpleDateFormat("dd/MM/yyyy").parse(sirhAbsDateBlocagePointage))) {
			result.getErrors().add(
					"Les saisies antérieur au " + sirhAbsDateBlocagePointage
							+ " sont à effectuer dans l'ancien système (AS400).");
			return result;
		}

		if (!helperService.isDateAMonday(dateLundi))
			throw new NotAMondayException();

		if (!approveModifiedPointages) {
			// #36156 : on autorise le changement pour la prime 7714
			result = ptgDataCosistencyRules.checkDateLundiAnterieurA3MoisWithPointage(result, dateLundi, null);
			// #15410 on bloque la saisie des pointages dans le futur
			result = ptgDataCosistencyRules.checkDateLundiNotSuperieurDateJour(result, dateLundi);
		}
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
				// cas de la DMP #11622
				EntiteDto service = sirhWsConsumer.getAgentDirection(idAgent, ptg.getDateDebut());
				if (null != service && service.getSigle().toUpperCase().equals("DPM")) {
					ptg.setHeureSupRecuperee(true);
					ptg.setHeureSupRappelService(hs.getRappelService());
				} else {
					ptg.setHeureSupRecuperee(hs.getRecuperee());
					ptg.setHeureSupRappelService(false);
				}
				ptg.setDateDebut(hs.getHeureDebut());
				ptg.setDateFin(hs.getHeureFin());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue()));

				crudCommentsHeureSup(ptg, hs.getIdMotifHsup(), hs.getCommentaire());

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
				
				// #47288 : Les régimes indemnitaires ne peuvent pas être saisis dans le futur.
				if (prime.getNumRubrique().equals(VentilationPrimeService.INDEMNITE_TRAVAIL_NUIT) ||
						prime.getNumRubrique().equals(VentilationPrimeService.INDEMNITE_TRAVAIL_DJF)) {
					ptgDataCosistencyRules.checkDateNotSuperieurDateJour(result, prime.getHeureDebut(), REGIMES_INDEMNITAIRES_DATE_FUTURE);
				}
				if (!result.getErrors().isEmpty())
					continue;
					
				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointages, prime);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChanged(ptg, prime)) {
					String info = "Le pointage de l'agent matricule " + ptg.getIdAgent();
					
					if (ptg.getDateDebut() != null)
						info += " du "+ sdf.format(ptg.getDateDebut());
					if (ptg.getDateFin() != null)
						info += " au " + sdf.format(ptg.getDateFin());
					info += " est inchangé.";
					
					result.getInfos().add(info);
					
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
		ptgDataCosistencyRules.processDataConsistency(result, idAgent, dateLundi, finalPointages, true);

		// If any blocking error, return the list of problems and do not save
		if (result.getErrors().size() != 0)
			return result;

		// si une heure sup ou absence est modifiee,
		// on repasse toutes les hsup et absence a APPROUVE si celles-ci etaient
		// en VALIDE ou JOURNALISE
		// pour qu elles soient reprises en compte dans la ventilation
		if (isPointageAbsenceModifie || isPointageHSupModifie) {

			List<Pointage> originalAgentPointagesTemp = pointageService.getLatestPointagesForSaisieForAgentAndDateMonday(idAgent,
					dateLundi);

			Date currentDateEtat = getCurrentDateEtat(idAgent, dateLundi);

			for (JourPointageDto jourDto : fichePointageDto.getSaisies()) {
				for (AbsenceDto abs : jourDto.getAbsences()) {

					if (abs.isaSupprimer())
						continue;

					// Try to retrieve in the existing original pointages if it
					// exists
					Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointagesTemp, abs);

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
					Pointage ptg = findPointageAndRemoveFromOriginals(originalAgentPointagesTemp, hs);

					if (null != ptg
							&& !isPointageDejaModifie(finalPointages, ptg)
							&& (EtatPointageEnum.VALIDE.equals(ptg.getLatestEtatPointage().getEtat()) || EtatPointageEnum.JOURNALISE
									.equals(ptg.getLatestEtatPointage().getEtat()))) {
						// Only if it has changed, process this pointage
						ptg = pointageService.getOrCreateNewPointage(idAgentOperator, hs.getIdPointage(), idAgent,
								dateLundi, helperService.getCurrentDate());
						ptg.setType(pointageRepository.getEntity(RefTypePointage.class,
								RefTypePointageEnum.H_SUP.getValue()));

						crudCommentsHeureSup(ptg, hs.getIdMotifHsup(), hs.getCommentaire());

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
		deletePointages(result, idAgentOperator, originalAgentPointages);
		
		majCompteurRecuperationProvisoire(finalPointages);

		return result;
	}

	/**
	 * Responsible for creating / updating / deleting comments based on what a
	 * user entered
	 * 
	 * @param ptg
	 * @param idMotifHsup
	 * @param commentaire
	 */
	protected void crudCommentsHeureSup(Pointage ptg, Integer idMotifHsup, String commentaire) {
		MotifHeureSup motifHSup = pointageRepository.getEntity(MotifHeureSup.class, idMotifHsup);
		ptg.setMotifHsup(motifHSup);

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
		// #41417 : Si la carrière de l'agent ne commence pas un lundi, il ne faut pas prendre la date du lundi...
		if (carr == null) {
			carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), new DateTime(dateLundi).plusWeeks(1).toDate());
		}
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
	protected void deletePointages(ReturnMessageDto result, Integer idAgentOperator, List<Pointage> pointagesToDelete) {
		// Delete anything that was not updated from the saving process
		for (Pointage pointageToDelete : pointagesToDelete) {
			// #18234
			if(null == pointageToDelete.getRefPrime()
					|| pointageToDelete.getRefPrime().isAffichageKiosque()) {
				// If the Pointage was SAISI, simply remove it
				if (pointageToDelete.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI) {
					pointageRepository.removeEntity(pointageToDelete);
					continue;
				// sinon message erreur (#15502)
				} else {
					result.getErrors().add("Vous ne pouvez pas supprimer un pointage s'il n'est pas à l'état Saisi.");
				}
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

		// #44443 : Si les 2 quantités sont à 0, mais que les heures changent, on ne s'en rend pas compte.
		if (ptg.getQuantite() != null && ptg.getQuantite() != 0)
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
				&& hasTextChangedHSup(ptg, hSup));
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

	protected boolean hasTextChangedHSup(Pointage ptg, HeureSupDto hsupDto) {

		boolean motifHasChanged = (ptg.getMotifHsup() == null && hsupDto.getIdMotifHsup() != null || (ptg
				.getMotifHsup() != null && ptg.getMotifHsup().getIdMotifHsup() != hsupDto.getIdMotifHsup()));

		boolean commentHasChanged = (ptg.getCommentaire() == null && hsupDto.getCommentaire() != null
				&& !hsupDto.getCommentaire().equals("") || (ptg.getCommentaire() != null && !ptg.getCommentaire()
				.getText().equals(hsupDto.getCommentaire())));

		return motifHasChanged || commentHasChanged;
	}

	@Override
	public ReturnMessageDto saveFichePointageKiosque(Integer idAgentOperator, FichePointageDtoKiosque fichePointageDto,
			boolean approveModifiedPointages) throws ParseException {

		Date dateLundi = fichePointageDto.getDateLundi();
		ReturnMessageDto result = new ReturnMessageDto();

		// cf #15027 on bloque la saisie des pointages avant la date de MEP
		if (dateLundi.before(new SimpleDateFormat("dd/MM/yyyy").parse(sirhAbsDateBlocagePointage))) {
			result.getErrors().add(
					"Saisie impossible pour cette date, merci de contacter la DRH pour effectuer cette saisie.");
			return result;
		}

		if (!helperService.isDateAMonday(dateLundi))
			throw new NotAMondayException();

		if (!approveModifiedPointages) {
			// #15410 on bloque la saisie des pointages dans le futur
			result = ptgDataCosistencyRules.checkDateLundiNotSuperieurDateJour(result, dateLundi);
		}
		if (!result.getErrors().isEmpty())
			return result;

		Integer idAgent = fichePointageDto.getAgent().getIdAgent();

		List<Pointage> originalAgentPointages = pointageService.getLatestPointagesForSaisieForAgentAndDateMonday(
				idAgent, dateLundi);

		List<Pointage> finalPointages = new ArrayList<Pointage>();
		boolean isPointageAbsenceModifie = false;
		boolean isPointageHSupModifie = false;
		boolean isPointagePrimeModifie = false;

		for (JourPointageDtoKiosque jourDto : fichePointageDto.getSaisies()) {

			for (AbsenceDtoKiosque abs : jourDto.getAbsences()) {

				if (abs.isaSupprimer())
					continue;

				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginalsKiosque(originalAgentPointages, abs);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChangedKiosque(ptg, abs)) {
					continue;
				}

				// Only if it has changed, process this pointage
				ptg = pointageService.getOrCreateNewPointage(idAgentOperator, abs.getIdPointage(), idAgent, dateLundi,
						helperService.getCurrentDate());
				if (null != abs.getIdRefTypeAbsence()) {
					ptg.setRefTypeAbsence(pointageRepository.getEntity(RefTypeAbsence.class, abs.getIdRefTypeAbsence()));
				}
				ptg.setDateDebut(abs.getHeureDebutDate());
				ptg.setDateFin(abs.getHeureFinDate());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.ABSENCE.getValue()));

				crudComments(ptg, abs.getMotif(), abs.getCommentaire());

				isPointageAbsenceModifie = true;
				finalPointages.add(ptg);
				// #36156 : on autorise le changement pour la prime 7714
				result = ptgDataCosistencyRules.checkDateLundiAnterieurA3MoisWithPointage(result, ptg.getDateLundi(), ptg);
			}

			for (HeureSupDtoKiosque hs : jourDto.getHeuresSup()) {

				if (hs.isaSupprimer())
					continue;

				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginalsKiosque(originalAgentPointages, hs);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChangedKiosque(ptg, hs)) {
					continue;
				}

				// Only if it has changed, process this pointage
				ptg = pointageService.getOrCreateNewPointage(idAgentOperator, hs.getIdPointage(), idAgent, dateLundi,
						helperService.getCurrentDate());
				// cas de la DMP #11622
				EntiteDto service = sirhWsConsumer.getAgentDirection(idAgent, ptg.getDateDebut());
				if (null != service && service.getSigle().toUpperCase().equals("DPM")) {
					ptg.setHeureSupRecuperee(true);
					ptg.setHeureSupRappelService(hs.getRappelService());
				} else {
					ptg.setHeureSupRecuperee(hs.getRecuperee());
					ptg.setHeureSupRappelService(false);
				}
				ptg.setDateDebut(hs.getHeureDebutDate());
				ptg.setDateFin(hs.getHeureFinDate());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue()));

				crudCommentsHeureSup(ptg, hs.getIdMotifHsup(), hs.getCommentaire());

				isPointageHSupModifie = true;
				finalPointages.add(ptg);
				// #36156 : on autorise le changement pour la prime 7714
				result = ptgDataCosistencyRules.checkDateLundiAnterieurA3MoisWithPointage(result, ptg.getDateLundi(), ptg);
			}

			for (PrimeDtoKiosque prime : jourDto.getPrimes()) {

				// if the new pointage has no idPointage, null qte, null
				// datedebut and datefin, leave it (it is a template)
				if (prime.isaSupprimer() || prime.getIdPointage() == null && prime.getHeureFin() == null
						&& (prime.getQuantite() == null || prime.getQuantite().equals(0))) {
					continue;
				}
				
				// #47288 : Les régimes indemnitaires ne peuvent pas être saisis dans le futur.
				if (prime.getNumRubrique() != null && 
						(prime.getNumRubrique().equals(VentilationPrimeService.INDEMNITE_TRAVAIL_NUIT) ||
						prime.getNumRubrique().equals(VentilationPrimeService.INDEMNITE_TRAVAIL_DJF))) {
					ptgDataCosistencyRules.checkDateNotSuperieurDateJour(result, prime.getHeureDebutDate(), REGIMES_INDEMNITAIRES_DATE_FUTURE);
				}
				if (!result.getErrors().isEmpty())
					continue;

				// Try to retrieve in the existing original pointages if it
				// exists
				Pointage ptg = findPointageAndRemoveFromOriginalsKiosque(originalAgentPointages, prime);

				// If already existing, try and compare if it has changed
				// compared to the original version
				if (ptg != null && !hasPointageChangedKiosque(ptg, prime)) {
					continue;
				}

				// Only if it has changed, process this pointage
				ptg = pointageService.getOrCreateNewPointage(idAgentOperator, prime.getIdPointage(), idAgent,
						dateLundi, helperService.getCurrentDate(), prime.getIdRefPrime());
				ptg.setDateDebut(prime.getHeureDebutDate() == null ? jourDto.getDate() : prime.getHeureDebutDate());
				ptg.setDateFin(prime.getHeureFinDate());
				ptg.setQuantite(prime.getQuantite());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue()));

				crudComments(ptg, prime.getMotif(), prime.getCommentaire());

				isPointagePrimeModifie = true;
				finalPointages.add(ptg);

				// #36156 : on autorise le changement pour la prime 7714
				result = ptgDataCosistencyRules.checkDateLundiAnterieurA3MoisWithPointage(result, ptg.getDateLundi(), ptg);
			}
		}

		// calling data consistency
		ptgDataCosistencyRules.processDataConsistency(result, idAgent, dateLundi, finalPointages, false);

		// If any blocking error, return the list of problems and do not save
		if (result.getErrors().size() != 0)
			return result;

		// si une heure sup ou absence est modifiee,
		// on repasse toutes les hsup et absence a APPROUVE si celles-ci etaient
		// en VALIDE ou JOURNALISE
		// pour qu elles soient reprises en compte dans la ventilation
		if (isPointageAbsenceModifie || isPointageHSupModifie) {

			List<Pointage> originalAgentPointagesTemp = pointageService.getLatestPointagesForSaisieForAgentAndDateMonday(idAgent,
					dateLundi);

			Date currentDateEtat = getCurrentDateEtat(idAgent, dateLundi);

			for (JourPointageDtoKiosque jourDto : fichePointageDto.getSaisies()) {
				for (AbsenceDtoKiosque abs : jourDto.getAbsences()) {

					if (abs.isaSupprimer())
						continue;

					// Try to retrieve in the existing original pointages if it
					// exists
					Pointage ptg = findPointageAndRemoveFromOriginalsKiosque(originalAgentPointagesTemp, abs);

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

				for (HeureSupDtoKiosque hs : jourDto.getHeuresSup()) {

					if (hs.isaSupprimer())
						continue;

					// Try to retrieve in the existing original pointages if it
					// exists
					Pointage ptg = findPointageAndRemoveFromOriginalsKiosque(originalAgentPointagesTemp, hs);

					if (null != ptg
							&& !isPointageDejaModifie(finalPointages, ptg)
							&& (EtatPointageEnum.VALIDE.equals(ptg.getLatestEtatPointage().getEtat()) || EtatPointageEnum.JOURNALISE
									.equals(ptg.getLatestEtatPointage().getEtat()))) {
						// Only if it has changed, process this pointage
						ptg = pointageService.getOrCreateNewPointage(idAgentOperator, hs.getIdPointage(), idAgent,
								dateLundi, helperService.getCurrentDate());
						ptg.setType(pointageRepository.getEntity(RefTypePointage.class,
								RefTypePointageEnum.H_SUP.getValue()));

						crudCommentsHeureSup(ptg, hs.getIdMotifHsup(), hs.getCommentaire());

						if (!approveModifiedPointages)
							pointageService.addEtatPointage(ptg, EtatPointageEnum.APPROUVE, idAgent, currentDateEtat);

						finalPointages.add(ptg);
					}
				}
			}
		}
		
		// #15506
		// on n affiche le message qu une seule fois
		if (!isPointageAbsenceModifie && !isPointageHSupModifie && !isPointagePrimeModifie && originalAgentPointages.isEmpty()) {
			result.getInfos().add("Vous n'avez effectué aucune modification depuis le dernier enregistrement.");
		}

		// If called with the approvedModifidPointages parameter, we need to
		// mark all the modified pointages directly as APPROUVE
		if (approveModifiedPointages) {
			markPointagesAsApproved(finalPointages, dateLundi, idAgent, idAgentOperator);
		}

		savePointages(finalPointages);
		deletePointages(result, idAgentOperator, originalAgentPointages);
		
		majCompteurRecuperationProvisoire(finalPointages);

		return result;
	}

	protected Pointage findPointageAndRemoveFromOriginalsKiosque(List<Pointage> originalAgentPointages,
			PointageDtoKiosque dto) {

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

	protected boolean hasPointageChangedKiosque(Pointage ptg, HeureSupDtoKiosque hSup) {

		boolean hasBeenModified = !(ptg.getHeureSupRecuperee().equals(hSup.getRecuperee())
				&& ptg.getHeureSupRappelService().equals(hSup.getRappelService())
				&& ptg.getDateDebut().getTime() == hSup.getHeureDebutDate().getTime() && ptg.getDateFin().getTime() == hSup
				.getHeureFinDate().getTime());

		return (hasBeenModified || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
				&& hasTextChangedHSupKiosque(ptg, hSup));
	}

	protected boolean hasPointageChangedKiosque(Pointage ptg, AbsenceDtoKiosque absence) {

		boolean hasBeenModified = !((null == ptg.getRefTypeAbsence() || ptg.getRefTypeAbsence().getIdRefTypeAbsence()
				.equals(absence.getIdRefTypeAbsence()))
				&& ptg.getDateDebut().getTime() == absence.getHeureDebutDate().getTime() && ptg.getDateFin().getTime() == absence
				.getHeureFinDate().getTime());

		return (hasBeenModified || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
				&& hasTextChangedKiosque(ptg, absence));
	}

	protected boolean hasPointageChangedKiosque(Pointage ptg, PrimeDtoKiosque prime) {

		if (ptg.getQuantite() != null)
			return (!ptg.getQuantite().equals(prime.getQuantite()) || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
					&& hasTextChangedKiosque(ptg, prime));

		boolean dateDebutHasChanged = !((ptg.getDateDebut() == null && prime.getHeureDebut() == null) || (ptg
				.getDateDebut() != null && prime.getHeureDebut() != null && ptg.getDateDebut().getTime() == prime
				.getHeureDebutDate().getTime()));
		boolean dateFinHasChanged = !((ptg.getDateFin() == null && prime.getHeureFin() == null) || (ptg.getDateFin() != null
				&& prime.getHeureFin() != null && ptg.getDateFin().getTime() == prime.getHeureFinDate().getTime()));

		return (dateDebutHasChanged || dateFinHasChanged || ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI
				&& hasTextChangedKiosque(ptg, prime));
	}

	protected boolean hasTextChangedKiosque(Pointage ptg, PointageDtoKiosque pointageDto) {

		boolean motifHasChanged = (ptg.getMotif() == null && pointageDto.getMotif() != null
				&& !pointageDto.getMotif().equals("") || (ptg.getMotif() != null && !ptg.getMotif().getText()
				.equals(pointageDto.getMotif())));

		boolean commentHasChanged = (ptg.getCommentaire() == null && pointageDto.getCommentaire() != null
				&& !pointageDto.getCommentaire().equals("") || (ptg.getCommentaire() != null && !ptg.getCommentaire()
				.getText().equals(pointageDto.getCommentaire())));

		return motifHasChanged || commentHasChanged;
	}

	protected boolean hasTextChangedHSupKiosque(Pointage ptg, HeureSupDtoKiosque hsupDto) {

		boolean motifHasChanged = (ptg.getMotifHsup() == null && hsupDto.getIdMotifHsup() != null || (ptg
				.getMotifHsup() != null && ptg.getMotifHsup().getIdMotifHsup() != hsupDto.getIdMotifHsup()));

		boolean commentHasChanged = (ptg.getCommentaire() == null && hsupDto.getCommentaire() != null
				&& !hsupDto.getCommentaire().equals("") || (ptg.getCommentaire() != null && !ptg.getCommentaire()
				.getText().equals(hsupDto.getCommentaire())));

		return motifHasChanged || commentHasChanged;
	}
	
	protected void majCompteurRecuperationProvisoire(List<Pointage> listPointage) {
		
		if(null != listPointage) {
			for(Pointage ptg : listPointage) {
				
				// si le pointage est une heure sup
				if(RefTypePointageEnum.H_SUP.equals(ptg.getTypePointageEnum())) {
					
					// dans le cas ou on a modifie un pointage avec un etat autre que SAISI
					// un second pointage se cree avec un pointage parent
					// et que ce pointage modifie (pointage parent) etait des HSup en recuperation
					if(null != ptg.getPointageParent()
							&& null != ptg.getPointageParent().getIdPointage()
							&& ptg.getPointageParent().getHeureSupRecuperee()) {
						
						absWsConsumer.addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getPointageParent().getDateDebut(), 0, 
								null != ptg.getPointageParent() ? ptg.getPointageParent().getIdPointage() : null, null);
					}
					// depuis SIRH, le nouveau pointage est directement mis a approuve
					if(ptg.getHeureSupRecuperee()
							&& null != ptg.getLatestEtatPointageWithPointageNotPersist()
							&& EtatPointageEnum.APPROUVE.equals(ptg.getLatestEtatPointageWithPointageNotPersist().getEtat())) {
						// calcul du nombre de minutes
						Integer nombreMinutes = helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin());
						
						// #30544 Indemnité forfaitaire travail DPM
						int nombreMinutesMajorees = dpmService.calculNombreMinutesRecupereesMajoreesToAgentForOnePointage(ptg);

						if (0 < nombreMinutesMajorees) {
							nombreMinutes += nombreMinutesMajorees;
						}
						
						absWsConsumer.addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), nombreMinutes, 
								ptg.getIdPointage() , null);
					}
					// depuis le kiosque
					// le nouveau pointage creditera le compteur lors de l approbation
				}
			}
		}
	}
}
