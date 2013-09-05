package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

@XmlRootElement
public class VentilDateDto {

	private Date dateVentil;
	private boolean isPaie;
	private TypeChainePaieEnum typeChaine;

	public VentilDateDto() {
	}

	public VentilDateDto(VentilDate ventilEnCoursDate) {
		if (ventilEnCoursDate != null
				&& ventilEnCoursDate.getIdVentilDate() != null) {
			dateVentil = ventilEnCoursDate.getDateVentilation();
			isPaie = ventilEnCoursDate.isPaye();
			typeChaine = ventilEnCoursDate.getTypeChainePaie();
		} else {
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

}
