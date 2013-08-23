package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_VENTIL_ABSENCE", identifierField = "idVentilAbsence", identifierType = Integer.class, table = "PTG_VENTIL_ABSENCE", sequenceName = "PTG_S_VENTIL_ABSENCE")
public class VentilAbsence {

    @Column(name = "ID_AGENT")
    private Integer idAgent;
    @Column(name = "DATE_LUNDI")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLundi;
    @Column(name = "MINUTES_CONCERTEE")
    private int minutesConcertee;
    @Column(name = "MINUTES_NON_CONCERTEE")
    private int minutesNonConcertee;
    @Column(name = "ETAT")
    @Enumerated(EnumType.ORDINAL)
    private EtatPointageEnum etat;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDate;

    public void addMinutesConcertee(Integer minutes) {
        minutesConcertee += minutes;
    }

    public void addMinutesNonConcertee(Integer minutes) {
        minutesNonConcertee += minutes;
    }
}
