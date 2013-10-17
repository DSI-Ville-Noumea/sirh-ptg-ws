package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import nc.noumea.mairie.domain.AgentStatutEnum;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_ETAT_PAYEUR", identifierField = "idEtatPayeur", identifierType = Integer.class, table = "PTG_ETAT_PAYEUR", sequenceName = "PTG_S_ETAT_PAYEUR")
@NamedQueries({
	@NamedQuery(name = "getListEditionsEtatPayeurByStatut", query = "select ptg from Pointage ptg JOIN FETCH ptg.type where ptg.statut = :statut order by ptg.dateEtatPayeur desc"),
	@NamedQuery(name = "getEtatPayeurById", query = "select ptg from Pointage ptg JOIN FETCH ptg.type where ptg.idEtatPayeur = :idEtatPayeur")
})
public class EtatPayeur {
	
	@Column(name = "STATUT")
	@Enumerated(EnumType.STRING)
	private AgentStatutEnum statut;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "ID_TYPE_POINTAGE")
	private RefTypePointage type;

	@Column(name = "DATE_ETAT_PAYEUR")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEtatPayeur;
	
	@Column(name = "LABEL", columnDefinition = "NVARCHAR2")
	private String label;
	
	@Column(name = "FICHIER", columnDefinition = "NVARCHAR2")
	private String fichier;
}
