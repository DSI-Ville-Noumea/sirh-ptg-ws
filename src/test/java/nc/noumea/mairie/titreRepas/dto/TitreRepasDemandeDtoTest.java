package nc.noumea.mairie.titreRepas.dto;

import static org.junit.Assert.*;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

import org.junit.Test;

public class TitreRepasDemandeDtoTest {

	@Test
	public void TitreRepasDemandeDto_constructor() {
		
		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setIdTrEtatDemande(13);
		etat.setIdAgent(9002990);
		etat.setEtat(EtatPointageEnum.SAISI);
		etat.setDateMaj(new Date());
		etat.setCommande(false);
		etat.setCommentaire("commentaire");
		
		TitreRepasDemande titreRepasDemande = new TitreRepasDemande();
		titreRepasDemande.setIdTrDemande(10);
		titreRepasDemande.setIdAgent(9005138);
		titreRepasDemande.setCommande(true);
		titreRepasDemande.getEtats().add(etat);
		
		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005138);
		
		TitreRepasDemandeDto dto = new TitreRepasDemandeDto(titreRepasDemande, ag);
		dto.updateEtat(etat, ag);
		
		assertEquals(dto.getIdTrDemande(), titreRepasDemande.getIdTrDemande());
		assertEquals(dto.getAgent().getIdAgent(), titreRepasDemande.getIdAgent());
		assertEquals(dto.getCommande(), titreRepasDemande.getCommande());
		assertEquals(dto.getCommentaire(), etat.getCommentaire());
	}
}
