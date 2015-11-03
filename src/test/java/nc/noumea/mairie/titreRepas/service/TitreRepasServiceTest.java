package nc.noumea.mairie.titreRepas.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefGroupeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.repository.ITitreRepasRepository;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TitreRepasServiceTest {

	private TitreRepasService service = new TitreRepasService();

	private RefTypeSaisiCongeAnnuelDto baseConge = new RefTypeSaisiCongeAnnuelDto();

	@Before
	public void setting() {
		baseConge.setIdRefTypeSaisiCongeAnnuel(1);
		baseConge.setDecompteSamedi(true);
	}

	@Test
	public void checkDateJourBetween1And10ofMonth_ok() {

		ReturnMessageDto result = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime().withDayOfMonth(5).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);

		result = service.checkDateJourBetween1And10ofMonth(result);

		assertTrue(result.getErrors().isEmpty());
	}

	@Test
	public void checkDateJourBetween1And10ofMonth_ko() {

		ReturnMessageDto result = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime().withDayOfMonth(11).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);

		result = service.checkDateJourBetween1And10ofMonth(result);

		assertEquals(result.getErrors().get(0), TitreRepasService.DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_10_DU_MOIS);
	}

	@Test
	public void checkPAUnJourActiviteMinimumsurMoisPrecedent_false() {

		Integer idAgent = 9005138;
		Date dateMonth = new DateTime(2015, 10, 22, 0, 0, 0).toDate();

		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMonth)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMonth)).thenReturn(toDate);

		Spadmn paInactive = new Spadmn();
		paInactive.setCdpadm("CA");

		Spadmn paInactiveBis = new Spadmn();
		paInactiveBis.setCdpadm("11");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(paInactive);
		listPA.add(paInactiveBis);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, fromDate, toDate)).thenReturn(listPA);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkPAUnJourActiviteMinimumsurMoisPrecedent(idAgent, dateMonth));
	}

	@Test
	public void checkPAUnJourActiviteMinimumsurMoisPrecedent_true() {

		Integer idAgent = 9005138;
		Date dateMonth = new DateTime(2015, 10, 22, 0, 0, 0).toDate();

		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMonth)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMonth)).thenReturn(toDate);

		Spadmn paInactive = new Spadmn();
		paInactive.setCdpadm("CA");

		Spadmn paInactiveBis = new Spadmn();
		paInactiveBis.setCdpadm("11");

		Spadmn paActive = new Spadmn();
		paActive.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(paInactive);
		listPA.add(paInactiveBis);
		listPA.add(paActive);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, fromDate, toDate)).thenReturn(listPA);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkPAUnJourActiviteMinimumsurMoisPrecedent(idAgent, dateMonth));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_false_CongeToutLeMois() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsence = new RefGroupeAbsenceDto();
		groupeAbsence.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demande = new DemandeDto();
		demande.setAgentWithServiceDto(agent);
		demande.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demande.setDateFin(new DateTime(2015, 11, 22, 0, 0, 0).toDate());
		demande.setGroupeAbsence(groupeAbsence);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demande);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_CongeToutLeMoisASA() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsence = new RefGroupeAbsenceDto();
		groupeAbsence.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.AS.getValue());

		DemandeDto demande = new DemandeDto();
		demande.setAgentWithServiceDto(agent);
		demande.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demande.setDateFin(new DateTime(2015, 11, 22, 0, 0, 0).toDate());
		demande.setGroupeAbsence(groupeAbsence);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demande);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_2CongeToutLeMois() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 19, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 30, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_2Conge_maisAvecUnJourTravailEntre2() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 20, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 30, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_2CongeToutLeMois_maisTravailUnWeekend() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 19, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 30, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_2CongeToutLeMois_plusUnJourFerie() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 18, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 30, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		JourDto jourFerie = new JourDto();
		jourFerie.setFerie(true);
		jourFerie.setJour(new DateTime(2015, 10, 17, 0, 0, 0).toDate());

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);
		Mockito.when(helperService.isJourHoliday(Arrays.asList(jourFerie), new DateTime(2015, 10, 17, 0, 0, 0).toDate())).thenReturn(true);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, Arrays.asList(jourFerie)));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_2CongeToutLeMois_maisTravailUnJourFerie() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 18, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 30, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend

		JourDto jourFerie = new JourDto();
		jourFerie.setFerie(true);
		jourFerie.setJour(new DateTime(2015, 10, 17, 0, 0, 0).toDate());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);
		Mockito.when(helperService.isJourHoliday(Arrays.asList(jourFerie), new DateTime(2015, 10, 17, 0, 0, 0).toDate())).thenReturn(true);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, Arrays.asList(jourFerie)));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_2CongeToutLeMois_maisTravailDernierJourMoisSamedi() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 17, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 30, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_2CongeToutLeMoisMemeWeekend() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 17, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 31, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_TravailWeekend_CongeEtMaladie() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 17, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 23, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		SpabsenId spabsenId = new SpabsenId();
		spabsenId.setDatdeb(20151024);
		Spabsen spabsen = new Spabsen();
		spabsen.setId(spabsenId);
		spabsen.setDatfin(20151031);

		List<Spabsen> listSpAbsen = new ArrayList<Spabsen>();
		listSpAbsen.add(spabsen);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListMaladieBetween(idAgent, fromDate, toDate)).thenReturn(listSpAbsen);

		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015, 10, 24, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015, 10, 31, 0, 0, 0).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_TravailWeekend_CongeEtMaladie() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 17, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 23, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		SpabsenId spabsenId = new SpabsenId();
		spabsenId.setDatdeb(20151025);
		Spabsen spabsen = new Spabsen();
		spabsen.setId(spabsenId);
		spabsen.setDatfin(20151031);

		List<Spabsen> listSpAbsen = new ArrayList<Spabsen>();
		listSpAbsen.add(spabsen);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListMaladieBetween(idAgent, fromDate, toDate)).thenReturn(listSpAbsen);

		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015, 10, 25, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015, 10, 31, 0, 0, 0).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_PasDeTravailWeekend_CongeEtMaladie() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 17, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 23, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		SpabsenId spabsenId = new SpabsenId();
		spabsenId.setDatdeb(20151026);
		Spabsen spabsen = new Spabsen();
		spabsen.setId(spabsenId);
		spabsen.setDatfin(20151031);

		List<Spabsen> listSpAbsen = new ArrayList<Spabsen>();
		listSpAbsen.add(spabsen);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListMaladieBetween(idAgent, fromDate, toDate)).thenReturn(listSpAbsen);

		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015, 10, 26, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015, 10, 31, 0, 0, 0).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_PasDeTravailWeekend_CongeEtMaladie() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);

		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());

		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015, 9, 22, 0, 0, 0).toDate());
		demandeCA.setDateFin(new DateTime(2015, 10, 16, 0, 0, 0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);

		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());

		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015, 10, 17, 0, 0, 0).toDate());
		demandeCE.setDateFin(new DateTime(2015, 10, 23, 0, 0, 0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);

		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);

		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le
												// weekend

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		SpabsenId spabsenId = new SpabsenId();
		spabsenId.setDatdeb(20151027);
		Spabsen spabsen = new Spabsen();
		spabsen.setId(spabsenId);
		spabsen.setDatfin(20151031);

		List<Spabsen> listSpAbsen = new ArrayList<Spabsen>();
		listSpAbsen.add(spabsen);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListMaladieBetween(idAgent, fromDate, toDate)).thenReturn(listSpAbsen);

		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015, 10, 27, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015, 10, 31, 0, 0, 0).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		// travaille le lundi 26/10/2015
		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}

	@Test
	public void checkPrimePanierSurAffectation_false() {

		RefPrimeDto prime = new RefPrimeDto();
		prime.setNumRubrique(7701);

		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(9005138);
		aff.getListPrimesAff().add(prime);

		assertFalse(service.checkPrimePanierSurAffectation(aff, 9005138));
	}

	@Test
	public void checkPrimePanierSurAffectation_true() {

		RefPrimeDto prime = new RefPrimeDto();
		prime.setNumRubrique(7701);

		RefPrimeDto prime7704 = new RefPrimeDto();
		prime7704.setNumRubrique(7704);

		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(9005138);
		aff.getListPrimesAff().add(prime);
		aff.getListPrimesAff().add(prime7704);

		assertTrue(service.checkPrimePanierSurAffectation(aff, 9005138));

		aff.getListPrimesAff().clear();

		RefPrimeDto prime7713 = new RefPrimeDto();
		prime7713.setNumRubrique(7713);
		aff.getListPrimesAff().add(prime7713);

		assertTrue(service.checkPrimePanierSurAffectation(aff, 9005138));
	}

	@Test
	public void checkAgentIsFiliereIncendie_false() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();

		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(5138, fromDate, toDate)).thenReturn("A");

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkAgentIsFiliereIncendie(idAgent, dateMoisPrecedent));
	}

	@Test
	public void checkAgentIsFiliereIncendie_true() {

		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015, 10, 22, 0, 0, 0).toDate();

		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(5138, fromDate, toDate)).thenReturn("I");

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkAgentIsFiliereIncendie(idAgent, dateMoisPrecedent));
	}

	@Test
	public void checkDateJourBetween1OfMonthAndGeneration_ok() {

		ReturnMessageDto rmd = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 22, 0, 0, 0).toDate());

		TitreRepasEtatPayeur etatPayeur = new TitreRepasEtatPayeur();
		etatPayeur.setDateEtatPayeur(new DateTime(2015, 10, 23, 0, 0, 0).toDate());

		List<TitreRepasEtatPayeur> listEtatPayeur = new ArrayList<TitreRepasEtatPayeur>();
		listEtatPayeur.add(etatPayeur);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasEtatPayeur()).thenReturn(listEtatPayeur);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		rmd = service.checkDateJourBetween1OfMonthAndGeneration(rmd);

		assertEquals(0, rmd.getErrors().size());
	}

	@Test
	public void checkDateJourBetween1OfMonthAndGeneration_ko() {

		ReturnMessageDto rmd = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 24, 0, 0, 0).toDate());

		TitreRepasEtatPayeur etatPayeur = new TitreRepasEtatPayeur();
		etatPayeur.setDateEdition(new DateTime(2015, 10, 23, 0, 0, 0).toDate());

		List<TitreRepasEtatPayeur> listEtatPayeur = new ArrayList<TitreRepasEtatPayeur>();
		listEtatPayeur.add(etatPayeur);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasEtatPayeur()).thenReturn(listEtatPayeur);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		rmd = service.checkDateJourBetween1OfMonthAndGeneration(rmd);

		assertEquals(TitreRepasService.DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_EDITION_PAYEUR, rmd.getErrors().get(0));
	}

	@Test
	public void checkDataTitreRepasDemandeDto() {

		ReturnMessageDto rmd = new ReturnMessageDto();

		TitreRepasDemandeDto dto = null;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005138);

		rmd = service.checkDataTitreRepasDemandeDto(rmd, dto);
		assertEquals(rmd.getErrors().get(0), TitreRepasService.DTO_NULL);

		dto = new TitreRepasDemandeDto();
		dto.setDateMonth(null);
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		rmd.getErrors().clear();
		rmd = service.checkDataTitreRepasDemandeDto(rmd, dto);
		assertEquals(rmd.getErrors().get(0), TitreRepasService.MOIS_COURS_NON_SAISI);

		dto.setDateMonth(new Date());
		dto.setAgent(null);

		rmd.getErrors().clear();
		rmd = service.checkDataTitreRepasDemandeDto(rmd, dto);
		assertEquals(rmd.getErrors().get(0), TitreRepasService.AGENT_NON_SAISI);

		dto.setAgent(ag);
		dto.setIdRefEtat(null);

		rmd.getErrors().clear();
		rmd = service.checkDataTitreRepasDemandeDto(rmd, dto);
		assertEquals(rmd.getErrors().get(0), String.format(TitreRepasService.ETAT_NON_SAISI, dto.getAgent().getIdAgent()));
	}

	@Test
	public void enregistreTitreDemandeAgent_erreurDroit() {

		Integer idAgentConnecte = 9005138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9002990);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.isUserApprobateur(idAgentConnecte)).thenReturn(false);
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(false);

		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.ERREUR_DROIT_AGENT);
	}

	@Test
	public void enregistreTitreDemandeAgent_pasDeBaseConge() {

		Integer idAgentConnecte = 9005138;
		Integer noMatr = 5138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(idAgentConnecte);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(null);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.AUCUNE_BASE_CONGE, dto.getAgent().getIdAgent()));
		Mockito.verify(titreRepasRepository, Mockito.never()).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreTitreDemandeAgent_titreRepasDejaExistant() {

		Integer idAgentConnecte = 9005138;
		Integer noMatr = 5138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(idAgentConnecte);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		List<TitreRepasDemande> listTitreRepasDemande = new ArrayList<TitreRepasDemande>();
		listTitreRepasDemande.add(new TitreRepasDemande());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(Arrays.asList(dto.getAgent().getIdAgent()), null, null, null, null, dto.getDateMonth())).thenReturn(
				listTitreRepasDemande);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.TITRE_DEMANDE_DEJA_EXISTANT, dto.getAgent().getIdAgent()));
		Mockito.verify(titreRepasRepository, Mockito.never()).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreTitreDemandeAgent_modify_pasDeTR() {

		Integer idAgentConnecte = 9005138;
		Integer noMatr = 5138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(idAgentConnecte);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());
		dto.setIdTrDemande(1);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.TITRE_DEMANDE_INEXISTANT, dto.getAgent().getIdAgent()));
		Mockito.verify(titreRepasRepository, Mockito.never()).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreTitreDemandeAgent_modify_errorEtatJournalise() {

		Integer idAgentConnecte = 9005138;
		Integer noMatr = 5138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(idAgentConnecte);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());
		dto.setIdTrDemande(1);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		TitreRepasEtatDemande etatJournalise = new TitreRepasEtatDemande();
		etatJournalise.setEtat(EtatPointageEnum.JOURNALISE);
		
		TitreRepasDemande tr = new TitreRepasDemande();
		tr.getEtats().add(etatJournalise);
		
		List<TitreRepasDemande> listTitreRepasDemande = new ArrayList<TitreRepasDemande>();
		listTitreRepasDemande.add(tr);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande())).thenReturn(tr);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.MODIFICATION_IMPOSSIBLE_DEMANDE_JOURNALISEE);
		Mockito.verify(titreRepasRepository, Mockito.never()).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreTitreDemandeAgent_modify_ok() {

		Integer idAgentConnecte = 9005138;
		Integer noMatr = 5138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(idAgentConnecte);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());
		dto.setIdTrDemande(1);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		TitreRepasEtatDemande etatJournalise = new TitreRepasEtatDemande();
		etatJournalise.setEtat(EtatPointageEnum.SAISI);
		
		TitreRepasDemande tr = new TitreRepasDemande();
		tr.getEtats().add(etatJournalise);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande())).thenReturn(tr);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getErrors().size());
		assertEquals(result.getInfos().get(0), String.format(TitreRepasService.ENREGISTREMENT_OK, dto.getAgent().getIdAgent()));
		Mockito.verify(titreRepasRepository, Mockito.times(1)).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreTitreDemandeAgent_ok() {

		Integer idAgentConnecte = 9005138;
		Integer noMatr = 5138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(idAgentConnecte);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getErrors().size());
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_OK);
		Mockito.verify(titreRepasRepository, Mockito.times(1)).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreListTitreDemandeFromSIRH_erreurDroit() {

		Integer idAgentConnecte = 9005138;

		List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReturnMessageDto errorDroit = new ReturnMessageDto();
		errorDroit.getErrors().add("agent non SIRH");

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(errorDroit);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromSIRH(idAgentConnecte, listDto);

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.ERREUR_DROIT_AGENT);
		Mockito.verify(titreRepasRepository, Mockito.never()).persist(Mockito.isA(TitreRepasDemande.class));
	}

	/**
	 * il y a une erreur, mais pour SIRH on ne bloque pas l'enregistrement. on
	 * renvoie juste un message d info
	 */
	@Test
	public void enregistreListTitreDemandeFromSIRH_1true_1false_1error() {

		Integer idAgentConnecte = 9005138;
		AgentWithServiceDto ag3 = new AgentWithServiceDto();
		ag3.setIdAgent(9009999);
		AgentWithServiceDto ag2 = new AgentWithServiceDto();
		ag2.setIdAgent(9005854);
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005131);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire 1");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		TitreRepasDemandeDto dto2 = new TitreRepasDemandeDto();
		dto2.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto2.setCommande(false);
		dto2.setCommentaire("commentaire 2");
		dto2.setAgent(ag2);
		dto2.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		TitreRepasDemandeDto dto3 = new TitreRepasDemandeDto();
		dto3.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto3.setCommande(true);
		dto3.setCommentaire("commentaire 3");
		dto3.setAgent(ag3);
		dto3.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();
		listDto.add(dto);
		listDto.add(dto2);
		listDto.add(dto3);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		Integer noMatr = dto.getAgent().getIdAgent() - 9000000;
		Integer noMatr2 = dto2.getAgent().getIdAgent() - 9000000;
		Integer noMatr3 = dto3.getAgent().getIdAgent() - 9000000;

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto2.getAgent().getIdAgent())).thenReturn(noMatr2);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto3.getAgent().getIdAgent())).thenReturn(noMatr3);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		AffectationDto aff2 = new AffectationDto();
		aff2.setBaseConge(baseConge);
		aff2.setIdAgent(dto2.getAgent().getIdAgent());

		AffectationDto aff3 = new AffectationDto();
		aff3.setBaseConge(baseConge);
		aff3.setIdAgent(dto3.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);
		listAffectation.add(aff2);
		listAffectation.add(aff3);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(new ReturnMessageDto());
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(
				sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()),
						dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(
				absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois,
						dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr2, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr3, dateDebutMois, dateFinMois)).thenReturn(listPA);

		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(noMatr3, dateDebutMois, dateFinMois)).thenReturn("I");

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromSIRH(idAgentConnecte, listDto);

		assertEquals(0, result.getErrors().size());
		assertEquals(4, result.getInfos().size());
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_OK);
		assertEquals(result.getInfos().get(1), TitreRepasService.ENREGISTREMENT_OK);
		assertEquals(result.getInfos().get(2), TitreRepasService.FILIERE_INCENDIE);
		assertEquals(result.getInfos().get(3), TitreRepasService.ENREGISTREMENT_OK);
		Mockito.verify(titreRepasRepository, Mockito.times(3)).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreListTitreDemandeFromKiosque_erreurDroit() {

		Integer idAgentConnecte = 9005138;

		List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.isUserApprobateur(idAgentConnecte)).thenReturn(false);
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(false);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, listDto);

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.ERREUR_DROIT_AGENT);
		Mockito.verify(titreRepasRepository, Mockito.never()).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreListTitreDemandeFromKiosque_1true_1false_1error() {

		Integer idAgentConnecte = 9005138;

		AgentWithServiceDto ag3 = new AgentWithServiceDto();
		ag3.setIdAgent(9009999);
		AgentWithServiceDto ag2 = new AgentWithServiceDto();
		ag2.setIdAgent(9005854);
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005131);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire 1");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		TitreRepasDemandeDto dto2 = new TitreRepasDemandeDto();
		dto2.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto2.setCommande(false);
		dto2.setCommentaire("commentaire 2");
		dto2.setAgent(ag2);
		dto2.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		TitreRepasDemandeDto dto3 = new TitreRepasDemandeDto();
		dto3.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto3.setCommande(true);
		dto3.setCommentaire("commentaire 3");
		dto3.setAgent(ag3);
		dto3.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		List<TitreRepasDemandeDto> listDto = new ArrayList<TitreRepasDemandeDto>();
		listDto.add(dto);
		listDto.add(dto2);
		listDto.add(dto3);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		Integer noMatr = dto.getAgent().getIdAgent() - 9000000;
		Integer noMatr2 = dto2.getAgent().getIdAgent() - 9000000;
		Integer noMatr3 = dto3.getAgent().getIdAgent() - 9000000;

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto2.getAgent().getIdAgent())).thenReturn(noMatr2);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto3.getAgent().getIdAgent())).thenReturn(noMatr3);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		AffectationDto aff2 = new AffectationDto();
		aff2.setBaseConge(baseConge);
		aff2.setIdAgent(dto2.getAgent().getIdAgent());

		AffectationDto aff3 = new AffectationDto();
		aff3.setBaseConge(baseConge);
		aff3.setIdAgent(dto3.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);
		listAffectation.add(aff2);
		listAffectation.add(aff3);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(
				sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()),
						dateDebutMois, dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(
				absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois,
						dateFinMois)).thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr2, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr3, dateDebutMois, dateFinMois)).thenReturn(listPA);

		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(noMatr3, dateDebutMois, dateFinMois)).thenReturn("I");

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		IAccessRightsService accessRightsService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightsService.isUserApprobateur(idAgentConnecte)).thenReturn(true);
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(false);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, listDto);

		assertEquals(1, result.getErrors().size());
		assertEquals(2, result.getInfos().size());
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_OK);
		assertEquals(result.getInfos().get(1), TitreRepasService.ENREGISTREMENT_OK);
		assertEquals(result.getErrors().get(0), TitreRepasService.FILIERE_INCENDIE);
		Mockito.verify(titreRepasRepository, Mockito.times(2)).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void updateEtatForTitreRepasDemande_check() {

		Integer idAgentConnecte = 9005138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005131);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(true);
		dto.setCommentaire("commentaire 1");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		// test TitreRepasDemandeDto.IdTrDemande
		ReturnMessageDto result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), TitreRepasService.AUCUN_ID_DEMANDE);

		// test ETAT envoyé
		dto.setIdTrDemande(12);
		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), TitreRepasService.NOUVELLE_ETAT_INCORRECT);

		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande())).thenReturn(null);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		// test si demande existe en BDD
		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), TitreRepasService.AUCUNE_DEMANDE_TROUVEE);
	}

	@Test
	public void updateEtatForTitreRepasDemande_checkBis() {

		Integer idAgentConnecte = 9005138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005131);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(false);
		dto.setCommentaire("commentaire 1");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());
		dto.setIdTrDemande(12);

		TitreRepasDemande demandeTR = Mockito.spy(new TitreRepasDemande());
		demandeTR.setDateMonth(new DateTime(2015, 8, 1, 0, 0, 0).toDate());
		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setCommande(false);
		demandeTR.getEtats().add(etat);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande())).thenReturn(demandeTR);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		// test le mois de la demande si mois courant
		ReturnMessageDto result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), TitreRepasService.DEMANDE_MOIS_EN_COURS_ERROR);

		// test si la commande de TR est à TRUE
		demandeTR.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), TitreRepasService.DEMANDE_NON_COMMANDE);

		TitreRepasEtatDemande etatApprouve = new TitreRepasEtatDemande();
		etatApprouve.setEtat(EtatPointageEnum.APPROUVE);
		demandeTR.getEtats().add(etatApprouve);
		TitreRepasEtatDemande etatRefuse = new TitreRepasEtatDemande();
		etatRefuse.setCommande(true);
		etatRefuse.setEtat(EtatPointageEnum.REFUSE);
		demandeTR.getEtats().clear();
		demandeTR.getEtats().add(etatRefuse);

		// test l ETAT de la demande de TR
		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE, EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatRefuse.getEtat().name()));

		TitreRepasEtatDemande etatJournalise = new TitreRepasEtatDemande();
		etatJournalise.setEtat(EtatPointageEnum.JOURNALISE);
		etatJournalise.setCommande(true);
		demandeTR.getEtats().clear();
		demandeTR.getEtats().add(etatJournalise);

		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE, EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatJournalise.getEtat().name()));

		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());
		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE, EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatJournalise.getEtat().name()));

		TitreRepasEtatDemande etatRejete = new TitreRepasEtatDemande();
		etatRejete.setEtat(EtatPointageEnum.REJETE);
		etatRejete.setCommande(true);
		demandeTR.getEtats().clear();
		demandeTR.getEtats().add(etatRejete);

		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE, EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatRejete.getEtat().name()));
	}

	@Test
	public void updateEtatForTitreRepasDemande_ok() {

		Integer idAgentConnecte = 9005138;
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005131);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		dto.setCommande(false);
		dto.setCommentaire("commentaire 1");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());
		dto.setIdTrDemande(12);

		TitreRepasDemande demandeTR = Mockito.spy(new TitreRepasDemande());
		demandeTR.setDateMonth(new DateTime(2015, 8, 1, 0, 0, 0).toDate());

		TitreRepasEtatDemande etatSaisi = new TitreRepasEtatDemande();
		etatSaisi.setEtat(EtatPointageEnum.SAISI);
		demandeTR.getEtats().add(etatSaisi);
		demandeTR.getLatestEtatTitreRepasDemande().setCommande(true);
		demandeTR.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande())).thenReturn(demandeTR);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		// on approuve
		ReturnMessageDto result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().size(), 0);
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_OK);

		assertEquals(demandeTR.getEtats().get(1).getEtat(), EtatPointageEnum.APPROUVE);
		assertEquals(demandeTR.getEtats().get(1).getIdAgent(), idAgentConnecte);
		assertEquals(demandeTR.getEtats().get(1).getCommentaire(), dto.getCommentaire());
		assertTrue(demandeTR.getEtats().get(1).getCommande());

		// on rejette
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());
		dto.setCommentaire("commentaire 2");

		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().size(), 0);
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_OK);

		assertEquals(demandeTR.getEtats().get(2).getEtat(), EtatPointageEnum.REJETE);
		assertEquals(demandeTR.getEtats().get(2).getIdAgent(), idAgentConnecte);
		assertEquals(demandeTR.getEtats().get(2).getCommentaire(), "commentaire 2");
		assertTrue(demandeTR.getEtats().get(2).getCommande());
	}

	@Test
	public void getListRefEtats() {

		List<RefEtat> listRefEtat = new ArrayList<RefEtat>();
		for (int i = 0; i < 10; i++) {
			RefEtat etat = new RefEtat();
			etat.setIdRefEtat(EtatPointageEnum.getEtatPointageEnum(i).getCodeEtat());
			etat.setLabel(EtatPointageEnum.getEtatPointageEnum(i).name());
			listRefEtat.add(etat);
		}

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.findAllRefEtats()).thenReturn(listRefEtat);

		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		List<RefEtatDto> result = service.getListRefEtats();

		assertEquals(4, result.size());
		assertEquals(result.get(0).getLibelle(), EtatPointageEnum.SAISI.name());
		assertEquals(result.get(1).getLibelle(), EtatPointageEnum.APPROUVE.name());
		assertEquals(result.get(2).getLibelle(), EtatPointageEnum.REJETE.name());
		assertEquals(result.get(3).getLibelle(), EtatPointageEnum.JOURNALISE.name());
	}

	@Test
	public void getListTitreRepasDemandeDto_errorDroit() {

		Integer idAgentConnecte = 9005138;
		Date fromDate = null;
		Date toDate = null;
		Integer etat = null;
		Boolean commande = null;
		Date dateMonth = null;
		Integer idServiceADS = null;
		Integer idAgent = null;

		IAccessRightsService accessRightService = Mockito.mock(IAccessRightsService.class);
		Mockito.when(accessRightService.canUserAccessVisualisation(idAgentConnecte)).thenReturn(false);

		ReturnMessageDto rmdSIRH = new ReturnMessageDto();
		rmdSIRH.getErrors().add("error");
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(rmdSIRH);

		ReflectionTestUtils.setField(service, "accessRightService", accessRightService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		try {
			service.getListTitreRepasDemandeDto(idAgentConnecte, fromDate, toDate, etat, commande, dateMonth, idServiceADS, idAgent, null, false);
		} catch (AccessForbiddenException e) {
			return;
		}
		fail();
	}

	@Test
	public void getListeMoisTitreRepasSaisie_PlusieursMois() {
		List<Date> list = new ArrayList<Date>();
		DateTime dateMonth = new DateTime(2014, 12, 1, 0, 0, 0);
		DateTime dateMonth2 = new DateTime(2014, 11, 1, 0, 0, 0);
		list.add(dateMonth.toDate());
		list.add(dateMonth2.toDate());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListeMoisTitreRepasSaisie()).thenReturn(list);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		List<Date> result = service.getListeMoisTitreRepasSaisie();

		assertEquals(2, result.size());
		assertEquals(dateMonth.toDate(), result.get(0));
		assertEquals(dateMonth2.toDate(), result.get(1));
	}

	@Test
	public void getListeMoisTitreRepasSaisie_ZeroMois() {

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListeMoisTitreRepasSaisie()).thenReturn(new ArrayList<Date>());

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		List<Date> result = service.getListeMoisTitreRepasSaisie();

		assertEquals(0, result.size());
	}
}
