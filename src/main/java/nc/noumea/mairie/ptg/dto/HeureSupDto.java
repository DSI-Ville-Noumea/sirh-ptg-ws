package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class HeureSupDto extends PointageDto  {
	
	private Boolean payee;

	public HeureSupDto() {
	
	}
	
	public HeureSupDto(Pointage p) {
		super(p);
		this.payee = p.getHeureSupPayee();
	}
	
	public Boolean getPayee() {
		return payee;
	}

	public void setPayee(Boolean payee) {
		this.payee = payee;
	}
}
