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
        int m_sup = 321644520;
        int m_sup_25 = 321644521;
        int m_sup_50 = 321644522;
        int m_abs = 3214521;
        int m_abs_as400 = 3214522;
        int mSimple = 2;
        int mComplementaires = 3;
        int mComposees = 4;
        int msdjf = 5;
        int msNuit = 6;

        // Given
        VentilHsup vh = new VentilHsup();
        vh.setEtat(EtatPointageEnum.SAISI);
        vh.setIdAgent(idAgent);
        vh.setIdVentilHSup(idVentil);
        vh.setDateLundi(new Date(System.currentTimeMillis()));
        vh.setMSimple(mSimple);
        vh.setMComplementaires(mComplementaires);
        vh.setMComposees(mComposees);
        vh.setMSup(m_sup);
        vh.setMSup25(m_sup_25);
        vh.setMSup50(m_sup_50);
        vh.setMAbsences(m_abs);
        vh.setMAbsencesAS400(m_abs_as400);
        vh.setMsdjf(msdjf);
        vh.setMsNuit(msNuit);
        // When
        VentilHSupDto result = new VentilHSupDto(vh);

        // Then
        assertEquals(idAgent, result.getIdAgent());
        assertEquals(true, result.getDateLundi() != null);
        assertEquals(mSimple, result.getmSimples());
        assertEquals(mComplementaires, result.getmComplementaires());
        assertEquals(mComposees, result.getmComposees());
        assertEquals(m_sup, result.getmSup());
        assertEquals(m_sup_25, result.getmSup25());
        assertEquals(m_sup_50, result.getmSup50());
        assertEquals(m_abs, result.getMabs());
        assertEquals(m_abs_as400, result.getMabsAs400());
        assertEquals(msdjf, result.getmDjf());
        assertEquals(msNuit, result.getmNuit());
        assertEquals(idVentil, result.getIdVentilHsup());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
}
