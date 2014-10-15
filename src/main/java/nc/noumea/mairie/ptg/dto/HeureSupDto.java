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
		// TODO
		// remplir le DTO pour rappel en service et isDPM
		super(p);
		this.recuperee = p.getHeureSupRecuperee();
	}

	public boolean getRecuperee() {
		return recuperee;
	}

	public void setRecuperee(boolean recuperee) {
		this.recuperee = recuperee;
	}

	public boolean isRappelService() {
		return rappelService;
	}

	public void setRappelService(boolean rappelService) {
		this.rappelService = rappelService;
	}
}
