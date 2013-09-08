package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

public class VentilDateDto {

	private Integer idVentilDate;
	private Date dateVentil;
	private boolean isPaie;
	private TypeChainePaieEnum typeChaine;

	public VentilDateDto() {
	}

	public VentilDateDto(VentilDate ventilEnCoursDate) {
		if (ventilEnCoursDate != null && ventilEnCoursDate.getIdVentilDate() != null) {
			idVentilDate = ventilEnCoursDate.getIdVentilDate();
			dateVentil = ventilEnCoursDate.getDateVentilation();
			isPaie = ventilEnCoursDate.isPaye();
			typeChaine = ventilEnCoursDate.getTypeChainePaie();
		} else {
			idVentilDate = null;
			dateVentil = null;
			isPaie = true;
			typeChaine = null;
		}

	}

	public Date getDateVentil() {
		return dateVentil;
	}

	public void setDateVentil(Date dateVentil) {
		this.dateVentil = dateVentil;
	}

	public boolean isPaie() {
		return isPaie;
	}

	public void setPaie(boolean isPaie) {
		this.isPaie = isPaie;
	}

	public TypeChainePaieEnum getTypeChaine() {
		return typeChaine;
	}

	public void setTypeChaine(TypeChainePaieEnum typeChaine) {
		this.typeChaine = typeChaine;
	}

	public Integer getIdVentilDate() {
		return idVentilDate;
	}

	public void setIdVentilDate(Integer idVentilDate) {
		this.idVentilDate = idVentilDate;
	}

}
