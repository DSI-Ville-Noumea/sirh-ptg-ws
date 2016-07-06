package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;

import org.junit.Test;

public class VentilAbsenceDtoTest {

    @Test
    public void ctor_with_VentilAbsence() {


        int idAgent = 9005138;
        int minC = 100;
        int minNC = 200;
        // Given
        VentilAbsence va = new VentilAbsence();
        va.setMinutesConcertee(minC);
        va.setDateLundi(new Date(System.currentTimeMillis()));
        va.setEtat(EtatPointageEnum.SAISI);
        va.setIdAgent(idAgent);
        va.setIdVentilAbsence(123123);
        va.setMinutesNonConcertee(minNC);

        // When
        VentilAbsenceDto result = new VentilAbsenceDto(va);

        // Then
        assertEquals(idAgent, result.getIdAgent());
        assertEquals(true, result.getDateLundi() != null);
        assertEquals(minC, result.getMinutesConcertees());
        assertEquals(minNC, result.getMinutesNonConcertees());
        assertEquals(EtatPointageEnum.SAISI, EtatPointageEnum.getEtatPointageEnum(result.getEtat()));
    }
}
