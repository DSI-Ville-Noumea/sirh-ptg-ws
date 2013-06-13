package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class HeureSupDto extends PointageDto  {
	
	private Boolean recuperee;

	public HeureSupDto() {
	
	}
	
	public HeureSupDto(Pointage p) {
		super(p);
		this.recuperee = p.getHeureSupRecuperee();
	}
	
	public Boolean getRecuperee() {
		return recuperee;
	}

	public void setRecuperee(Boolean recuperee) {
		this.recuperee = recuperee;
	}
}
