package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "R_TYPE_JOUR_FERIE")
public class TypeJourFerie {

	@Id
	@Column(name = "ID_TYPE_JOUR_FERIE")
	@NotNull
	private Integer idTypeJourFerie;
	
	@NotNull
	@Column(name = "LIB_TYPE_JOUR_FERIE")
	private String libelle;

	public Integer getIdTypeJourFerie() {
		return idTypeJourFerie;
	}

	public void setIdTypeJourFerie(Integer idTypeJourFerie) {
		this.idTypeJourFerie = idTypeJourFerie;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	
}
