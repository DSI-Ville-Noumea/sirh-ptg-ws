package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListEtatsPayeurDto {

	private Integer idEtatPayeur;
	private String statut;
	private Integer type;
	private Date dateEtatPayeur;
	private String label;
	private String fichier;
	private Integer idAgent;
	private Date dateEdition;
	
	public ListEtatsPayeurDto(){
		
	}
	
	public ListEtatsPayeurDto(Integer idEtatPayeur, String statut, Integer type, 
			Date dateEtatPayeur, String label, String fichier, Integer idAgent, Date dateEdition) { 
		this.idEtatPayeur = idEtatPayeur;
		this.statut = statut;
		this.type = type;
		this.dateEtatPayeur = dateEtatPayeur;
		this.label = label;
		this.fichier = fichier;
		this.idAgent = idAgent;
		this.dateEdition = dateEdition;
	}

	public Integer getIdEtatPayeur() {
		return idEtatPayeur;
	}

	public void setIdEtatPayeur(Integer idEtatPayeur) {
		this.idEtatPayeur = idEtatPayeur;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getDateEtatPayeur() {
		return dateEtatPayeur;
	}

	public void setDateEtatPayeur(Date dateEtatPayeur) {
		this.dateEtatPayeur = dateEtatPayeur;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFichier() {
		return fichier;
	}

	public void setFichier(String fichier) {
		this.fichier = fichier;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateEdition() {
		return dateEdition;
	}

	public void setDateEdition(Date dateEdition) {
		this.dateEdition = dateEdition;
	}
	
	
}
