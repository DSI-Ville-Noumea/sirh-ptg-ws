package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.repository.IEtatPayeurRepository;
import nc.noumea.mairie.ptg.service.IEtatPayeurService;
import nc.noumea.mairie.ptg.web.EtatsPayeurController;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class EtatPayeurServiceTest {
	
	private Logger logger = LoggerFactory.getLogger(EtatsPayeurController.class);
	
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
		Pair<String, String> result = null;
		EtatPayeurService service = new EtatPayeurService();
		ReflectionTestUtils.setField(service, "etatPayeurRepository", repo);
		
		try {
			result = service.getPathFichierEtatPayeur(idEtatPayeur);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		
		// Then
		//assertNotNull(result.getLeft());
		assertEquals("testUnit.pdf", result.getRight());
	}
}
