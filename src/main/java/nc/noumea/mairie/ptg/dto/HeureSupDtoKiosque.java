package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class HeureSupDtoKiosque extends PointageDtoKiosque {

	private boolean recuperee;
	private boolean rappelService;
	private Integer idMotifHsup;

	public HeureSupDtoKiosque() {

	}

	public HeureSupDtoKiosque(Pointage p) {
		super(p);
		this.recuperee = p.getHeureSupRecuperee();
		this.rappelService = p.getHeureSupRappelService();
		this.idMotifHsup = p.getMotifHsup() == null ? null : p.getMotifHsup().getIdMotifHsup();
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

	public Integer getIdMotifHsup() {
		return idMotifHsup;
	}

	public void setIdMotifHsup(Integer idMotifHsup) {
		this.idMotifHsup = idMotifHsup;
	}
}
