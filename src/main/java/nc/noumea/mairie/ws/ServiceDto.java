package nc.noumea.mairie.ws;

public class ServiceDto {

	private String service;
	private String serviceLibelle;
	private String sigle;
	private String sigleParent;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getServiceLibelle() {
		return serviceLibelle;
	}

	public void setServiceLibelle(String serviceLibelle) {
		this.serviceLibelle = serviceLibelle;
	}

	public String getSigle() {
		return sigle;
	}

	public void setSigle(String sigle) {
		this.sigle = sigle;
	}

	public String getSigleParent() {
		return sigleParent;
	}

	public void setSigleParent(String sigleParent) {
		this.sigleParent = sigleParent;
	}
}
