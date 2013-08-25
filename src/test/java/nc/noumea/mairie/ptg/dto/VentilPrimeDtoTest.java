package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.VentilPrime;

import org.junit.Test;

public class VentilPrimeDtoTest {

    @Test
    public void ctor_with_VentilPrime() {

        int idVentil = 987654321;
        RefPrime ref = new RefPrime();
        int libRef = 321654987;
        ref.setIdRefPrime(libRef);
        int idAgent = 9005138;
        int qte = 100;
        // Given
        VentilPrime vp = new VentilPrime();
        vp.setRefPrime(ref);
        vp.setIdVentilPrime(idVentil);
        vp.setQuantite(qte);
        vp.setDateDebutMois(new Date(System.currentTimeMillis()));
        vp.setEtat(EtatPointageEnum.SAISI);
        vp.setIdAgent(idAgent);

        // When
        VentilPrimeDto result = new VentilPrimeDto(vp);

        // Then
        assertEquals(idAgent, result.getId_agent());
        assertEquals(true, result.getDate_debut_mois() != null);
        assertEquals(libRef, result.getId_ref_prime());
        assertEquals(idVentil, result.getId_ventil_prime());
        assertEquals(qte, result.getQuantite());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
}
