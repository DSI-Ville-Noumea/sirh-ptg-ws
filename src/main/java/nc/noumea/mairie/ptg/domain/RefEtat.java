package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PTG_REF_ETAT")
public class RefEtat {

	@Id
	@Column(name = "ID_REF_ETAT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRefEtat;

	@Column(name = "LABEL", columnDefinition = "NVARCHAR2")
	private String label;

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
