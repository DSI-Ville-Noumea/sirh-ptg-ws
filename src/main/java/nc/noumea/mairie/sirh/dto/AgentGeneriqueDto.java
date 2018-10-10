package nc.noumea.mairie.sirh.dto;


public class AgentGeneriqueDto {

	private Integer idAgent;
	private Integer nomatr;
	private String nomMarital;
	private String nomPatronymique;
	private String nomUsage;
	private String prenom;
	private String prenomUsage;
	// #40074 optimisation pour Tickets restaurant
	private Integer	idTicketRestaurant;
	private String	idTiarhe;

	public Integer getIdTicketRestaurant() {
		return idTicketRestaurant;
	}

	public void setIdTicketRestaurant(Integer idTicketRestaurant) {
		this.idTicketRestaurant = idTicketRestaurant;
	}

	public String getDisplayPrenom() {
		return getPrenomUsage();
	}

	public String getDisplayNom() {
		return getNomUsage();
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public String getNomMarital() {
		return nomMarital;
	}

	public void setNomMarital(String nomMarital) {
		this.nomMarital = nomMarital;
	}

	public String getNomPatronymique() {
		return nomPatronymique;
	}

	public void setNomPatronymique(String nomPatronymique) {
		this.nomPatronymique = nomPatronymique;
	}

	public String getNomUsage() {
		return nomUsage;
	}

	public void setNomUsage(String nomUsage) {
		this.nomUsage = nomUsage;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getPrenomUsage() {
		return prenomUsage;
	}

	public void setPrenomUsage(String prenomUsage) {
		this.prenomUsage = prenomUsage;
	}

	public String getIdTiarhe() {
		return idTiarhe;
	}

	public void setIdTiarhe(String idTiarhe) {
		this.idTiarhe = idTiarhe;
	}
}
