package nc.noumea.mairie.sirh.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "P_JOUR_FERIE") 
@NamedQuery(name = "isJourHoliday", query = "select 1 from JourFerie where dateJour = :date")
public class JourFerie {

	@Id
	@Column(name = "ID_JOUR_FERIE")
	@NotNull
	private Integer idJourFerie;
	
	@NotNull
	@Column(name = "DATE_JOUR")
	@Temporal(TemporalType.DATE)
	private Date dateJour;

	@Column(name = "DESCRIPTION")
	private String description;

	@NotNull
	@OneToOne
	@JoinColumn(name = "ID_TYPE_JOUR_FERIE", referencedColumnName = "ID_TYPE_JOUR_FERIE")
	private TypeJourFerie typeJour;

	public Integer getIdJourFerie() {
		return idJourFerie;
	}

	public void setIdJourFerie(Integer idJourFerie) {
		this.idJourFerie = idJourFerie;
	}

	public Date getDateJour() {
		return dateJour;
	}

	public void setDateJour(Date dateJour) {
		this.dateJour = dateJour;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TypeJourFerie getTypeJour() {
		return typeJour;
	}

	public void setTypeJour(TypeJourFerie typeJour) {
		this.typeJour = typeJour;
	}
	
	
}
