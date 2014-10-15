package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class HeureSupDto extends PointageDto {

	private boolean recuperee;
	private boolean rappelService;

	public HeureSupDto() {

	}

	public HeureSupDto(Pointage p) {
		super(p);
		this.recuperee = p.getHeureSupRecuperee();
		this.rappelService = p.getHeureSupRappelService();
	}

	public boolean getRecuperee() {
		return recuperee;
	}

	public void setRecuperee(boolean recuperee) {
		this.recuperee = recuperee;
	}

	public boolean getRappelService() {
		return rappelService;
	}

	public void setRappelService(boolean rappelService) {
		this.rappelService = rappelService;
	}
}
