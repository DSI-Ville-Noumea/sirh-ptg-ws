package nc.noumea.mairie.titreRepas.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefGroupeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.SpadmnId;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spchge;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Spperm;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatPayeurTask;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.reporting.EtatPayeurTitreRepasReporting;
import nc.noumea.mairie.ptg.reporting.EtatPrestataireTitreRepasReporting;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasEtatPayeurTaskDto;
import nc.noumea.mairie.titreRepas.repository.ITitreRepasRepository;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

public class TitreRepasServiceTest {

	private TitreRepasService			service		= new TitreRepasService();

	private RefTypeSaisiCongeAnnuelDto	baseConge	= new RefTypeSaisiCongeAnnuelDto();

	@Before
	public void setting() {
		baseConge.setIdRefTypeSaisiCongeAnnuel(1);
		baseConge.setDecompteSamedi(true);
		baseConge.setCodeBaseHoraireAbsence("C");
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

		RefGroupeAbsenceDto groupeAbsenceMaladies = new RefGroupeAbsenceDto();
		groupeAbsenceMaladies.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.MALADIES.getValue());
		
		DemandeDto demandeMaladies = new DemandeDto();
		demandeMaladies.setAgentWithServiceDto(agent);
		demandeMaladies.setDateDebut(new DateTime(2015, 10, 24, 0, 0, 0).toDate());
		demandeMaladies.setDateFin(new DateTime(2015, 10, 31, 23, 59, 59).toDate());
		demandeMaladies.setGroupeAbsence(groupeAbsenceMaladies);
		listAbences.add(demandeMaladies);

		ReflectionTestUtils.setField(service, "helperService", helperService);

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

