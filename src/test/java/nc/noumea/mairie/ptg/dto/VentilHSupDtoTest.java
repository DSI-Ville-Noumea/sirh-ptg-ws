package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
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
        assertEquals(idAgent, result.getIdAgent());
        assertEquals(true, result.getDateLundi() != null);
        assertEquals(m_sup_50, result.getmSup50());
        assertEquals(idVentil, result.getIdVentilHsup());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
}
