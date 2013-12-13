package nc.noumea.mairie.ptg.service.impl;

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

			//TODO Search if exists record for date
			// if already existing, get old value, update it
			// if new, store new record
			ReposCompHisto histo = new ReposCompHisto();
			histo.setIdAgent(task.getIdAgent());
			histo.setDateLundi(vhs.getDateLundi());
			histo.setMBaseHoraire(weekBase);
			histo.setMSup(vhs.getMSup());
			pointageRepository.persisEntity(histo);

			// Adding the nb of HSups to the counter in order to not have to
			// query again the db for total amount of Hsups over the year
			totalMinutesOfYear += vhs.getMSup();
			logger.info("Hsups: {} minutes. Total HSups count for year is {} minutes: {} hours.", 
					vhs.getMSup(), totalMinutesOfYear, totalMinutesOfYear/60);
			
			int nbMinutesToCount = (weekBase + vhs.getMSup()) - MAX_MIN_PER_WEEK;

			if (nbMinutesToCount <= 0) {
				logger.info("Agent has not done more than 42H this week, no RC to add.");
				continue;
			}
			
			logger.info("Agent has done {} minutes more than 42H this week.", nbMinutesToCount);

			int coef = totalMinutesOfYear > (130 * 60) ? 20	: 30;
			int nbRecups = (nbMinutesToCount % 60) * coef;

			// call sirh-abs-ws to persist nbRecups
			logger.info("Calling SIRH-ABS-WS to add {} minutes...", nbRecups);
			absWsConsumer.addReposCompToAgent(task.getIdAgent(), vhs.getDateLundi(), nbRecups);
		}

		logger.info("Done processing ReposCompTask.");
	}
}
