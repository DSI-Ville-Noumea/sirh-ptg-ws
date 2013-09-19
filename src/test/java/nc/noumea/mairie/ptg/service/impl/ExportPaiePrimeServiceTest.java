package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.SppprmId;
import nc.noumea.mairie.domain.Spprim;
import nc.noumea.mairie.domain.SpprimId;
import nc.noumea.mairie.ptg.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportPaiePrimeServiceTest {

	private static RefTypePointage hSup;
	private static RefTypePointage pri;
	
	@BeforeClass
	public static void Setup() {
		hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		pri= new RefTypePointage();
		pri.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
	}
	
	@Test
	public void exportPrimesJourToPaie_3Pointages_1PrimeJour_CreateNewRecord() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setType(hSup);
		
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		Pointage p2 = new Pointage();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(2);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7750);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		Pointage p3 = new Pointage();
		p3.setRefPrime(rp2);
		p3.setType(pri);
		p3.setQuantite(4);
		
		List<Pointage> pointagesOrderedByDateAsc = Arrays.asList(p1, p2, p3);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_1Pointage_1PrimeJourNBHeures_CreateNewRecordWithNbHeuresFormatMairie() {
		
		// Given
		
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		Pointage p2 = new Pointage();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(135);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		List<Pointage> pointagesOrderedByDateAsc = Arrays.asList(p2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(135)).thenReturn(2.15d);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2.15d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_1Pointage_1PrimeJourPeriodeHeures_CreateNewRecordWithNbHeuresFormatMairie() {
		
		// Given
		
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		Pointage p2 = new Pointage();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(200);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		List<Pointage> pointagesOrderedByDateAsc = Arrays.asList(p2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(200)).thenReturn(3.20d);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(3.20d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_3Pointages_1PrimeJour_UpdateExistingRecord() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setType(hSup);
		
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		Pointage p2 = new Pointage();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(2);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7750);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		Pointage p3 = new Pointage();
		p3.setRefPrime(rp2);
		p3.setType(pri);
		p3.setQuantite(4);
		
		List<Pointage> pointagesOrderedByDateAsc = Arrays.asList(p1, p2, p3);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		
		Sppprm existingSppprm = new Sppprm();
		existingSppprm.setNbPrime(7);
		existingSppprm.setId(new SppprmId(9898, 20130809, 7701));
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(existingSppprm);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(existingSppprm, result.get(0));
		assertEquals(2d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_3Pointages_1PrimeJour_QuantiteIs0_DeleteExistingRecord() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setType(hSup);
		
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);
		Pointage p2 = new Pointage();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(0);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7750);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		Pointage p3 = new Pointage();
		p3.setRefPrime(rp2);
		p3.setType(pri);
		p3.setQuantite(4);
		
		List<Pointage> pointagesOrderedByDateAsc = Arrays.asList(p1, p2, p3);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		
		Sppprm existingSppprm = Mockito.spy(new Sppprm());
		Mockito.doNothing().when(existingSppprm).remove();
		existingSppprm.setNbPrime(7);
		existingSppprm.setId(new SppprmId(9898, 20130809, 7701));
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(existingSppprm);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(0, result.size());
		Mockito.verify(existingSppprm, Mockito.times(1)).remove();
	}
	
	@Test
	public void exportPrimesJourToPaie_3PointagesCalcules_1PrimeJour_CreateNewRecord() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		PointageCalcule p2 = new PointageCalcule();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(2);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		List<PointageCalcule> pointagesOrderedByDateAsc = Arrays.asList(p2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesCalculeesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_1PointagesCalcule_1PrimeJourNBHeures_CreateNewRecordWithNbHeuresFormatMairie() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		PointageCalcule p2 = new PointageCalcule();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(135);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		List<PointageCalcule> pointagesOrderedByDateAsc = Arrays.asList(p2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(135)).thenReturn(2.15d);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesCalculeesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2.15d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_1PointagesCalcule_1PrimeJourPeriodeHeures_CreateNewRecordWithNbHeuresFormatMairie() {
		
		// Given
		
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		PointageCalcule p2 = new PointageCalcule();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(200);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		List<PointageCalcule> pointagesOrderedByDateAsc = Arrays.asList(p2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(200)).thenReturn(3.20d);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesCalculeesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(3.20d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_2PointagesCalcules_1PrimeJour_UpdateExistingRecord() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		PointageCalcule p2 = new PointageCalcule();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(2);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7750);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		PointageCalcule p3 = new PointageCalcule();
		p3.setRefPrime(rp2);
		p3.setType(pri);
		p3.setQuantite(4);
		
		List<PointageCalcule> pointagesOrderedByDateAsc = Arrays.asList(p2, p3);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		
		Sppprm existingSppprm = new Sppprm();
		existingSppprm.setNbPrime(7);
		existingSppprm.setId(new SppprmId(9898, 20130809, 7701));
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(existingSppprm);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesCalculeesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(existingSppprm, result.get(0));
		assertEquals(2d, result.get(0).getNbPrime(), 0);
		assertEquals(20130809, (int) result.get(0).getId().getDatJour());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesJourToPaie_2PointagesCalcules_1PrimeJour_QuantiteIs0_DeleteExistingRecord() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);
		PointageCalcule p2 = new PointageCalcule();
		p2.setRefPrime(rp);
		p2.setType(pri);
		p2.setQuantite(0);
		p2.setIdAgent(9009898);
		p2.setDateDebut(new DateTime(2013, 8, 9, 19, 5, 23).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7750);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		PointageCalcule p3 = new PointageCalcule();
		p3.setRefPrime(rp2);
		p3.setType(pri);
		p3.setQuantite(4);
		
		List<PointageCalcule> pointagesOrderedByDateAsc = Arrays.asList(p2, p3);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130809);
		
		Sppprm existingSppprm = Mockito.spy(new Sppprm());
		Mockito.doNothing().when(existingSppprm).remove();
		existingSppprm.setNbPrime(7);
		existingSppprm.setId(new SppprmId(9898, 20130809, 7701));
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSppprmForDayAgentAndNorubr(9009898, p2.getDateDebut(), rp.getNoRubr())).thenReturn(existingSppprm);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Sppprm> result = service.exportPrimesCalculeesJourToPaie(pointagesOrderedByDateAsc);
		
		// Then
		assertEquals(0, result.size());
		Mockito.verify(existingSppprm, Mockito.times(1)).remove();
	}
	
	@Test
	public void exportPrimesMoisToPaie_2VentilPrime_1PrimeMois_Create1NewRecord() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		VentilPrime vp = new VentilPrime();
		vp.setRefPrime(rp);
		vp.setQuantite(2);
		vp.setIdAgent(9009898);
		vp.setDateDebutMois(new LocalDate(2013, 8, 1).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7701);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		VentilPrime vp2 = new VentilPrime();
		vp2.setRefPrime(rp2);
		vp2.setQuantite(2);
		vp2.setIdAgent(9009898);
		vp2.setDateDebutMois(new LocalDate(2013, 8, 1).toDate());
		
		List<VentilPrime> ventilPrimesByDateAsc = Arrays.asList(vp, vp2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(vp.getDateDebutMois())).thenReturn(20130801);
		Mockito.when(hS.getIntegerDateMairieFromDate(new LocalDate(2013, 9, 1).toDate())).thenReturn(20130901);
		
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSpprimForDayAgentAndNorubr(9009898, vp.getDateDebutMois(), rp.getNoRubr())).thenReturn(null);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Spprim> result = service.exportPrimesMoisToPaie(ventilPrimesByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2d, result.get(0).getMontantPrime(), 0);
		assertEquals(20130801, (int) result.get(0).getId().getDateDebut());
		assertEquals(20130901, (int) result.get(0).getDateFin());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesMoisToPaie_1VentilPrime_UpdateExistingRecord() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		VentilPrime vp = new VentilPrime();
		vp.setRefPrime(rp);
		vp.setQuantite(2);
		vp.setIdAgent(9009898);
		vp.setDateDebutMois(new LocalDate(2013, 8, 1).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7701);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		VentilPrime vp2 = new VentilPrime();
		vp2.setRefPrime(rp2);
		vp2.setQuantite(2);
		vp2.setIdAgent(9009898);
		vp2.setDateDebutMois(new LocalDate(2013, 8, 1).toDate());

		List<VentilPrime> ventilPrimesByDateAsc = Arrays.asList(vp, vp2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(vp.getDateDebutMois())).thenReturn(20130801);
		
		Spprim existingSpprim = new Spprim();
		existingSpprim.setMontantPrime(7);
		existingSpprim.setId(new SpprimId(9898, 20130801, 7701));
		existingSpprim.setDateFin(20130901);
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSpprimForDayAgentAndNorubr(9009898, vp.getDateDebutMois(), rp.getNoRubr())).thenReturn(existingSpprim);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Spprim> result = service.exportPrimesMoisToPaie(ventilPrimesByDateAsc);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(existingSpprim, result.get(0));
		assertEquals(2d, result.get(0).getMontantPrime(), 0);
		assertEquals(20130801, (int) result.get(0).getId().getDateDebut());
		assertEquals(20130901, (int) result.get(0).getDateFin());
		assertEquals(9898, (int) result.get(0).getId().getNomatr());
		assertEquals(7701, (int) result.get(0).getId().getNoRubr());
	}
	
	@Test
	public void exportPrimesMoisToPaie_1VentilPrime_MontantIs0_DeleteExistingRecord() {
		
		// Given
		RefPrime rp = new RefPrime();
		rp.setNoRubr(7701);
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		VentilPrime vp = new VentilPrime();
		vp.setRefPrime(rp);
		vp.setQuantite(0);
		vp.setIdAgent(9009898);
		vp.setDateDebutMois(new LocalDate(2013, 8, 1).toDate());
		
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7701);
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		VentilPrime vp2 = new VentilPrime();
		vp2.setRefPrime(rp2);
		vp2.setQuantite(2);
		vp2.setIdAgent(9009898);
		vp2.setDateDebutMois(new LocalDate(2013, 8, 1).toDate());

		List<VentilPrime> ventilPrimesByDateAsc = Arrays.asList(vp, vp2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009898)).thenReturn(9898);
		Mockito.when(hS.getIntegerDateMairieFromDate(vp.getDateDebutMois())).thenReturn(20130801);
		
		Spprim existingSpprim = Mockito.spy(new Spprim());
		Mockito.doNothing().when(existingSpprim).remove();
		existingSpprim.setMontantPrime(7);
		existingSpprim.setId(new SpprimId(9898, 20130801, 7701));
		existingSpprim.setDateFin(20130901);
		IExportPaieRepository epR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(epR.getSpprimForDayAgentAndNorubr(9009898, vp.getDateDebutMois(), rp.getNoRubr())).thenReturn(existingSpprim);
		
		ExportPaiePrimeService service = new ExportPaiePrimeService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", epR);
		
		// When
		List<Spprim> result = service.exportPrimesMoisToPaie(ventilPrimesByDateAsc);
		
		// Then
		assertEquals(0, result.size());
		Mockito.verify(existingSpprim, Mockito.times(1)).remove();
	}
}
