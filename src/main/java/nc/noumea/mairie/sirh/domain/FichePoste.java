package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FICHE_POSTE")
public class FichePoste {

	@Id
	@Column(name = "ID_FICHE_POSTE")
	@NotNull
	private Integer idFichePoste;
	
	@Column(name = "ID_SERVI", columnDefinition = "char")
	private String codeService;

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_RESPONSABLE", referencedColumnName = "ID_FICHE_POSTE")
	private FichePoste responsable;

	public Integer getIdFichePoste() {
		return idFichePoste;
	}

	public void setIdFichePoste(Integer idFichePoste) {
		this.idFichePoste = idFichePoste;
	}

	public String getCodeService() {
		return codeService;
	}

	public void setCodeService(String codeService) {
		this.codeService = codeService;
	}

	public FichePoste getResponsable() {
		return responsable;
	}

	public void setResponsable(FichePoste responsable) {
		this.responsable = responsable;
	}
	
	
}
