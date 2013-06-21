package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_VENTIL_ABSENCE", identifierField = "idVentilAbsence", identifierType = Integer.class, table = "PTG_VENTIL_ABSENCE", sequenceName = "PTG_S_VENTIL_ABSENCE")
public class VentilAbsence {

	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_DEBUT_MOIS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDebutMois;
	
	@Column(name = "QUANTITE_CONCERTEE")
	private Integer quantiteConcertee;

	@Column(name = "QUANTITE_NON_CONCERTEE")
	private Integer quantiteNonConcertee;

	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
}
