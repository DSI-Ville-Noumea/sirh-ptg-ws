package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_VENTIL_ABSENCE")
@NamedQuery(name = "getPriorVentilAbsenceForAgentAndDate", query = "select va from VentilAbsence va where va.idVentilAbsence != :idLatestVentilAbsence and va.idAgent = :idAgent and va.dateLundi = :dateLundi order by va.idVentilAbsence desc")
public class VentilAbsence {

	@Id 
	@Column(name = "ID_VENTIL_ABSENCE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilAbsence;
	
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
