package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.junit.Test;
import org.mockito.Mockito;

public class VentilPrimeDtoTest {

    @Test
    public void ctor_with_VentilPrime() {

        int idVentil = 987654321;
        RefPrime ref = new RefPrime();
        int libRef = 321654987;
        ref.setIdRefPrime(libRef);
        ref.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
        int idAgent = 9005138;
        double qte = 100.0;
        // Given
        VentilPrime vp = new VentilPrime();
        vp.setRefPrime(ref);
        vp.setIdVentilPrime(idVentil);
        vp.setQuantite(qte);
        vp.setDateDebutMois(new Date(System.currentTimeMillis()));
        vp.setEtat(EtatPointageEnum.SAISI);
        vp.setIdAgent(idAgent);

        HelperService hS = Mockito.mock(HelperService.class);
        
        // When
        VentilPrimeDto result = new VentilPrimeDto(vp, hS);

        // Then
        assertEquals(idAgent, result.getIdAgent());
        assertEquals(true, result.getDateDebutMois() != null);
        assertEquals(libRef, result.getIdRefPrime());
        assertEquals(idVentil, result.getIdVentilPrime());
        assertEquals("100.0", result.getQuantite());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
    
    @Test
    public void ctor_with_VentilPrime_NB_HEURES() {

        int idVentil = 987654321;
        RefPrime ref = new RefPrime();
        int libRef = 321654987;
        ref.setIdRefPrime(libRef);
        ref.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
        int idAgent = 9005138;
        Double qte = 150.0;
        // Given
        VentilPrime vp = new VentilPrime();
        vp.setRefPrime(ref);
        vp.setIdVentilPrime(idVentil);
        vp.setQuantite(qte);
        vp.setDateDebutMois(new Date(System.currentTimeMillis()));
        vp.setEtat(EtatPointageEnum.SAISI);
        vp.setIdAgent(idAgent);

        HelperService hS = Mockito.mock(HelperService.class);
        Mockito.when(hS.formatMinutesToString(qte.intValue())).thenReturn("2h30m");
        
        // When
        VentilPrimeDto result = new VentilPrimeDto(vp, hS);
        
        // Then
        assertEquals(idAgent, result.getIdAgent());
        assertEquals(true, result.getDateDebutMois() != null);
        assertEquals(libRef, result.getIdRefPrime());
        assertEquals(idVentil, result.getIdVentilPrime());
        assertEquals("2h30m", result.getQuantite());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
    
    @Test
    public void ctor_with_VentilPrime_PERIODE_HEURES() {

        int idVentil = 987654321;
        RefPrime ref = new RefPrime();
        int libRef = 321654987;
        ref.setIdRefPrime(libRef);
        ref.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
        int idAgent = 9005138;
        Double qte = 45.0;
        // Given
        VentilPrime vp = new VentilPrime();
        vp.setRefPrime(ref);
        vp.setIdVentilPrime(idVentil);
        vp.setQuantite(qte);
        vp.setDateDebutMois(new Date(System.currentTimeMillis()));
        vp.setEtat(EtatPointageEnum.SAISI);
        vp.setIdAgent(idAgent);

        HelperService hS = Mockito.mock(HelperService.class);
        Mockito.when(hS.formatMinutesToString(qte.intValue())).thenReturn("45m");
        
        // When
        VentilPrimeDto result = new VentilPrimeDto(vp, hS);

        // Then
        assertEquals(idAgent, result.getIdAgent());
        assertEquals(true, result.getDateDebutMois() != null);
        assertEquals(libRef, result.getIdRefPrime());
        assertEquals(idVentil, result.getIdVentilPrime());
        assertEquals("45m", result.getQuantite());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
}
