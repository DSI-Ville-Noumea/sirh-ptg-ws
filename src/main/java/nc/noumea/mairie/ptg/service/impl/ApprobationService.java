package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IApprobationService;
import nc.noumea.mairie.ptg.service.IDpmService;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

@Service
public class ApprobationService implements IApprobationService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IPointageService pointageService;

	@Autowired
	private IDpmService dpmService;

	@Autowired
	private IVentilationRepository ventilRepository;

	@Autowired
	private IAgentMatriculeConverterService matriculeConvertor;

	@Autowired
	private IVentilationRepository ventilationRepository;

	@Autowired
	private IPointageDataConsistencyRules ptgDataCosistencyRules;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private IAbsWsConsumer absWsConsumer;

	@Autowired
	private SirhWSUtils sirhWSUtils;

	@Override
	public List<ConsultPointageDto> getPointages(Integer idAgent, Date fromDate, Date toDate, Integer idServiceAds, Integer agent, Integer idRefEtat, Integer idRefType, String typeHS) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		// list of agents corresponding to filters
		List<Integer> agentIds = new ArrayList<Integer>();
		List<DroitsAgent> listDroitsAgentTemp = accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent);
		List<DroitsAgent> listDroitsAgent = new ArrayList<DroitsAgent>();
		if (idServiceAds != null) {
			// #18722 : pour chaque agent on va recuperer son
			// service
			List<Integer> listAgentDtoAppro = new ArrayList<Integer>();
			for (DroitsAgent da : listDroitsAgentTemp) {
				if (!listAgentDtoAppro.contains(da.getIdAgent()))
					listAgentDtoAppro.add(da.getIdAgent());
			}
			List<AgentWithServiceDto> listAgentsApproServiceDto = sirhWSConsumer.getListAgentsWithService(listAgentDtoAppro, new Date());

			for (DroitsAgent da : listDroitsAgentTemp) {
				AgentWithServiceDto agDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentsApproServiceDto, da.getIdAgent());
				if (agDtoServ != null && agDtoServ.getIdServiceADS() != null && agDtoServ.getIdServiceADS().toString().equals(idServiceAds.toString())) {
					listDroitsAgent.add(da);
				}
			}
		} else {
			listDroitsAgent.addAll(listDroitsAgentTemp);
		}
		for (DroitsAgent da : listDroitsAgent) {
			agentIds.add(da.getIdAgent());
		}

		// if the filter for one agent is set, check whether this agent is in
		// the list of the authorized ones
		if (agent != null) {
			// if not return empty result
			if (!agentIds.contains(agent))
				return result;
			// otherwise set it as the only agent in the filter
			else {
				agentIds.clear();
				agentIds.add(agent);
			}
		}

		return createConsultPointageDtoListFromSearch(agentIds.size() == 0 ? Arrays.asList(0) : agentIds, fromDate, toDate, idRefEtat, idRefType, typeHS, listDroitsAgent, idAgent, false, null);
	}

	@Override
	public List<ConsultPointageDto> getPointagesSIRH(Date fromDate, Date toDate, List<Integer> agentIds, Integer idRefEtat, Integer idRefType, String typeHS, Date dateEtat) {
		return createConsultPointageDtoListFromSearch(agentIds, fromDate, toDate, idRefEtat, idRefType, typeHS, null, null, true, dateEtat);
	}

	protected List<ConsultPointageDto> createConsultPointageDtoListFromSearch(List<Integer> agentIds, Date fromDate, Date toDate, Integer idRefEtat, Integer idRefType, String typeHS,
			List<DroitsAgent> listdroitsAgent, Integer idAgentOpeOrAppro, boolean isFromSirh, Date dateEtat) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		// get pointages with filters
		// We convert the 'toDate' date into the next day at 00H00 because the
		// query will take it as a timestamp.
		List<Pointage> pointages = pointageService.getLatestPointagesForAgentsAndDates(agentIds, fromDate, new LocalDate(toDate).plusDays(1).toDate(),
				RefTypePointageEnum.getRefTypePointageEnum(idRefType), idRefEtat == null ? null : Arrays.asList(EtatPointageEnum.getEtatPointageEnum(idRefEtat)), typeHS);

		List<Integer> listAgentDto = new ArrayList<Integer>();
		for (Pointage ptg : pointages) {
			if (!listAgentDto.contains(ptg.getIdAgent())) {
				listAgentDto.add(ptg.getIdAgent());
			}
			if (!listAgentDto.contains(ptg.getLatestEtatPointage().getIdAgent())) {
				listAgentDto.add(ptg.getLatestEtatPointage().getIdAgent());
			}
		}

		// dans un souci de performances, on n affichera toujours le service de
		// l agent a la date du jour
		// ce qui permet de ne faire qu un seul appel a SIRH-WS
		// et non plus un appel par demande (avec la date de la demande)
		List<AgentGeneriqueDto> listAgentsExistants = sirhWSConsumer.getListAgents(listAgentDto);

		for (Pointage ptg : pointages) {

			// #18234 : les TID ne doivent pas apparaitre côté kiosque
			if (isFromSirh || null == ptg.getRefPrime() || ptg.getRefPrime().isAffichageKiosque()) {
				// #19424 : on ajoute un filtre sur la date du dernier etat
				if (dateEtat != null) {
					Calendar dateFinEtat = Calendar.getInstance();
					dateFinEtat.setTime(dateEtat);
					dateFinEtat.set(Calendar.HOUR, 23);
					dateFinEtat.set(Calendar.MINUTE, 59);
					dateFinEtat.set(Calendar.SECOND, 59);
					dateFinEtat.set(Calendar.MILLISECOND, 0);

					Calendar dateDebEtat = Calendar.getInstance();
					dateDebEtat.setTime(dateEtat);
					dateDebEtat.set(Calendar.HOUR, 0);
					dateDebEtat.set(Calendar.MINUTE, 0);
					dateDebEtat.set(Calendar.SECOND, 0);
					dateDebEtat.set(Calendar.MILLISECOND, 0);

					if (ptg.getLatestEtatPointage().getDateEtat().after(dateFinEtat.getTime()) || ptg.getLatestEtatPointage().getDateEtat().before(dateDebEtat.getTime())) {
						continue;
					}
				}
				AgentDto agDto = new AgentDto(sirhWSUtils.getAgentOfListAgentGeneriqueDto(listAgentsExistants, ptg.getIdAgent()));
				AgentDto opeDto = new AgentDto(sirhWSUtils.getAgentOfListAgentGeneriqueDto(listAgentsExistants, ptg.getLatestEtatPointage().getIdAgent()));

				ConsultPointageDto dto = new ConsultPointageDto(ptg, helperService);

				dto.updateEtat(ptg.getLatestEtatPointage(), opeDto);
				dto.setAgent(agDto);

				// #14325
				dto.setApprobation(checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, idAgentOpeOrAppro));

				// #17613 : ne pas afficher les pouces vert/rouge en fonction
				// des
				// etats
				dto.setAffichageBoutonAccepter(dto.isApprobation()
						&& (ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.SAISI) || ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.REFUSE)));
				dto.setAffichageBoutonRefuser(dto.isApprobation()
						&& (ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.SAISI) || ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.APPROUVE)));
				dto.setAffichageBoutonRejeter(dto.isApprobation() && ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.JOURNALISE));

				result.add(dto);
			}
		}

		return result;
	}

	protected boolean checkDroitApprobationByPointage(Pointage ptg, ConsultPointageDto dto, List<DroitsAgent> listdroitsAgent, Integer idAgentOpeOrAppro) {
		// #14325 modifications sur le cumul des roles
		// on attribue les droits d approbation pour chaque demande
		if (dto.isApprobation()) {
			return true;
		}

		if (null != listdroitsAgent) {
			for (DroitsAgent da : listdroitsAgent) {
				if (da.getIdAgent().equals(ptg.getIdAgent())) {
					for (Droit droit : da.getDroits()) {
						if ((droit.getIdAgent().equals(idAgentOpeOrAppro) || (null != droit.getIdAgentDelegataire() && droit.getIdAgentDelegataire().equals(idAgentOpeOrAppro)))
								&& droit.isApprobateur()
								// #14841 on ne peut pas approuver un journalise
								// j ai ajoute les autres etats pour lesquels l
								// approbateur sera bloque
								// SIRH ne se fit pas a ce booleen pour afficher
								// les pouces
								// &&
								// !EtatPointageEnum.JOURNALISE.equals(ptg.getLatestEtatPointage().getEtat())
								&& !EtatPointageEnum.REFUSE_DEFINITIVEMENT.equals(ptg.getLatestEtatPointage().getEtat())
								&& !EtatPointageEnum.REJETE_DEFINITIVEMENT.equals(ptg.getLatestEtatPointage().getEtat()) && !EtatPointageEnum.EN_ATTENTE.equals(ptg.getLatestEtatPointage().getEtat())
								&& !EtatPointageEnum.VALIDE.equals(ptg.getLatestEtatPointage().getEtat()) && !EtatPointageEnum.VENTILE.equals(ptg.getLatestEtatPointage().getEtat())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<ConsultPointageDto> getPointagesArchives(Integer idAgent, Integer idPointage) {
		return getPointagesArchives(idPointage);
	}

	@Override
	public List<ConsultPointageDto> getPointagesArchives(Integer idPointage) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();
		List<Pointage> list = pointageRepository.getPointageArchives(idPointage);

		// optimisation performances
		Map<Integer, AgentDto> mapAgentDto = new HashMap<Integer, AgentDto>();

		for (Pointage ptg : list) {
			for (EtatPointage etat : ptg.getEtats()) {

				AgentDto agDto = null;
				if (mapAgentDto.containsKey(ptg.getIdAgent())) {
					agDto = mapAgentDto.get(ptg.getIdAgent());
				} else {
					agDto = new AgentDto(sirhWSConsumer.getAgent(ptg.getIdAgent()));
					mapAgentDto.put(ptg.getIdAgent(), agDto);
				}

				ConsultPointageDto dto = new ConsultPointageDto(ptg, helperService);

				AgentDto opeDto = null;
				if (mapAgentDto.containsKey(etat.getIdAgent())) {
					opeDto = mapAgentDto.get(etat.getIdAgent());
				} else {
					opeDto = new AgentDto(sirhWSConsumer.getAgent(etat.getIdAgent()));
					mapAgentDto.put(etat.getIdAgent(), opeDto);
				}

				dto.updateEtat(etat, opeDto);
				dto.setAgent(agDto);
				result.add(dto);
			}
		}
		return result;
	}

	@Override
	public ReturnMessageDto setPointagesEtat(Integer idAgent, List<PointagesEtatChangeDto> etatsDto) {

		ReturnMessageDto result = new ReturnMessageDto();

		List<DroitsAgent> droitsAgents = accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent);
		List<Integer> droitsAgentsIds = new ArrayList<Integer>();

		for (DroitsAgent da : droitsAgents)
			droitsAgentsIds.add(da.getIdAgent());

		for (PointagesEtatChangeDto dto : etatsDto) {

			Pointage ptg = pointageRepository.getEntity(Pointage.class, dto.getIdPointage());

			// check whether the Pointage exists
			if (ptg == null) {
				result.getErrors().add(String.format("Le pointage %s n'existe pas.", dto.getIdPointage()));
				continue;
			}

			// Check whether the user has sufficient rights to update this
			// Pointage
			if (!droitsAgentsIds.contains(ptg.getIdAgent())) {
				result.getErrors().add(
						String.format("L'agent %s n'a pas le droit de mettre à jour le pointage %s de l'agent %s.", matriculeConvertor.tryConvertIdAgentToNomatr(idAgent), ptg.getIdPointage(),
								matriculeConvertor.tryConvertIdAgentToNomatr(ptg.getIdAgent())));
				continue;
			}

			// #18224 REJETER un pointage JOURNALISE :
			// on ne peut pas modifier un pointage de plus de 3 mois dans le
			// kiosque
			// #36156 : on autorise le changement pour la prime 7714
			result = ptgDataCosistencyRules.checkDateLundiAnterieurA3MoisWithPointage(result, ptg.getDateLundi(),ptg);
			if (!result.getErrors().isEmpty()) {
				break;
			}
			
			if (!result.getErrors().isEmpty()) {
				break;
			}

			// Check whether the current target and if it can be updated
			EtatPointage currentEtat = ptg.getLatestEtatPointage();
			if (currentEtat.getEtat() != EtatPointageEnum.SAISI && currentEtat.getEtat() != EtatPointageEnum.APPROUVE && currentEtat.getEtat() != EtatPointageEnum.REFUSE
			// #18224 on doit pouvoir rejeter un pointage journalise de moins de
			// 3 mois
					&& currentEtat.getEtat() != EtatPointageEnum.JOURNALISE) {
				result.getErrors().add(
						String.format("Impossible de mettre à jour le pointage %s de l'agent %s car celui-ci est à l'état %s.", ptg.getIdPointage(),
								matriculeConvertor.tryConvertIdAgentToNomatr(ptg.getIdAgent()), currentEtat.getEtat().name()));
				continue;
			}

			// Check whether the target EtatPointage is authorized
			EtatPointageEnum targetEtat = EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat());

			if (targetEtat != EtatPointageEnum.APPROUVE && targetEtat != EtatPointageEnum.REFUSE && targetEtat != EtatPointageEnum.SAISI
			// #18224 on doit pouvoir rejeter un pointage journalise de moins de
			// 3 mois
					&& targetEtat != EtatPointageEnum.REJETE) {
				result.getErrors().add(
						String.format("Impossible de mettre à jour le pointage %s de l'agent %s à l'état %s. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.", ptg.getIdPointage(),
								matriculeConvertor.tryConvertIdAgentToNomatr(ptg.getIdAgent()), targetEtat.name()));
				continue;
			}

			// #18224 des pointages journalises ne peuvent etre que rejetees
			// et on ne peut rejeter que des pointages journalises
			if ((currentEtat.getEtat() == EtatPointageEnum.JOURNALISE && targetEtat != EtatPointageEnum.REJETE)
					|| (currentEtat.getEtat() != EtatPointageEnum.JOURNALISE && targetEtat == EtatPointageEnum.REJETE)) {
				result.getErrors().add(
						String.format("Impossible de mettre à jour le pointage %s de l'agent %s à l'état %s. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.", ptg.getIdPointage(),
								matriculeConvertor.tryConvertIdAgentToNomatr(ptg.getIdAgent()), targetEtat.name()));
				continue;
			}

			// #13380 dans le cas ou on approuve ou saisit, on check si une
			// absence n existe pas sur les memes dates
			if (targetEtat == EtatPointageEnum.SAISI || targetEtat == EtatPointageEnum.APPROUVE) {
				ptgDataCosistencyRules.checkAbsences(result, ptg.getIdAgent(), Arrays.asList(ptg));
				if (!result.getErrors().isEmpty())
					continue;
			}

			addRecuperationToAgent(targetEtat, ptg);

			// at last, create and add the new EtatPointage
			EtatPointage etat = new EtatPointage();
			etat.setDateEtat(helperService.getCurrentDate());
			etat.setDateMaj(helperService.getCurrentDate());
			etat.setPointage(ptg);
			etat.setIdAgent(idAgent);
			etat.setEtat(targetEtat);
			ptg.getEtats().add(etat);

			// #18224
			Date dateEtat = helperService.getCurrentDate();
			reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(idAgent, dto, currentEtat.getEtat(), ptg, dateEtat);
			reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(idAgent, dto, currentEtat.getEtat(), ptg, dateEtat);
			
			// #19718 si rejet ou approbation de prime Renfort de Garde
			reinitialisePointageHSupEtAbsEtPrime7717_AApprouveForVentilationSuiteRejetOuApprobation(idAgent, dto, currentEtat.getEtat(), ptg, dateEtat);
		}

		return result;
	}

	@Override
	public ReturnMessageDto setPointagesEtatSIRH(Integer idAgent, List<PointagesEtatChangeDto> etatsDto) {

		ReturnMessageDto result = new ReturnMessageDto();
		for (PointagesEtatChangeDto dto : etatsDto) {
			Pointage ptg = pointageRepository.getEntity(Pointage.class, dto.getIdPointage());

			// check whether the Pointage exists
			if (ptg == null) {
				result.getErrors().add(String.format("Le pointage %s n'existe pas.", dto.getIdPointage()));
				continue;
			}

			// Check whether the current target and the target EtatPointage are
			// authorized
			EtatPointageEnum currentEtat = ptg.getLatestEtatPointage().getEtat();
			EtatPointageEnum targetEtat = EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat());
			boolean ok = true;

			if (currentEtat != EtatPointageEnum.SAISI && currentEtat != EtatPointageEnum.EN_ATTENTE && currentEtat != EtatPointageEnum.REJETE && currentEtat != EtatPointageEnum.APPROUVE
					&& targetEtat == EtatPointageEnum.APPROUVE) {
				ok = false;
			}

			if (currentEtat != EtatPointageEnum.APPROUVE && currentEtat != EtatPointageEnum.REJETE && currentEtat != EtatPointageEnum.VENTILE && targetEtat == EtatPointageEnum.EN_ATTENTE) {
				ok = false;
			}
			if (currentEtat != EtatPointageEnum.APPROUVE && currentEtat != EtatPointageEnum.EN_ATTENTE && currentEtat != EtatPointageEnum.VENTILE && currentEtat != EtatPointageEnum.VALIDE
			// #18224 on doit pouvoir rejeter un pointage journalise
					&& currentEtat != EtatPointageEnum.JOURNALISE && targetEtat == EtatPointageEnum.REJETE) {
				ok = false;
			}
			if (!ok) {
				result.getErrors().add(
						String.format("Impossible de mettre à %s le pointage %s de l'agent %s car celui-ci est à l'état %s.", targetEtat.name(), ptg.getIdPointage(),
								matriculeConvertor.tryConvertIdAgentToNomatr(ptg.getIdAgent()), currentEtat.name()));
				continue;
			}

			if (targetEtat != EtatPointageEnum.APPROUVE && targetEtat != EtatPointageEnum.REJETE && targetEtat != EtatPointageEnum.EN_ATTENTE) {
				result.getErrors().add(
						String.format("Impossible de mettre à jour le pointage %s de l'agent %s à l'état %s. Seuls APPROUVE, REJETE ou EN_ATTENTE sont acceptés depuis SIRH.", ptg.getIdPointage(),
								matriculeConvertor.tryConvertIdAgentToNomatr(ptg.getIdAgent()), targetEtat.name()));
				continue;
			}

			// #13380 dans le cas ou on approuve ou saisit, on check si une
			// absence n existe pas sur les memes dates
			if (targetEtat == EtatPointageEnum.SAISI || targetEtat == EtatPointageEnum.APPROUVE) {
				ptgDataCosistencyRules.checkAbsences(result, ptg.getIdAgent(), Arrays.asList(ptg));
				if (!result.getErrors().isEmpty())
					continue;
			}

			// at last, create and add the new EtatPointage
			EtatPointage etat = new EtatPointage();

			Spcarr spcarr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(ptg.getIdAgent()), ptg.getDateDebut());

			VentilDate lastVentil = ventilRepository.getLatestVentilDate(helperService.getTypeChainePaieFromStatut(spcarr.getStatutCarriere()), false);
			if ((targetEtat == EtatPointageEnum.APPROUVE
			// #18224 #19322 on doit pouvoir rejeter un pointage journalise
					|| (currentEtat != EtatPointageEnum.JOURNALISE && targetEtat == EtatPointageEnum.REJETE))
					&& lastVentil != null)
				etat.setDateEtat(lastVentil.getDateVentilation());
			else
				etat.setDateEtat(helperService.getCurrentDate());

			etat.setDateMaj(helperService.getCurrentDate());
			etat.setPointage(ptg);
			etat.setIdAgent(idAgent);
			etat.setEtat(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()));
			ptg.getEtats().add(etat);

			Date dateEtat = null;
			if (null != lastVentil) {
				dateEtat = lastVentil.getDateVentilation();
			} else {
				dateEtat = helperService.getCurrentDate();
			}

			addRecuperationToAgent(targetEtat, ptg);

			reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(idAgent, dto, currentEtat, ptg, dateEtat);
			reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(idAgent, dto, currentEtat, ptg, dateEtat);
			reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(idAgent, dto, currentEtat, ptg, dateEtat);
			
			// #19718 si rejet ou approbation de prime Renfort de Garde
			reinitialisePointageHSupEtAbsEtPrime7717_AApprouveForVentilationSuiteRejetOuApprobation(idAgent, dto, currentEtat, ptg, dateEtat);
		}
		return result;
	}

	/**
	 * Passer a APPOUVE les primes de meme rubrique pour un agent
	 * 
	 * @param idAgent
	 *            Integer
	 * @param dto
	 *            PointagesEtatChangeDto
	 * @param currentEtat
	 *            EtatPointageEnum
	 * @param ptg
	 *            Pointage
	 * @param dateEtat
	 *            Date
	 */
	protected void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(Integer idAgent, PointagesEtatChangeDto dto, EtatPointageEnum currentEtat, Pointage ptg, Date dateEtat) {

		if (EtatPointageEnum.REJETE.equals(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()))
				&& (EtatPointageEnum.VALIDE.equals(currentEtat) || EtatPointageEnum.JOURNALISE.equals(currentEtat)) && (RefTypePointageEnum.PRIME.equals(ptg.getTypePointageEnum()))) {

			List<Pointage> listePointagesPrimeAgent = ventilationRepository.getListPointagesPrimeValideByMoisAndRefPrime(ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getIdRefPrime());

			List<Pointage> filteredListePointagesPrimeAgent = pointageService.filterOldPointagesAndEtatFromList(listePointagesPrimeAgent,
					Arrays.asList(EtatPointageEnum.VALIDE, EtatPointageEnum.JOURNALISE), null);

			if (null != filteredListePointagesPrimeAgent && !filteredListePointagesPrimeAgent.isEmpty()) {
				for (Pointage pointage : filteredListePointagesPrimeAgent) {
					if ((EtatPointageEnum.VALIDE.equals(pointage.getLatestEtatPointage().getEtat()) || EtatPointageEnum.JOURNALISE.equals(pointage.getLatestEtatPointage().getEtat()))
							&& (RefTypePointageEnum.PRIME.equals(pointage.getTypePointageEnum())) && !pointage.getIdPointage().equals(ptg.getIdPointage())) {

						EtatPointage etat = new EtatPointage();
						etat.setDateEtat(dateEtat);
						etat.setDateMaj(helperService.getCurrentDate());
						etat.setPointage(pointage);
						etat.setIdAgent(idAgent);
						etat.setEtat(EtatPointageEnum.APPROUVE);
						pointage.getEtats().add(etat);
					}
				}
			}
		}
	}

	protected void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(Integer idAgent, PointagesEtatChangeDto dto, EtatPointageEnum currentEtat, Pointage ptg, Date dateEtat) {

		if (EtatPointageEnum.REJETE.equals(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()))
				&& (EtatPointageEnum.VALIDE.equals(currentEtat) || EtatPointageEnum.JOURNALISE.equals(currentEtat))
				&& (RefTypePointageEnum.H_SUP.equals(ptg.getTypePointageEnum()) || RefTypePointageEnum.ABSENCE.equals(ptg.getTypePointageEnum()))) {
			// si on REJETE un pointages VALIDE
			// on reinitialise les autres pointages du meme agent pour la meme
			// semaine
			// a APPROUVE pour prise en compte dans la ventilation
			List<Pointage> listePointagesAgent = pointageRepository.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi());
			if (null != listePointagesAgent && !listePointagesAgent.isEmpty()) {
				for (Pointage pointage : listePointagesAgent) {
					if ((EtatPointageEnum.VALIDE.equals(pointage.getLatestEtatPointage().getEtat())
					// TODO voir avec Michel pour ce point
					|| EtatPointageEnum.JOURNALISE.equals(pointage.getLatestEtatPointage().getEtat()))
							&& (RefTypePointageEnum.H_SUP.equals(pointage.getTypePointageEnum()) || RefTypePointageEnum.ABSENCE.equals(pointage.getTypePointageEnum()))
							&& !pointage.getIdPointage().equals(ptg.getIdPointage())) {

						EtatPointage etat = new EtatPointage();
						etat.setDateEtat(dateEtat);
						etat.setDateMaj(helperService.getCurrentDate());
						etat.setPointage(pointage);
						etat.setIdAgent(idAgent);
						etat.setEtat(EtatPointageEnum.APPROUVE);
						pointage.getEtats().add(etat);
					}
				}
			}
		}
	}

	protected void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(Integer idAgent, PointagesEtatChangeDto dto, EtatPointageEnum currentEtat, Pointage ptg, Date dateEtat) {

		if (EtatPointageEnum.APPROUVE.equals(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat())) && EtatPointageEnum.REJETE.equals(currentEtat)
				&& (RefTypePointageEnum.H_SUP.equals(ptg.getTypePointageEnum()) || RefTypePointageEnum.ABSENCE.equals(ptg.getTypePointageEnum()))) {
			// si on REJETE un pointages VALIDE
			// on reinitialise les autres pointages du meme agent pour la meme
			// semaine
			// a APPROUVE pour prise en compte dans la ventilation
			List<Pointage> listePointagesAgent = pointageRepository.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi());
			if (null != listePointagesAgent && !listePointagesAgent.isEmpty()) {
				for (Pointage pointage : listePointagesAgent) {
					if (EtatPointageEnum.VALIDE.equals(pointage.getLatestEtatPointage().getEtat())
							&& (RefTypePointageEnum.H_SUP.equals(pointage.getTypePointageEnum()) || RefTypePointageEnum.ABSENCE.equals(pointage.getTypePointageEnum()))
							&& !pointage.getIdPointage().equals(ptg.getIdPointage())) {

						EtatPointage etat = new EtatPointage();
						etat.setDateEtat(dateEtat);
						etat.setDateMaj(helperService.getCurrentDate());
						etat.setPointage(pointage);
						etat.setIdAgent(idAgent);
						etat.setEtat(EtatPointageEnum.APPROUVE);
						pointage.getEtats().add(etat);
					}
				}
			}
		}
	}

	/**
	 * Uen prime Renfort de Garde est une prime calculée qui genere des HSup dans la ventilation.
	 * 
	 * Si on rejette un Renfort de Garde, il faut remettre a Approuve tous les pointages 
	 * utils a la ventilation des Heures Sup. 
	 * c-a-d Pointages HSup, Abs et Prime Renfort de Garde
	 * 
	 * Pour prise en compte dans la prochaine ventilation
	 *  
	 * @param idAgent Integer Agent connecte
	 * @param dto PointagesEtatChangeDto DTO de mise a jour
	 * @param currentEtat EtatPointageEnum Etat actuel du pointage
	 * @param ptg Pointage concerne
	 * @param dateEtat Date de l action
	 */
	protected void reinitialisePointageHSupEtAbsEtPrime7717_AApprouveForVentilationSuiteRejetOuApprobation(Integer idAgent, PointagesEtatChangeDto dto, EtatPointageEnum currentEtat, Pointage ptg, Date dateEtat) {
		
		// si le pointage est un Renfort de Garde
		if ((RefTypePointageEnum.PRIME.equals(ptg.getTypePointageEnum()) 
				&& null != ptg.getRefPrime()
				&& ptg.getRefPrime().getNoRubr().equals(VentilationPrimeService.PRIME_RENFORT_GARDE))
				// ET SOIT on a rejete ce pointage VALIDE ou JOURNALISE
			&& (EtatPointageEnum.REJETE.equals(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()))
						&& (EtatPointageEnum.VALIDE.equals(currentEtat) || EtatPointageEnum.JOURNALISE.equals(currentEtat)))
					// SOIT on a APPROUVE ce pointage REJETE
				|| (EtatPointageEnum.APPROUVE.equals(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat())) 
						&& EtatPointageEnum.REJETE.equals(currentEtat))
			) {
			// on reinitialise les autres pointages du meme agent pour la meme
			// semaine
			// a APPROUVE pour prise en compte dans la ventilation
			List<Pointage> listePointagesAgent = pointageRepository.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi());
			if (null != listePointagesAgent && !listePointagesAgent.isEmpty()) {
				for (Pointage pointage : listePointagesAgent) {
					if ((EtatPointageEnum.VALIDE.equals(pointage.getLatestEtatPointage().getEtat())
					|| EtatPointageEnum.JOURNALISE.equals(pointage.getLatestEtatPointage().getEtat()))
							&& (RefTypePointageEnum.H_SUP.equals(pointage.getTypePointageEnum()) 
									|| RefTypePointageEnum.ABSENCE.equals(pointage.getTypePointageEnum())
									|| (RefTypePointageEnum.PRIME.equals(pointage.getTypePointageEnum())
											&& pointage.getRefPrime().getNoRubr().equals(VentilationPrimeService.PRIME_RENFORT_GARDE))
									)
							&& !pointage.getIdPointage().equals(ptg.getIdPointage())) {

						EtatPointage etat = new EtatPointage();
						etat.setDateEtat(dateEtat);
						etat.setDateMaj(helperService.getCurrentDate());
						etat.setPointage(pointage);
						etat.setIdAgent(idAgent);
						etat.setEtat(EtatPointageEnum.APPROUVE);
						pointage.getEtats().add(etat);
					}
				}
			}
		}
	}

	@Override
	public List<AgentDto> listerTousAgentsPointages() {

		List<AgentDto> result = new ArrayList<AgentDto>();
		List<Integer> list = pointageRepository.listAllDistinctIdAgentPointage();

		for (Integer idAgent : list) {
			AgentDto agDto = new AgentDto();
			agDto.setIdAgent(idAgent);
			result.add(agDto);
		}
		return result;
	}

	@Override
	public void addRecuperationToAgent(EtatPointageEnum targetEtat, Pointage ptg) {

		// si le pointage est une heure sup en recuperation
		if (RefTypePointageEnum.H_SUP.equals(ptg.getTypePointageEnum()) && ptg.getHeureSupRecuperee()) {

			// calcul du nombre de minutes
			Integer nombreMinutes = helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin());
			
			// #30544 Indemnité forfaitaire travail DPM
			int nombreMinutesMajorees = dpmService.calculNombreMinutesRecupereesMajoreesToAgentForOnePointage(ptg);

			if (0 < nombreMinutesMajorees) {
				nombreMinutes += nombreMinutesMajorees;
			}

			EtatPointageEnum currentEtat = ptg.getLatestEtatPointage().getEtat();

			// dans le cas ou le pointage est approuve
			if (EtatPointageEnum.APPROUVE.equals(targetEtat)
					&& (EtatPointageEnum.SAISI.equals(currentEtat) || EtatPointageEnum.REFUSE.equals(currentEtat) || EtatPointageEnum.REJETE.equals(currentEtat))) {
				absWsConsumer.addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), nombreMinutes, ptg.getIdPointage(), null != ptg.getPointageParent() ? ptg
						.getPointageParent().getIdPointage() : null);
			}
			// dans le cas ou le pointage est refuse, rejete
			if ((EtatPointageEnum.REFUSE.equals(targetEtat) || EtatPointageEnum.REJETE.equals(targetEtat) || EtatPointageEnum.SAISI.equals(targetEtat))
					&& (EtatPointageEnum.APPROUVE.equals(currentEtat) || EtatPointageEnum.VENTILE.equals(currentEtat) || EtatPointageEnum.VALIDE.equals(currentEtat) || EtatPointageEnum.JOURNALISE
							.equals(currentEtat))) {
				absWsConsumer.addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), 0, ptg.getIdPointage(), null != ptg.getPointageParent() ? ptg.getPointageParent()
						.getIdPointage() : null);
			}
		}
	}

}
