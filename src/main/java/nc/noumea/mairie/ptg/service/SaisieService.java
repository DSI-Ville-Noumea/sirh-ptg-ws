package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaisieService implements ISaisieService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IPointageService pointageService;
	
	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IPointageDataConsistencyRules ptgDataCosistencyRules;
	
	@Override
	public void saveFichePointage(FichePointageDto fichePointageDto) {

		Date dateLundi = fichePointageDto.getDateLundi();
		
		if (!helperService.isDateAMonday(dateLundi))
			throw new NotAMondayException();
		
		Integer idAgent = fichePointageDto.getAgent().getIdAgent();
		
		List<Pointage> originalAgentPointages = pointageRepository.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi);
		
		List<Pointage> finalPointages = new ArrayList<Pointage>();
		
		for (JourPointageDto jourDto : fichePointageDto.getSaisies()) {
			
			for (AbsenceDto abs : jourDto.getAbsences()) {
				
				Pointage ptg = pointageService.getOrCreateNewPointage(abs.getIdPointage(), idAgent, dateLundi);
				originalAgentPointages.remove(ptg);
				
				ptg.setAbsenceConcertee(abs.getConcertee());
				ptg.setDateDebut(abs.getHeureDebut());
				ptg.setDateFin(abs.getHeureFin());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.ABSENCE.getValue()));
				
				crudComments(ptg, abs.getMotif(), abs.getCommentaire());
				
				finalPointages.add(ptg);
			}
			
			for (HeureSupDto hs : jourDto.getHeuresSup()) {
				
				Pointage ptg = pointageService.getOrCreateNewPointage(hs.getIdPointage(), idAgent, dateLundi);
				originalAgentPointages.remove(ptg);
				
				ptg.setHeureSupPayee(hs.getPayee());
				ptg.setDateDebut(hs.getHeureDebut());
				ptg.setDateFin(hs.getHeureFin());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue()));

				crudComments(ptg, hs.getMotif(), hs.getCommentaire());

				finalPointages.add(ptg);
			}

			for (PrimeDto prime : jourDto.getPrimes()) {
				
				// if the new pointage has null qte, null datedebut and datefin, leave it (it is a template)
				if (prime.getHeureDebut() == null 
					&& prime.getHeureFin() == null 
					&& (prime.getQuantite() == null || prime.getQuantite().equals(0))) {
					
					continue;
				}
				
				Pointage ptg = pointageService.getOrCreateNewPointage(prime.getIdPointage(), idAgent, dateLundi, prime.getIdRefPrime());
				originalAgentPointages.remove(ptg);

				ptg.setDateDebut(prime.getHeureDebut());
				ptg.setDateFin(prime.getHeureFin());
				ptg.setQuantite(prime.getQuantite());
				ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue()));
				
				crudComments(ptg, prime.getMotif(), prime.getCommentaire());

				finalPointages.add(ptg);
			}
			
		}

		// calling data consistency
		List<String> errors = new ArrayList<String>();
		ptgDataCosistencyRules.checkSprircRecuperation(errors, idAgent, dateLundi, finalPointages);
		ptgDataCosistencyRules.checkSpcongConge(errors, idAgent, dateLundi, finalPointages);
		
		if (errors.size() != 0)
			return;
		
		savePointages(finalPointages);
		deletePointages(originalAgentPointages);
	}
	
	private void savePointages(List<Pointage> finalPointages) {
		for (Pointage ptg : finalPointages)
			pointageRepository.savePointage(ptg);
	}

	/**
	 * Given a list of Pointage, delete each of them when in SAISI
	 * @param pointagesToDelete
	 */
	protected void deletePointages(List<Pointage> pointagesToDelete) {
		// Delete anything that was not updated from the saving process
		for (Pointage pointageToDelete : pointagesToDelete) {
			
			// If the Pointage was SAISI, simply remove it
			if (pointageToDelete.getLatestEtatPointage().getEtat() == EtatPointageEnum.SAISI)
			{
				pointageToDelete.remove();
				continue;
			}
			
			// Otherwise, create a new record of the Pointage with values set to 0 (in order to keep track of deletion)
			Pointage pbis = pointageService.getOrCreateNewPointage(pointageToDelete);
			pointageRepository.savePointage(pbis);
		}
	}
	
	/**
	 * Responsible for creating / updating / deleting comments based on what a user entered
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
			motifPtgComment.remove();
			ptg.setMotif(null);
		}

		PtgComment commentPtgComment = ptg.getCommentaire();
		if (commentaire != null && !commentaire.equals("")) {
			if (commentPtgComment != null)
				commentPtgComment.setText(commentaire);
			else
				ptg.setCommentaire(new PtgComment(commentaire));
		} else if (commentPtgComment != null) {
			commentPtgComment.remove();
			ptg.setCommentaire(null);
		}
	}
	
}