		RefGroupeAbsenceDto groupeAbsenceMaladies = new RefGroupeAbsenceDto();
		groupeAbsenceMaladies.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.MALADIES.getValue());
		
		DemandeDto demandeMaladies = new DemandeDto();
		demandeMaladies.setAgentWithServiceDto(agent);
		demandeMaladies.setDateDebut(new DateTime(2015, 10, 25, 0, 0, 0).toDate());
		demandeMaladies.setDateFin(new DateTime(2015, 10, 31, 23, 59, 59).toDate());
		demandeMaladies.setGroupeAbsence(groupeAbsenceMaladies);
		listAbences.add(demandeMaladies);

		ReflectionTestUtils.setField(service, "helperService", helperService);

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

		RefGroupeAbsenceDto groupeAbsenceMaladies = new RefGroupeAbsenceDto();
		groupeAbsenceMaladies.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.MALADIES.getValue());
		
		DemandeDto demandeMaladies = new DemandeDto();
		demandeMaladies.setAgentWithServiceDto(agent);
		demandeMaladies.setDateDebut(new DateTime(2015, 10, 26, 0, 0, 0).toDate());
		demandeMaladies.setDateFin(new DateTime(2015, 10, 31, 23, 59, 59).toDate());
		demandeMaladies.setGroupeAbsence(groupeAbsenceMaladies);
		listAbences.add(demandeMaladies);

		ReflectionTestUtils.setField(service, "helperService", helperService);

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

		RefGroupeAbsenceDto groupeAbsenceMaladies = new RefGroupeAbsenceDto();
		groupeAbsenceMaladies.setIdRefGroupeAbsence(RefTypeGroupeAbsenceEnum.MALADIES.getValue());
		
		DemandeDto demandeMaladies = new DemandeDto();
		demandeMaladies.setAgentWithServiceDto(agent);
		demandeMaladies.setDateDebut(new DateTime(2015, 10, 27, 0, 0, 0).toDate());
		demandeMaladies.setDateFin(new DateTime(2015, 10, 31, 23, 59, 59).toDate());
		demandeMaladies.setGroupeAbsence(groupeAbsenceMaladies);
		listAbences.add(demandeMaladies);

		ReflectionTestUtils.setField(service, "helperService", helperService);
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

		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(5138, fromDate, toDate)).thenReturn("A");

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertFalse(service.checkAgentIsFiliereIncendie(idAgent, fromDate, toDate));
	}

	@Test
	public void checkAgentIsFiliereIncendie_true() {

		Integer idAgent = 9005138;

		Date fromDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(5138, fromDate, toDate)).thenReturn("I");

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		assertTrue(service.checkAgentIsFiliereIncendie(idAgent, fromDate, toDate));
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
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuivant);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(null);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois,
				dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
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

		Date dateDebutMoisSuiv = new DateTime(2015, 11, 1, 0, 0, 0).toDate();

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
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
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois,
				dateFinMois)).thenReturn(listAffectation);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuiv);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
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
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(Mockito.anyListOf(Integer.class), Mockito.any(Date.class), Mockito.any(Date.class),
				Mockito.any(Integer.class), Mockito.any(boolean.class), Mockito.any(Date.class))).thenReturn(listTitreRepasDemande);

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
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuivant);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois,
				dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
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
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuivant);
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
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois,
				dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
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

		TitreRepasEtatDemande etatREjete = new TitreRepasEtatDemande();
		etatREjete.setEtat(EtatPointageEnum.REJETE);

		tr.getEtats().clear();
		tr.getEtats().add(etatREjete);

		result = service.enregistreListTitreDemandeFromKiosque(idAgentConnecte, Arrays.asList(dto));

		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.MODIFICATION_IMPOSSIBLE_DEMANDE_AUTRE_SAISI_DEPUIS_KIOSQUE);
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
		Date dateDebutMoisSuiv = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois,
				dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);

		TitreRepasEtatDemande etatJournalise = new TitreRepasEtatDemande();
		etatJournalise.setEtat(EtatPointageEnum.SAISI);
		etatJournalise.setCommande(true);

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
		dto.setCommande(true);
		dto.setCommentaire("commentaire");
		dto.setAgent(ag);
		dto.setIdRefEtat(EtatPointageEnum.SAISI.getCodeEtat());

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date dateDebutMoisSuiv = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateFinMois);
		Mockito.when(helperService.getMairieMatrFromIdAgent(dto.getAgent().getIdAgent())).thenReturn(noMatr);

		AffectationDto aff = new AffectationDto();
		aff.setBaseConge(baseConge);
		aff.setIdAgent(dto.getAgent().getIdAgent());

		List<AffectationDto> listAffectation = new ArrayList<AffectationDto>();
		listAffectation.add(aff);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois)).thenReturn(new ArrayList<JourDto>());
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois,
				dateFinMois)).thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(Arrays.asList(dto.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
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
		Date dateDebutMoisSuiv = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		Integer noMatr = dto.getAgent().getIdAgent() - 9000000;
		Integer noMatr2 = dto2.getAgent().getIdAgent() - 9000000;
		Integer noMatr3 = dto3.getAgent().getIdAgent() - 9000000;

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuiv);
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
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(
				Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(
				Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr2, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr3, dateDebutMois, dateFinMois)).thenReturn(listPA);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.enregistreListTitreDemandeFromSIRH(idAgentConnecte, listDto);

		assertEquals(0, result.getErrors().size());
		assertEquals(3, result.getInfos().size());
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_OK);
		assertEquals(result.getInfos().get(1), TitreRepasService.ENREGISTREMENT_OK);
		assertEquals(result.getInfos().get(2), TitreRepasService.ENREGISTREMENT_OK);
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
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		Integer noMatr = dto.getAgent().getIdAgent() - 9000000;
		Integer noMatr2 = dto2.getAgent().getIdAgent() - 9000000;
		Integer noMatr3 = dto3.getAgent().getIdAgent() - 9000000;

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuivant);
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
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(
				Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(
				Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr2, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr3, dateDebutMois, dateFinMois)).thenReturn(listPA);

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

		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals(result.getInfos().get(0), TitreRepasService.ENREGISTREMENT_PLURIEL_OK);
		Mockito.verify(titreRepasRepository, Mockito.times(3)).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void enregistreListTitreDemandeFromKiosque_errorBaseConge() {

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
		Date dateDebutMoisSuiv = new DateTime(2015, 11, 1, 0, 0, 0).toDate();
		Date dateFinMois = new DateTime(2015, 10, 31, 0, 0, 0).toDate();

		Integer noMatr = dto.getAgent().getIdAgent() - 9000000;
		Integer noMatr2 = dto2.getAgent().getIdAgent() - 9000000;
		Integer noMatr3 = dto3.getAgent().getIdAgent() - 9000000;

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuiv);
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
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(
				Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(listAffectation);

		RefTypeAbsenceDto refTypeAbsence = new RefTypeAbsenceDto();
		refTypeAbsence.setTypeSaisiCongeAnnuelDto(baseConge);
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(refTypeAbsence);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListAbsencesForListAgentsBetween2Dates(
				Arrays.asList(dto.getAgent().getIdAgent(), dto2.getAgent().getIdAgent(), dto3.getAgent().getIdAgent()), dateDebutMois, dateFinMois))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);

		Spadmn pa = new Spadmn();
		pa.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(pa);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr2, dateDebutMois, dateFinMois)).thenReturn(listPA);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(noMatr3, dateDebutMois, dateFinMois)).thenReturn(listPA);

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

		assertEquals(3, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.BASE_CONGE_C);
		Mockito.verify(titreRepasRepository, Mockito.times(0)).persist(Mockito.isA(TitreRepasDemande.class));
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
		assertEquals(result.getErrors().get(0), TitreRepasService.TITRE_DEMANDE_INEXISTANT);
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
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(new DateTime(2015, 10, 1, 0, 0, 0).toDate()))
				.thenReturn(new DateTime(2015, 11, 1, 0, 0, 0).toDate());

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		// test le mois de la demande si mois courant
		ReturnMessageDto result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), TitreRepasService.TITRE_DEMANDE_INEXISTANT);

		// test si la commande de TR est à TRUE
		demandeTR.setDateMonth(new DateTime(2015, 11, 1, 0, 0, 0).toDate());
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
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE,
				EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatRefuse.getEtat().name()));

		TitreRepasEtatDemande etatJournalise = new TitreRepasEtatDemande();
		etatJournalise.setEtat(EtatPointageEnum.JOURNALISE);
		etatJournalise.setCommande(true);
		demandeTR.getEtats().clear();
		demandeTR.getEtats().add(etatJournalise);

		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE,
				EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatJournalise.getEtat().name()));

		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());
		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE,
				EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatJournalise.getEtat().name()));

		TitreRepasEtatDemande etatRejete = new TitreRepasEtatDemande();
		etatRejete.setEtat(EtatPointageEnum.REJETE);
		etatRejete.setCommande(true);
		demandeTR.getEtats().clear();
		demandeTR.getEtats().add(etatRejete);

		result = service.updateEtatForTitreRepasDemande(idAgentConnecte, dto);
		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.ERROR_ETAT_DEMANDE,
				EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), etatRejete.getEtat().name()));
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
		demandeTR.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());

		TitreRepasEtatDemande etatSaisi = new TitreRepasEtatDemande();
		etatSaisi.setEtat(EtatPointageEnum.SAISI);
		demandeTR.getEtats().add(etatSaisi);
		demandeTR.getLatestEtatTitreRepasDemande().setCommande(true);
		demandeTR.setDateMonth(new DateTime(2015, 10, 1, 0, 0, 0).toDate());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande())).thenReturn(demandeTR);

		Date dateDebutMois = new DateTime(2015, 10, 1, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 9, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(new DateTime(2015, 9, 1, 0, 0, 0).toDate()))
				.thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());

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
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new Date());
		ReflectionTestUtils.setField(service, "helperService", helperService);

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

	@Test
	public void enregistreTitreDemandeOneByOne_checkEtat() {
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9005138);
		TitreRepasDemandeDto dto = new TitreRepasDemandeDto();
		dto.setAgent(agent);
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		RefTypeSaisiCongeAnnuelDto base = new RefTypeSaisiCongeAnnuelDto();

		Date fromDate = new DateTime(2015, 9, 1, 0, 0, 0).toDate();
		Date toDate = new DateTime(2015, 9, 30, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2015, 10, 1, 0, 0, 0).toDate();

		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(agent.getIdAgent());

		Spadmn paInactiveBis = new Spadmn();
		paInactiveBis.setCdpadm("01");

		List<Spadmn> listPA = new ArrayList<Spadmn>();
		listPA.add(paInactiveBis);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListeMoisTitreRepasSaisie()).thenReturn(new ArrayList<Date>());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 11, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(Mockito.any(Date.class))).thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());

		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(dateDebutMoisSuivant);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(2015, 9, 1, 0, 0, 0).toDate())).thenReturn(fromDate);
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(2015, 9, 1, 0, 0, 0).toDate())).thenReturn(toDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, fromDate, toDate)).thenReturn(listPA);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		ReturnMessageDto result = service.enregistreTitreDemandeOneByOne(9005138, dto, new ArrayList<DemandeDto>(), base, new ArrayList<JourDto>(),
				aff, true);

		assertEquals(0, result.getErrors().size());
		Mockito.verify(titreRepasRepository, Mockito.times(1)).persist(Mockito.isA(TitreRepasDemande.class));
	}

	@Test
	public void genereEtatPayeur_NoSIRHUser() {
		Integer idAgent = 9005138;

		ReturnMessageDto erreuUser = new ReturnMessageDto();
		erreuUser.getErrors().add("bad user");

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(erreuUser);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.ERREUR_DROIT_AGENT);
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
	}

	@Test
	public void genereEtatPayeur_EtatDejaGenere() {
		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 02, 25, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2016, 03, 01, 0, 0, 0).toDate();

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(true);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurByMonth(dateDebutMoisSuivant)).thenReturn(new TitreRepasEtatPayeur());

		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.GENERATION_EXIST);
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
	}

	@Test
	public void genereEtatPayeur_EtatDejaGenere_avecTaskOK() {
		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 02, 25, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2016, 03, 01, 0, 0, 0).toDate();

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(true);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurByMonth(dateDebutMoisSuivant)).thenReturn(null);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurTaskByMonthAndStatus(dateDebutMoisSuivant, "OK"))
				.thenReturn(new TitreRepasExportEtatPayeurTask());

		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.GENERATION_EXIST);
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
	}

	@Test
	public void genereEtatPayeur_PaieEnCours() {
		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 02, 25, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2016, 03, 01, 0, 0, 0).toDate();

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(true);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurByMonth(dateDebutMoisSuivant)).thenReturn(null);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurTaskByMonthAndStatus(dateDebutMoisSuivant, "OK")).thenReturn(null);

		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.PAIE_EN_COURS);
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
	}

	@Test
	public void genereEtatPayeur_Avant_11() {
		Integer idAgent = 9005138;
		Date currentDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(false);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(currentDate);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.GENERATION_IMPOSSIBLE_AVANT_11);
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
	}

	@Test
	public void genereEtatPayeur_DemandeSaisieExistantes() {
		Integer idAgent = 9005138;
		Date currentDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date currentDate2 = new DateTime(2015, 10, 11, 0, 0, 0).toDate();

		List<TitreRepasDemande> listDemandeTr = new ArrayList<TitreRepasDemande>();
		listDemandeTr.add(new TitreRepasDemande());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(currentDate2);
		Mockito.when(helperService.getDatePremierJourOfMonth(currentDate2)).thenReturn(currentDate);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(currentDate2)).thenReturn(currentDate);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(null, null, null, EtatPointageEnum.SAISI.getCodeEtat(), true, currentDate))
				.thenReturn(listDemandeTr);

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(false);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), TitreRepasService.DEMANDE_EN_COURS);
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
	}

	@Test
	public void genereEtatPayeur_EcartDonnees() {
		Integer idAgent = 9005138;
		Date currentDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date currentDate2 = new DateTime(2015, 10, 11, 0, 0, 0).toDate();

		AgentWithServiceDto ag1 = new AgentWithServiceDto();
		ag1.setIdAgent(idAgent);
		AgentWithServiceDto ag2 = new AgentWithServiceDto();
		ag2.setIdAgent(9002990);

		TitreRepasDemande tr1 = new TitreRepasDemande();
		tr1.setIdAgent(idAgent);
		tr1.setDateMonth(currentDate);
		tr1.setCommande(true);

		TitreRepasDemande tr2 = new TitreRepasDemande();
		tr2.setIdAgent(9002990);
		tr2.setDateMonth(currentDate);
		tr2.setCommande(true);

		List<TitreRepasDemande> listeTr = new ArrayList<>();
		listeTr.add(tr1);
		listeTr.add(tr2);

		Spcarr carr = new Spcarr();
		carr.setCdcate(1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(currentDate2);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(currentDate2)).thenReturn(currentDate);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(currentDate);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(null, null, null, EtatPointageEnum.SAISI.getCodeEtat(), true, currentDate))
				.thenReturn(new ArrayList<TitreRepasDemande>());
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(null, null, null, EtatPointageEnum.APPROUVE.getCodeEtat(), true, currentDate))
				.thenReturn(listeTr);

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(new ArrayList<AgentWithServiceDto>(), idAgent)).thenReturn(ag1);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(new ArrayList<AgentWithServiceDto>(), 9002990)).thenReturn(ag2);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(carr);

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(false);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());
		Mockito.when(sirhWsConsumer.getListAgentsWithService(Arrays.asList(idAgent, 9002990), currentDate2))
				.thenReturn(new ArrayList<AgentWithServiceDto>());

		EtatPayeurTitreRepasReporting reportingTitreRepasPayeurService = Mockito.mock(EtatPayeurTitreRepasReporting.class);
		EtatPrestataireTitreRepasReporting reportingTitreRepasPrestataireService = Mockito.mock(EtatPrestataireTitreRepasReporting.class);

		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "reportingTitreRepasPayeurService", reportingTitreRepasPayeurService);
		ReflectionTestUtils.setField(service, "reportingTitreRepasPrestataireService", reportingTitreRepasPrestataireService);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(1, result.getErrors().size());
		assertEquals(result.getErrors().get(0), "Il n'y a pas le même nombre d'agents entre le nombre de demande et le resultat des agents avec services.");
		Mockito.verify(mairieRepository, Mockito.never()).mergeEntity(Mockito.isA(Spchge.class));
		Mockito.verify(mairieRepository, Mockito.never()).persistEntity(Mockito.isA(Spmatr.class));
	}

	@Test
	public void genereEtatPayeur_ok() {
		Integer idAgent = 9005138;
		Date currentDate = new DateTime(2015, 10, 1, 0, 0, 0).toDate();
		Date currentDate2 = new DateTime(2015, 10, 11, 0, 0, 0).toDate();

		AgentWithServiceDto ag1 = new AgentWithServiceDto();
		ag1.setIdAgent(idAgent);
		AgentWithServiceDto ag2 = new AgentWithServiceDto();
		ag2.setIdAgent(9002990);

		TitreRepasDemande tr1 = new TitreRepasDemande();
		tr1.setIdAgent(idAgent);
		tr1.setDateMonth(currentDate);
		tr1.setCommande(true);

		TitreRepasDemande tr2 = new TitreRepasDemande();
		tr2.setIdAgent(9002990);
		tr2.setDateMonth(currentDate);
		tr2.setCommande(true);

		List<TitreRepasDemande> listeTr = new ArrayList<>();
		listeTr.add(tr1);
		listeTr.add(tr2);

		Spcarr carr = new Spcarr();
		carr.setCdcate(1);
		
		Spperm refPrime = new Spperm();
		
		
		List<AgentWithServiceDto> listAgWithService=new ArrayList<AgentWithServiceDto>();
		listAgWithService.add(new AgentWithServiceDto());
		listAgWithService.add(new AgentWithServiceDto());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(currentDate2);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(currentDate2)).thenReturn(currentDate);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(Mockito.any(Date.class))).thenReturn(currentDate);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(null, null, null, EtatPointageEnum.SAISI.getCodeEtat(), true, currentDate))
				.thenReturn(new ArrayList<TitreRepasDemande>());
		Mockito.when(titreRepasRepository.getListTitreRepasDemande(null, null, null, EtatPointageEnum.APPROUVE.getCodeEtat(), true, currentDate))
				.thenReturn(listeTr);

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(new ArrayList<AgentWithServiceDto>(), idAgent)).thenReturn(ag1);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(new ArrayList<AgentWithServiceDto>(), 9002990)).thenReturn(ag2);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(carr);
		Mockito.when(mairieRepository.getTREtatPayeurRates(Mockito.any(Date.class))).thenReturn(refPrime);

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(false);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());
		Mockito.when(sirhWsConsumer.getListAgentsWithService(Arrays.asList(idAgent, 9002990), currentDate2))
				.thenReturn(listAgWithService);

		EtatPayeurTitreRepasReporting reportingTitreRepasPayeurService = Mockito.mock(EtatPayeurTitreRepasReporting.class);
		EtatPrestataireTitreRepasReporting reportingTitreRepasPrestataireService = Mockito.mock(EtatPrestataireTitreRepasReporting.class);

		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "reportingTitreRepasPayeurService", reportingTitreRepasPayeurService);
		ReflectionTestUtils.setField(service, "reportingTitreRepasPrestataireService", reportingTitreRepasPrestataireService);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		ReturnMessageDto result = service.genereEtatPayeur(idAgent);

		assertEquals(0, result.getErrors().size());
		Mockito.verify(mairieRepository, Mockito.times(2)).mergeEntity(Mockito.isA(Spchge.class));
		Mockito.verify(mairieRepository, Mockito.times(2)).persistEntity(Mockito.isA(Spmatr.class));
	}

	@Test
	public void checkAffichageMenuTitreRepasAgent_NoAffectationActive_ko() {

		Integer idAgent = 9005138;
		Date dateJour = new Date();

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateJour, dateJour))
				.thenReturn(new ArrayList<AffectationDto>());

		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		boolean result = service.checkAffichageMenuTitreRepasAgent(idAgent, dateJour);
		assertFalse(result);

	}

	@Test
	public void checkAffichageMenuTitreRepasAgent_NoAffectationPrec_ko() {

		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 01, 05, 8, 0, 0).toDate();
		Date dateDebMoisSuiv = new DateTime(2015, 12, 01, 0, 0, 0).toDate();
		Date dateFinMoisSuiv = new DateTime(2015, 12, 31, 23, 59, 59).toDate();

		List<AffectationDto> listAffPrec = new ArrayList<AffectationDto>();
		List<AffectationDto> listAff = new ArrayList<AffectationDto>();
		listAff.add(new AffectationDto());

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonthPrecedent(dateJour)).thenReturn(dateDebMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonthPrecedent(dateJour)).thenReturn(dateFinMoisSuiv);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateJour, dateJour))
				.thenReturn(listAff);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateDebMoisSuiv, dateFinMoisSuiv))
				.thenReturn(listAffPrec);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		boolean result = service.checkAffichageMenuTitreRepasAgent(idAgent, dateJour);
		assertFalse(result);

	}

	@Test
	public void checkAffichageMenuTitreRepasAgent_PrimePanier_ko() {

		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 01, 05, 8, 0, 0).toDate();
		Date dateDebMoisSuiv = new DateTime(2015, 12, 01, 0, 0, 0).toDate();
		Date dateFinMoisSuiv = new DateTime(2015, 12, 31, 23, 59, 59).toDate();

		RefPrimeDto prime = new RefPrimeDto();
		prime.setNumRubrique(7704);
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.getListPrimesAff().add(prime);
		List<AffectationDto> listAff = new ArrayList<AffectationDto>();
		listAff.add(aff);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonthPrecedent(dateJour)).thenReturn(dateDebMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonthPrecedent(dateJour)).thenReturn(dateFinMoisSuiv);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateJour, dateJour))
				.thenReturn(listAff);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateDebMoisSuiv, dateFinMoisSuiv))
				.thenReturn(listAff);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		boolean result = service.checkAffichageMenuTitreRepasAgent(idAgent, dateJour);
		assertFalse(result);

	}

	@Test
	public void checkAffichageMenuTitreRepasAgent_BaseC_ko() {

		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 01, 05, 8, 0, 0).toDate();
		Date dateDebMoisSuiv = new DateTime(2015, 12, 01, 0, 0, 0).toDate();
		Date dateFinMoisSuiv = new DateTime(2015, 12, 31, 23, 59, 59).toDate();

		RefTypeSaisiCongeAnnuelDto baseConge = new RefTypeSaisiCongeAnnuelDto();
		baseConge.setCodeBaseHoraireAbsence("C");
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);
		List<AffectationDto> listAff = new ArrayList<AffectationDto>();
		listAff.add(aff);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonthPrecedent(dateJour)).thenReturn(dateDebMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonthPrecedent(dateJour)).thenReturn(dateFinMoisSuiv);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateJour, dateJour))
				.thenReturn(listAff);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateDebMoisSuiv, dateFinMoisSuiv))
				.thenReturn(listAff);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		boolean result = service.checkAffichageMenuTitreRepasAgent(idAgent, dateJour);
		assertFalse(result);

	}

	@Test
	public void checkAffichageMenuTitreRepasAgent_FiliereIncendie_ko() {

		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 01, 05, 8, 0, 0).toDate();
		Date dateDebMoisSuiv = new DateTime(2015, 12, 01, 0, 0, 0).toDate();
		Date dateFinMoisSuiv = new DateTime(2015, 12, 31, 23, 59, 59).toDate();

		RefTypeSaisiCongeAnnuelDto baseConge = new RefTypeSaisiCongeAnnuelDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);
		List<AffectationDto> listAff = new ArrayList<AffectationDto>();
		listAff.add(aff);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonthPrecedent(dateJour)).thenReturn(dateDebMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonthPrecedent(dateJour)).thenReturn(dateFinMoisSuiv);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateJour, dateJour))
				.thenReturn(listAff);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateDebMoisSuiv, dateFinMoisSuiv))
				.thenReturn(listAff);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(5138, dateDebMoisSuiv, dateFinMoisSuiv)).thenReturn("I");

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		boolean result = service.checkAffichageMenuTitreRepasAgent(idAgent, dateJour);
		assertFalse(result);

	}

	@Test
	public void checkAffichageMenuTitreRepasAgent_OK() {

		Integer idAgent = 9005138;
		Date dateJour = new DateTime(2016, 01, 05, 8, 0, 0).toDate();
		Date dateDebMoisSuiv = new DateTime(2015, 12, 01, 0, 0, 0).toDate();
		Date dateFinMoisSuiv = new DateTime(2015, 12, 31, 23, 59, 59).toDate();

		RefTypeSaisiCongeAnnuelDto baseConge = new RefTypeSaisiCongeAnnuelDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);
		List<AffectationDto> listAff = new ArrayList<AffectationDto>();
		listAff.add(aff);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDatePremierJourOfMonthPrecedent(dateJour)).thenReturn(dateDebMoisSuiv);
		Mockito.when(helperService.getDateDernierJourOfMonthPrecedent(dateJour)).thenReturn(dateFinMoisSuiv);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateJour, dateJour))
				.thenReturn(listAff);
		Mockito.when(sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateDebMoisSuiv, dateFinMoisSuiv))
				.thenReturn(listAff);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getDerniereFiliereOfAgentOnPeriod(5138, dateDebMoisSuiv, dateFinMoisSuiv)).thenReturn("B");

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		boolean result = service.checkAffichageMenuTitreRepasAgent(idAgent, dateJour);
		assertTrue(result);

	}

	@Test
	public void checkBaseCongeCSurAffectation_false() {

		Integer idAgent = 9005138;
		RefTypeSaisiCongeAnnuelDto baseConge = new RefTypeSaisiCongeAnnuelDto();
		baseConge.setCodeBaseHoraireAbsence("A");
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);

		assertFalse(service.checkBaseCongeCSurAffectation(aff, idAgent));
	}

	@Test
	public void checkBaseCongeCSurAffectation_true() {

		Integer idAgent = 9005138;
		RefTypeSaisiCongeAnnuelDto baseConge = new RefTypeSaisiCongeAnnuelDto();
		baseConge.setCodeBaseHoraireAbsence("C");
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);

		assertTrue(service.checkBaseCongeCSurAffectation(aff, idAgent));
	}

	@Test
	public void checkDateJourBetween1OfMonthAndGeneration_ok() {

		ReturnMessageDto rmd = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 22, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(new DateTime(2015, 10, 22, 0, 0, 0).toDate()))
				.thenReturn(new DateTime(2015, 11, 01, 0, 0, 0).toDate());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurByMonth(new DateTime(2015, 11, 01, 0, 0, 0).toDate())).thenReturn(null);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		rmd = service.checkDateJourBetween11OfMonthAndGeneration(rmd, true);

		assertEquals(0, rmd.getErrors().size());
	}

	@Test
	public void checkDateJourBetween11OfMonthAndGeneration_ko() {

		ReturnMessageDto rmd = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 10, 0, 0, 0).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);

		rmd = service.checkDateJourBetween11OfMonthAndGeneration(rmd, true);

		assertEquals(TitreRepasService.DATE_ETAT_NON_COMPRISE_ENTRE_11_ET_EDITION_PAYEUR, rmd.getErrors().get(0));
	}

	@Test
	public void checkDateJourBetween11OfMonthAndGeneration_koBis() {

		ReturnMessageDto rmd = new ReturnMessageDto();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new DateTime(2015, 10, 22, 0, 0, 0).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(new DateTime(2015, 10, 22, 0, 0, 0).toDate()))
				.thenReturn(new DateTime(2015, 11, 01, 0, 0, 0).toDate());

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurByMonth(new DateTime(2015, 11, 01, 0, 0, 0).toDate()))
				.thenReturn(new TitreRepasEtatPayeur());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		rmd = service.checkDateJourBetween11OfMonthAndGeneration(rmd, true);

		assertEquals(TitreRepasService.EDITION_PAYEUR_DEJA_EDITEE, rmd.getErrors().get(0));
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
	public void checkDroitATitreRepas_NoPA_returnError() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("05");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 10, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 10, 31, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 10, 31, 23, 59, 59).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMonth, null, null, null, null, false);

		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
		assertEquals(result.getInfos().size(), 0);
	}

	@Test
	public void checkDroitATitreRepas_NoPA_returnInfo() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("05");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 10, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 10, 31, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 10, 31, 23, 59, 59).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMonth, null, null, null, null, true);

		assertEquals(result.getInfos().get(0), String.format(TitreRepasService.AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
		assertEquals(result.getErrors().size(), 0);
	}

	@Test
	public void checkDroitATitreRepas_NoBaseConge_returnError() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Date dateMoisSuivant = new DateTime(dateMonth).plusMonths(1).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("01");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 9, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 9, 30, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 30, 23, 59, 59).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMoisSuivant, null, null, null, null, false);

		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.AUCUNE_BASE_CONGE, idAgent));
		assertEquals(result.getInfos().size(), 0);
	}

	@Test
	public void checkDroitATitreRepas_NoBaseConge_returnInfo() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Date dateMoisSuivant = new DateTime(dateMonth).plusMonths(1).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("01");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 9, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 9, 30, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 30, 23, 59, 59).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMoisSuivant, null, null, null, null, true);

		assertEquals(result.getInfos().get(0), String.format(TitreRepasService.AUCUNE_BASE_CONGE, idAgent));
		assertEquals(result.getErrors().size(), 0);
	}

	@Test
	public void checkDroitATitreRepas_WithAbsence_returnError() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("01");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);
		baseConge.setCodeBaseHoraireAbsence("A");

		RefGroupeAbsenceDto groupeAbsence = new RefGroupeAbsenceDto();
		groupeAbsence.setIdRefGroupeAbsence(3);
		AgentWithServiceDto agentWithServiceDto = new AgentWithServiceDto();
		agentWithServiceDto.setIdAgent(idAgent);
		DemandeDto demandeDto = new DemandeDto();
		demandeDto.setAgentWithServiceDto(agentWithServiceDto);
		demandeDto.setGroupeAbsence(groupeAbsence);
		List<DemandeDto> listAbsences = new ArrayList<>();
		listAbsences.add(demandeDto);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 10, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 10, 31, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 10, 31, 23, 59, 59).toDate());
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).toDate()))
				.thenReturn(new DateTime(2015, 10, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).toDate()))
				.thenReturn(new DateTime(2015, 10, 31, 23, 59, 59).toDate());
		
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMonth, listAbsences, baseConge, null, null, false);

		assertEquals(result.getErrors().get(0), String.format(TitreRepasService.AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
		assertEquals(result.getInfos().size(), 0);
	}

	@Test
	public void checkDroitATitreRepas_BaseCongeC_returnError() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Date dateMoisSuivant = new DateTime(dateMonth).plusMonths(1).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("01");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 9, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 9, 30, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 30, 23, 59, 59).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMoisSuivant, null, baseConge, null, aff, false);

		assertEquals(result.getErrors().get(0), TitreRepasService.BASE_CONGE_C);
		assertEquals(result.getInfos().size(), 0);
	}

	@Test
	public void checkDroitATitreRepas_BaseCongeC_returnInfo() {

		ReturnMessageDto result = new ReturnMessageDto();
		Integer idAgent = 9005138;

		Date dateMonth = new DateTime(2015, 10, 01, 0, 0, 0).toDate();
		Date dateMoisSuivant = new DateTime(dateMonth).plusMonths(1).toDate();
		Spadmn spadmn = new Spadmn();
		spadmn.setCdpadm("01");
		List<Spadmn> listSpAdmn = new ArrayList<>();
		listSpAdmn.add(spadmn);
		AffectationDto aff = new AffectationDto();
		aff.setIdAgent(idAgent);
		aff.setBaseConge(baseConge);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(5138, new DateTime(2015, 9, 1, 0, 0, 0).toDate(),
				new DateTime(2015, 9, 30, 23, 59, 59).toDate())).thenReturn(listSpAdmn);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(5138);
		Mockito.when(helperService.getDatePremierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 1, 0, 0, 0).toDate());
		Mockito.when(helperService.getDateDernierJourOfMonth(new DateTime(dateMonth).minusMonths(1).toDate()))
				.thenReturn(new DateTime(2015, 9, 30, 23, 59, 59).toDate());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		result = service.checkDroitATitreRepas(result, idAgent, dateMoisSuivant, null, baseConge, null, aff, true);

		assertEquals(result.getInfos().get(0), TitreRepasService.BASE_CONGE_C);
		assertEquals(result.getErrors().size(), 0);
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
	public void isEtatPayeurRunning_ReturnTrue() {

		Date dateJour = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurTaskByMonthAndStatus(dateDebutMoisSuivant, null))
				.thenReturn(new TitreRepasExportEtatPayeurTask());

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		assertTrue(service.isEtatPayeurRunning());
	}

	@Test
	public void isEtatPayeurRunning_ReturnFalse() {

		Date dateJour = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurTaskByMonthAndStatus(dateDebutMoisSuivant, null)).thenReturn(null);

		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);

		assertFalse(service.isEtatPayeurRunning());
	}

	@Test
	public void startEtatPayeurTitreRepas_OK() {
		Integer idAgent = 9005138;

		Date dateJour = new DateTime(2015, 10, 22, 0, 0, 0).toDate();
		Date dateDebutMoisSuivant = new DateTime(2015, 11, 1, 0, 0, 0).toDate();

		IPaieWorkflowService paieWorkflowService = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(paieWorkflowService.isCalculSalaireEnCours()).thenReturn(false);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isUtilisateurSIRH(idAgent)).thenReturn(new ReturnMessageDto());

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurByMonth(dateDebutMoisSuivant)).thenReturn(null);
		Mockito.when(titreRepasRepository.getTitreRepasEtatPayeurTaskByMonthAndStatus(dateDebutMoisSuivant, "OK")).thenReturn(null);

		ReflectionTestUtils.setField(service, "paieWorkflowService", paieWorkflowService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		ReturnMessageDto result = service.startEtatPayeurTitreRepas(idAgent);

		assertNotNull(result);
		assertEquals(0, result.getErrors().size());
		Mockito.verify(titreRepasRepository, Mockito.times(1))
				.persisTitreRepasExportEtatPayeurTask(Mockito.isA(TitreRepasExportEtatPayeurTask.class));
	}

	@Test
	public void getListTitreRepasTaskErreur_NoResult() {
		Date dateJour = new DateTime(2015, 10, 22, 0, 0, 0).toDate();

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasTaskErreur()).thenReturn(new ArrayList<TitreRepasExportEtatPayeurTask>());

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		List<TitreRepasEtatPayeurTaskDto> result = service.getListTitreRepasTaskErreur();

		assertEquals(0, result.size());
	}

	@Test
	public void getListTitreRepasTaskErreur_Result() {
		Date dateJour = new DateTime(2015, 10, 22, 0, 0, 0).toDate();

		AgentWithServiceDto agDto1 = new AgentWithServiceDto();
		agDto1.setIdAgent(9002990);
		agDto1.setNom("TOTO");
		agDto1.setService("service");
		agDto1.setIdServiceADS(11);

		TitreRepasExportEtatPayeurTask task = new TitreRepasExportEtatPayeurTask();
		task.setIdAgent(9002990);
		task.setDateMonth(new DateTime(2015, 11, 01, 0, 0, 0).toDate());
		task.setDateExport(dateJour);
		task.setTaskStatus("Erreur");

		List<TitreRepasExportEtatPayeurTask> listErr = new ArrayList<TitreRepasExportEtatPayeurTask>();
		listErr.add(task);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);

		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);
		Mockito.when(titreRepasRepository.getListTitreRepasTaskErreur()).thenReturn(listErr);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getAgentService(task.getIdAgent(), dateJour)).thenReturn(agDto1);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		List<TitreRepasEtatPayeurTaskDto> result = service.getListTitreRepasTaskErreur();

		assertEquals(1, result.size());
		assertEquals(new Integer(9002990), result.get(0).getIdAgent());
		assertEquals(dateJour, result.get(0).getDateExport());
		assertEquals(new DateTime(2015, 11, 01, 0, 0, 0).toDate(), result.get(0).getDateMonth());
		assertEquals("Erreur", result.get(0).getErreur());
		assertEquals("TOTO", result.get(0).getAgent().getNom());
	}

	@Test
	public void checkPAMoisPrecedent_true() {
		
		Spadmn pa1 = new Spadmn();
		SpadmnId pa1Id = new SpadmnId();
		Spadmn pa2 = new Spadmn();
		SpadmnId pa2Id = new SpadmnId();
		
		// Première PA
		pa1Id.setDatdeb(20150101);
		pa1Id.setNomatr(5421);
		pa1.setId(pa1Id);
		pa1.setDatfin(20170306);
		pa1.setCdpadm("01");    	// Activité normale  	=> L'agent a le droit aux TR
		
		// Deuxième PA
		pa2Id.setDatdeb(20170307);
		pa2Id.setNomatr(5421);
		pa2.setId(pa2Id);
		pa2.setDatfin(0);
		pa2.setCdpadm("41");    	// Accident du travail  => L'agent n'a pas le droit aux TR sur le mois d'avril uniquement (il a travaillé la première semaine de mars)
		
		List<Spadmn> listPA = Lists.newArrayList();
		listPA.add(pa1);
		listPA.add(pa2);
		
		// Base de congé
		RefTypeSaisiCongeAnnuelDto bc = new RefTypeSaisiCongeAnnuelDto();
		bc.setCodeBaseHoraireAbsence("A");

		// Dto du titre repas
		TitreRepasDemandeDto trDto = new TitreRepasDemandeDto();
		trDto.setAgent(new AgentWithServiceDto());
		trDto.getAgent().setIdAgent(5421);
		trDto.setIdRefEtat(1);
		
		// Dates
		Date dateDebutMoisPrecedent = new DateTime(2017, 03, 01, 0, 0, 0).toDate();				// 1e mars
		Date dateFinMoisPrecedent = new DateTime(2017, 03, 31, 0, 0, 0).toDate();				// 31 mars

		Date dateDebutMois = new DateTime(2017, 04, 01, 0, 0, 0).toDate();						// 1e avril
		Date dateJour = new DateTime(2017, 04, 04, 0, 0, 0).toDate();							// 4 avril
		
		Date dateDebutMoisSuivant = new DateTime(2017, 05, 01, 0, 0, 0).toDate();				// 1e mai

		// Mock datas
		HelperService helperService = Mockito.mock(HelperService.class);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getMairieMatrFromIdAgent(5421)).thenReturn(9005421);
		
		// TODO : Méthode précédente : getDatePremierJourOfMonthSuivant(), à changer par getDatePremierJourOfMonth dans TitreRepasService, l.350
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateJour)).thenReturn(dateDebutMois);
		
		Mockito.when(helperService.getDatePremierJourOfMonth(dateDebutMoisPrecedent)).thenReturn(dateDebutMoisPrecedent);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateDebutMoisPrecedent)).thenReturn(dateFinMoisPrecedent);
		
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(9005421, dateDebutMoisPrecedent, dateFinMoisPrecedent)).thenReturn(listPA);
		

		ReturnMessageDto message = service.enregistreTitreDemandeOneByOne(9005555, trDto, null, bc, null, null, false);
		
		assertEquals(message.getErrors().size(), 0);
		assertEquals(message.getInfos().size(), 1);
		assertEquals(message.getInfos().get(0), "La demande est bien enregistrée.");
	}

	
	@Test
	public void checkPAMoisPrecedent_false() {
		
		Spadmn pa1 = new Spadmn();
		SpadmnId pa1Id = new SpadmnId();
		
		// Deuxième PA
		pa1Id.setDatdeb(20170207);
		pa1Id.setNomatr(5421);
		pa1.setId(pa1Id);
		pa1.setDatfin(0);
		pa1.setCdpadm("41");    	// Accident du travail  => L'agent n'a pas le droit aux TR
		
		List<Spadmn> listPA = Lists.newArrayList();
		listPA.add(pa1);
		
		// Base de congé
		RefTypeSaisiCongeAnnuelDto bc = new RefTypeSaisiCongeAnnuelDto();
		bc.setCodeBaseHoraireAbsence("A");

		// Dto du titre repas
		TitreRepasDemandeDto trDto = new TitreRepasDemandeDto();
		trDto.setAgent(new AgentWithServiceDto());
		trDto.getAgent().setIdAgent(5421);
		trDto.setIdRefEtat(1);
		
		// Dates
		Date dateDebutMois = new DateTime(2017, 04, 01, 0, 0, 0).toDate();						// 1e avril
		Date dateJour = new DateTime(2017, 04, 04, 0, 0, 0).toDate();							// 4 avril
		Date datefinMois = new DateTime(2017, 04, 30, 0, 0, 0).toDate();						// 30 avril

		Date dateDebutMoisSuivant = new DateTime(2017, 05, 01, 0, 0, 0).toDate();				// 1e mai

		// Mock datas
		HelperService helperService = Mockito.mock(HelperService.class);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		ITitreRepasRepository titreRepasRepository = Mockito.mock(ITitreRepasRepository.class);

		ReflectionTestUtils.setField(service, "titreRepasRepository", titreRepasRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		Mockito.when(helperService.getMairieMatrFromIdAgent(5421)).thenReturn(9005421);
		
		Mockito.when(helperService.getDatePremierJourOfMonthSuivant(dateJour)).thenReturn(dateDebutMoisSuivant);
		Mockito.when(helperService.getDatePremierJourOfMonth(dateJour)).thenReturn(dateDebutMois);
		
		Mockito.when(helperService.getDatePremierJourOfMonth(dateDebutMois)).thenReturn(dateDebutMois);
		Mockito.when(helperService.getDateDernierJourOfMonth(dateDebutMois)).thenReturn(datefinMois);
		
		Mockito.when(mairieRepository.getListPAOfAgentBetween2Date(9005421, dateDebutMois, datefinMois)).thenReturn(listPA);

		ReturnMessageDto message = service.enregistreTitreDemandeOneByOne(9005555, trDto, null, bc, null, null, false);
		
		assertEquals(message.getErrors().size(), 1);
		assertEquals(message.getErrors().get(0), "L'agent 5421 n'a pas travaillé le mois précédent.");
		assertEquals(message.getInfos().size(), 0);
	}

}
