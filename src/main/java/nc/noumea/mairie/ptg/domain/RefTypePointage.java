package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PTG_REF_TYPE_POINTAGE")
public class RefTypePointage {

	@Id
	@Column(name = "ID_REF_TYPE_POINTAGE")
	private Integer idRefTypePointage;

	@Column(name = "LABEL", columnDefinition = "NVARCHAR2")
	private String label;

	public Integer getIdRefTypePointage() {
		return idRefTypePointage;
	}

	public void setIdRefTypePointage(Integer idRefTypePointage) {
		this.idRefTypePointage = idRefTypePointage;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
