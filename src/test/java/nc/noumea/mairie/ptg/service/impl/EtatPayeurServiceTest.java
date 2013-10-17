package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.repository.IEtatPayeurRepository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class EtatPayeurServiceTest {
	
	@Test
	public void getEtatPayeurByIdEtatPayeur(){
		// Given
		Date d = new LocalDate(2013, 10, 17).toDate();
		
		EtatPayeur etatPayeur = new EtatPayeur();
		etatPayeur.setIdEtatPayeur(new Integer(1));
		etatPayeur.setDateEtatPayeur(d);
		etatPayeur.setFichier("testUnit.pdf");
		etatPayeur.setLabel("test unitaire");
		etatPayeur.setStatut(AgentStatutEnum.C);
		RefTypePointage refType = new RefTypePointage();
		refType.setIdRefTypePointage(new Integer(1));
		etatPayeur.setType(refType);
		
		Integer idEtatPayeur = new Integer(1);
		
		IEtatPayeurRepository repo = Mockito.mock(IEtatPayeurRepository.class);
		Mockito.when(repo.getEtatPayeurById(idEtatPayeur)).thenReturn(etatPayeur);
		
		// When
		EtatPayeur result = null;
		EtatPayeurService service = new EtatPayeurService();
		ReflectionTestUtils.setField(service, "etatPayeurRepository", repo);
		
		result = service.getEtatPayeurByIdEtatPayeur(idEtatPayeur);
		
		// Then
		assertEquals(idEtatPayeur, result.getIdEtatPayeur());
		assertEquals(d, result.getDateEtatPayeur());
		assertEquals("testUnit.pdf", result.getFichier());
		assertEquals("test unitaire", result.getLabel());
		assertEquals(AgentStatutEnum.C, result.getStatut());
		assertEquals(new Integer(1), result.getType().getIdRefTypePointage());
	}
	
	@Test
	public void getListEtatsPayeurByStatut(){
		// Given
		Date d1 = new LocalDate(2013, 10, 17).toDate();
		Date d2 = new LocalDate(2013, 10, 18).toDate();
		
		List<EtatPayeur> lstEP = new ArrayList<EtatPayeur>();
		
		EtatPayeur ep1 = new EtatPayeur();
		ep1.setIdEtatPayeur(new Integer(1));
		ep1.setDateEtatPayeur(d1);
		ep1.setFichier("testUnit.pdf");
		ep1.setLabel("test unitaire");
		ep1.setStatut(AgentStatutEnum.C);
		RefTypePointage refType = new RefTypePointage();
		refType.setIdRefTypePointage(new Integer(1));
		ep1.setType(refType);
		
		EtatPayeur ep2 = new EtatPayeur();
		ep2.setIdEtatPayeur(new Integer(2));
		ep2.setDateEtatPayeur(d2);
		ep2.setFichier("testUnit2.pdf");
		ep2.setLabel("test unitaire 2");
		ep2.setStatut(AgentStatutEnum.C);
		RefTypePointage refType2 = new RefTypePointage();
		refType2.setIdRefTypePointage(new Integer(2));
		ep2.setType(refType2);
		
		lstEP.add(ep1);
		lstEP.add(ep2);
		
		// When
		IEtatPayeurRepository repo = Mockito.mock(IEtatPayeurRepository.class);
		Mockito.when(repo.getListEditionEtatPayeur(AgentStatutEnum.C)).thenReturn(lstEP);
		
		List<ListEtatsPayeurDto> result = null;
		EtatPayeurService service = new EtatPayeurService();
		ReflectionTestUtils.setField(service, "etatPayeurRepository", repo);
		
		result = service.getListEtatsPayeurByStatut(AgentStatutEnum.C);
		
		// Then
		assertEquals(2, result.size());
		assertEquals(new Integer(1), result.get(0).getIdEtatPayeur());
		assertEquals(d1, result.get(0).getDateEtatPayeur());
		assertEquals("testUnit.pdf", result.get(0).getFichier());
		assertEquals("test unitaire", result.get(0).getLabel());
		assertEquals("C", result.get(0).getStatut());
		assertEquals(new Integer(1), result.get(0).getType());
		
		assertEquals(new Integer(2), result.get(1).getIdEtatPayeur());
		assertEquals(d2, result.get(1).getDateEtatPayeur());
		assertEquals("testUnit2.pdf", result.get(1).getFichier());
		assertEquals("test unitaire 2", result.get(1).getLabel());
		assertEquals("C", result.get(1).getStatut());
		assertEquals(new Integer(2), result.get(1).getType());
		
	}

}
