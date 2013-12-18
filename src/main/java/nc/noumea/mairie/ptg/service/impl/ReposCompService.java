package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.ReposCompHisto;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IReposCompRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IReposCompService;
import nc.noumea.mairie.ws.IAbsWsConsumer;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReposCompService implements IReposCompService {

	private Logger logger = LoggerFactory.getLogger(ReposCompService.class);

	@Autowired
	private IReposCompRepository reposCompRepository;

	@Autowired
	private IVentilationRepository ventilationRepository;

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private HelperService helperService;

	@Autowired
	private ISirhRepository sirhRepository;

	@Autowired
	private IAbsWsConsumer absWsConsumer;

	private static int MAX_MIN_PER_WEEK = 42 * 60; // 42h
	private static int REPOS_COMP_COEF_THRESHOLD = 130 * 60; // 130h

	@Override
	public void processReposCompTask(Integer idReposCompTask) {

		logger.info("Processing ReposCompTaskid {}...", idReposCompTask);

		ReposCompTask task = reposCompRepository
				.getReposCompTask(idReposCompTask);

		if (task == null) {
			logger.error("This Task cannot be found. Exiting process.");
			return;
		}

		logger.info("Agent {}.", task.getIdAgent());
		
		List<VentilHsup> hSs = ventilationRepository
				.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(task
						.getIdAgent(), task.getVentilDate().getIdVentilDate());

		if (hSs.size() == 0) {
			logger.info("Agent {} does not have any HSUPS over ventilation period. Exiting process.", task.getIdAgent());
			return;
		}
		
		Integer totalMinutesOfYear = reposCompRepository
				.countTotalHSupsSinceStartOfYear(task.getIdAgent(),
						new DateTime(helperService.getCurrentDate()).getYear());

		for (VentilHsup vhs : hSs) {
			
			logger.info("Processing week {}", vhs.getDateLundi()); 
			
			Spcarr carr = sirhRepository.getAgentCurrentCarriere(
					helperService.getMairieMatrFromIdAgent(task.getIdAgent()),
					vhs.getDateLundi());
			Spbase base = carr.getSpbase();
			Spbhor spbhor = carr.getSpbhor();
			int weekBase = (int) (helperService
					.convertMairieNbHeuresFormatToMinutes(base.getNbashh()) * spbhor
					.getTaux());

			ReposCompHisto histo = getOrCreateReposCompHisto(task.getIdAgent(), vhs.getDateLundi(), weekBase, vhs.getMSup());

			// Adding the nb of HSups to the counter in order to not have to
			// query again the db for total amount of Hsups over the year
			totalMinutesOfYear += histo.getMSup();
			logger.info("Hsups: {} minutes. Total HSups count for year is {} minutes: {} hours.", 
					histo.getMSup(), totalMinutesOfYear, totalMinutesOfYear/60);
			
			int nbMinutesToCount = (weekBase + histo.getMSup()) - MAX_MIN_PER_WEEK;

			if (nbMinutesToCount <= 0) {
				logger.info("Agent has not done more than 42H this week, no RC to add.");
				continue;
			}
			
			logger.info("Agent has done {} minutes more than 42H this week.", nbMinutesToCount);

			int coef = totalMinutesOfYear > REPOS_COMP_COEF_THRESHOLD ? 20	: 30;
			int nbRecups = (nbMinutesToCount / 60) * coef;

			logger.info("Agent is accountable for {} minutes.", nbRecups);
			
			logger.info("Calling SIRH-ABS-WS to add {} minutes...", nbRecups);
			absWsConsumer.addReposCompToAgent(task.getIdAgent(), histo.getDateLundi(), nbRecups);
		}

		logger.info("Done processing ReposCompTask.");
	}
	
	protected ReposCompHisto getOrCreateReposCompHisto(Integer idAgent, Date dateLundi, Integer weekBase, Integer mSups) {

		ReposCompHisto histo = reposCompRepository.findReposCompHistoForAgentAndDate(idAgent, dateLundi);
		
		if (histo == null) {
			histo = new ReposCompHisto();
			histo.setIdAgent(idAgent);
			histo.setDateLundi(dateLundi);
			histo.setMBaseHoraire(weekBase);
		}
		
		histo.setMSup(mSups);
		
		if (histo.getIdRcHisto() == null)
			pointageRepository.persisEntity(histo);
		
		return histo;
	}
}
