package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_VENTIL_PRIME", identifierField = "idVentilPrime", identifierType = Integer.class, table = "PTG_VENTIL_PRIME", sequenceName = "PTG_S_VENTIL_PRIME")
public class VentilPrime {

	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_DEBUT_MOIS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDebutMois;
	
	@ManyToOne
	@JoinColumn(name = "ID_REF_PRIME", referencedColumnName = "ID_REF_PRIME")
	private RefPrime refPrime;
	
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
	
	@Column(name = "QUANTITE")
	private Integer quantite;
	
	@Transient
	public void addQuantite(Integer quantite) {
		if(this.quantite == null)
			this.quantite = 0;
		this.quantite += quantite;
	}
}
