package nc.noumea.mairie.titreRepas.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefGroupeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TitreRepasServiceTest {

	private TitreRepasService service = new TitreRepasService();
	
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
		Date dateMonth = new DateTime(2015,10,22,0,0,0).toDate();
		
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
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
		Date dateMonth = new DateTime(2015,10,22,0,0,0).toDate();
		
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsence = new RefGroupeAbsenceDto();
		groupeAbsence.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demande = new DemandeDto();
		demande.setAgentWithServiceDto(agent);
		demande.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demande.setDateFin(new DateTime(2015,11,22,0,0,0).toDate());
		demande.setGroupeAbsence(groupeAbsence);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demande);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend

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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsence = new RefGroupeAbsenceDto();
		groupeAbsence.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.AS.getValue());
		
		DemandeDto demande = new DemandeDto();
		demande.setAgentWithServiceDto(agent);
		demande.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demande.setDateFin(new DateTime(2015,11,22,0,0,0).toDate());
		demande.setGroupeAbsence(groupeAbsence);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demande);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend

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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,19,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,30,0,0,0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend

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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,20,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,30,0,0,0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend

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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,19,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,30,0,0,0).toDate());
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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,18,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,30,0,0,0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend
		
		JourDto jourFerie = new JourDto();
		jourFerie.setFerie(true);
		jourFerie.setJour(new DateTime(2015,10,17,0,0,0).toDate());
		
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);
		Mockito.when(helperService.isJourHoliday(Arrays.asList(jourFerie), new DateTime(2015,10,17,0,0,0).toDate())).thenReturn(true);
		
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		
		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, Arrays.asList(jourFerie)));
	}
	
	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_2CongeToutLeMois_maisTravailUnJourFerie() {
		
		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,18,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,30,0,0,0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(false); // travaille donc le weekend
		
		JourDto jourFerie = new JourDto();
		jourFerie.setFerie(true);
		jourFerie.setJour(new DateTime(2015,10,17,0,0,0).toDate());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateMoisPrecedent)).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateMoisPrecedent)).thenReturn(toDate);
		Mockito.when(helperService.isJourHoliday(Arrays.asList(jourFerie), new DateTime(2015,10,17,0,0,0).toDate())).thenReturn(true);
		
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		
		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, Arrays.asList(jourFerie)));
	}
	
	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_2CongeToutLeMois_maisTravailDernierJourMoisSamedi() {
		
		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,17,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,30,0,0,0).toDate());
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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,17,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,31,0,0,0).toDate());
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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,17,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,23,0,0,0).toDate());
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
		
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015,10,24,0,0,0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015,10,31,0,0,0).toDate());
		
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		
		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}
	
	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_TravailWeekend_CongeEtMaladie() {
		
		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,17,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,23,0,0,0).toDate());
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
		
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015,10,25,0,0,0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015,10,31,0,0,0).toDate());
		
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		
		assertTrue(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}
	
	@Test
	public void checkUnJourSansAbsenceSurLeMois_False_PasDeTravailWeekend_CongeEtMaladie() {
		
		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,17,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,23,0,0,0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend

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
		
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015,10,26,0,0,0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015,10,31,0,0,0).toDate());
		
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		
		assertFalse(service.checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, null));
	}
	
	@Test
	public void checkUnJourSansAbsenceSurLeMois_True_PasDeTravailWeekend_CongeEtMaladie() {
		
		Integer idAgent = 9005138;
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(idAgent);
		
		RefGroupeAbsenceDto groupeAbsenceCA = new RefGroupeAbsenceDto();
		groupeAbsenceCA.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_ANNUELS.getValue());
		
		DemandeDto demandeCA = new DemandeDto();
		demandeCA.setAgentWithServiceDto(agent);
		demandeCA.setDateDebut(new DateTime(2015,9,22,0,0,0).toDate());
		demandeCA.setDateFin(new DateTime(2015,10,16,0,0,0).toDate());
		demandeCA.setGroupeAbsence(groupeAbsenceCA);
		
		RefGroupeAbsenceDto groupeAbsenceCE = new RefGroupeAbsenceDto();
		groupeAbsenceCE.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.CONGES_EXCEP.getValue());
		
		DemandeDto demandeCE = new DemandeDto();
		demandeCE.setAgentWithServiceDto(agent);
		demandeCE.setDateDebut(new DateTime(2015,10,17,0,0,0).toDate());
		demandeCE.setDateFin(new DateTime(2015,10,23,0,0,0).toDate());
		demandeCE.setGroupeAbsence(groupeAbsenceCE);
		
		List<DemandeDto> listAbences = new ArrayList<DemandeDto>();
		listAbences.add(demandeCA);
		listAbences.add(demandeCE);
		
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = new RefTypeSaisiCongeAnnuelDto();
		baseCongeAgent.setDecompteSamedi(true); // ne travaille donc pas le weekend

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
		
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb())).thenReturn(new DateTime(2015,10,27,0,0,0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(spabsen.getDatfin())).thenReturn(new DateTime(2015,10,31,0,0,0).toDate());
		
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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
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
		Date dateMoisPrecedent = new DateTime(2015,10,22,0,0,0).toDate();
		
		Date fromDate = new DateTime(2015,10,1,0,0,0).toDate();
		Date toDate = new DateTime(2015,10,31,0,0,0).toDate();
		
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
}
