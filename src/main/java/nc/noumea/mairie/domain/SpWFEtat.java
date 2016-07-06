package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SPWFETAT")
public class SpWFEtat {

	@Id
	@NotNull
	@Column(name = "CDETAT", columnDefinition = "numeric")
	@Enumerated(EnumType.ORDINAL)
	private SpWfEtatEnum codeEtat;

	@NotNull
	@Column(name = "LIBETAT", columnDefinition = "char")
	private String libelleEtat;

	public SpWfEtatEnum getCodeEtat() {
		return codeEtat;
	}

	public void setCodeEtat(SpWfEtatEnum codeEtat) {
		this.codeEtat = codeEtat;
	}

	public String getLibelleEtat() {
		return libelleEtat;
	}

	public void setLibelleEtat(String libelleEtat) {
		this.libelleEtat = libelleEtat;
	}
	
	
}
