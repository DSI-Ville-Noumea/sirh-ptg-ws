package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.VentilHsup;

import org.junit.Test;

public class VentilHSupDtoTest {

    @Test
    public void ctor_with_VentilHSup() {

        int idVentil = 987654321;
        int idAgent = 9005138;
        int m_sup_50 = 321644521;

        // Given
        VentilHsup vh = new VentilHsup();
        vh.setEtat(EtatPointageEnum.SAISI);
        vh.setIdAgent(idAgent);
        vh.setIdVentilHSup(idVentil);
        vh.setDateLundi(new Date(System.currentTimeMillis()));
        vh.setMSup50(m_sup_50);
        // When
        VentilHSupDto result = new VentilHSupDto(vh);

        // Then
        assertEquals(idAgent, result.getId_agent());
        assertEquals(true, result.getDate_lundi() != null);
        assertEquals(m_sup_50, result.getM_sup_50());
        assertEquals(idVentil, result.getId_ventil_hsup());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
}
