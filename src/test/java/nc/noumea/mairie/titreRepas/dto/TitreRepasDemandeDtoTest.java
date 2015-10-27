package nc.noumea.mairie.titreRepas.dto;

import static org.junit.Assert.*;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;

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
		
		TitreRepasDemande titreRepasDemande = new TitreRepasDemande();
		titreRepasDemande.setIdTrDemande(10);
		titreRepasDemande.setIdAgent(9005138);
		titreRepasDemande.setCommande(true);
		titreRepasDemande.setCommentaire("commentaire");
		titreRepasDemande.getEtats().add(etat);
		
		TitreRepasDemandeDto dto = new TitreRepasDemandeDto(titreRepasDemande);
		
		assertEquals(dto.getIdTrDemande(), titreRepasDemande.getIdTrDemande());
		assertEquals(dto.getIdAgent(), titreRepasDemande.getIdAgent());
		assertEquals(dto.getCommande(), titreRepasDemande.getCommande());
		assertEquals(dto.getCommentaire(), titreRepasDemande.getCommentaire());
		assertEquals(dto.getListEtats().get(0).getIdTrEtatDemande(), etat.getIdTrEtatDemande());
		assertEquals(dto.getListEtats().get(0).getIdAgent(), etat.getIdAgent());
		assertEquals(dto.getListEtats().get(0).getEtat().intValue(), etat.getEtat().getCodeEtat());
		assertEquals(dto.getListEtats().get(0).getDateMaj(), etat.getDateMaj());
		assertEquals(dto.getListEtats().get(0).getCommande(), etat.getCommande());
	}
}
