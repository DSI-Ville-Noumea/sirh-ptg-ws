package nc.noumea.mairie.titreRepas.dto;

import static org.junit.Assert.assertEquals;

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
		titreRepasDemande.getEtats().add(etat);
		titreRepasDemande.setCommande(false);

		AgentWithServiceDto ag = new AgentWithServiceDto();
		ag.setIdAgent(9005138);

		TitreRepasDemandeDto dto = new TitreRepasDemandeDto(titreRepasDemande, ag);
		dto.updateEtat(etat, ag);

		assertEquals(dto.getIdTrDemande(), titreRepasDemande.getIdTrDemande());
		assertEquals(dto.getAgent().getIdAgent(), titreRepasDemande.getIdAgent());
		assertEquals(dto.getCommande(), etat.getCommande());
		assertEquals(dto.getCommentaire(), etat.getCommentaire());

		TitreRepasEtatDemande etat2 = new TitreRepasEtatDemande();
		etat2.setIdTrEtatDemande(99);
		etat2.setIdAgent(9002904);
		etat2.setEtat(EtatPointageEnum.APPROUVE);
		etat2.setDateMaj(new Date());
		etat2.setCommande(false);
		etat2.setCommentaire("commentaire 2");
		
		AgentWithServiceDto ope = new AgentWithServiceDto();
		ope.setIdAgent(9007894);
		
		dto.updateEtat(etat2, ope);

		assertEquals(dto.getCommande(), etat2.getCommande());
		assertEquals(dto.getCommentaire(), etat2.getCommentaire());
		assertEquals(dto.getIdRefEtat().intValue(), etat2.getEtat().getCodeEtat());
		assertEquals(dto.getDateSaisie(), etat2.getDateMaj());
		assertEquals(dto.getOperateur(), ope);
	}
}
