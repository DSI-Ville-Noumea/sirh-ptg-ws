package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefPrime;

@XmlRootElement
public class PrimeDto {

	private Integer idPointage;
	private String titre;
	private String typePrime;
	private Integer quantite;
	private Date heureDebut;
	private Date heureFin;
	private String motif;
	private String commentaire;
	private String etatPointage;
	private Integer numRubrique;

	public PrimeDto() {
	}

	public PrimeDto(RefPrime prime) {
		titre = prime.getLibelle();
		// TODO: compelte ctor
	}
	
	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getTypePrime() {
		return typePrime;
	}

	public void setTypePrime(String typePrime) {
		this.typePrime = typePrime;
	}

	public Integer getQuantite() {
		return quantite;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}

	public Date getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(Date heureDebut) {
		this.heureDebut = heureDebut;
	}

	public Date getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(Date heureFin) {
		this.heureFin = heureFin;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getEtatPointage() {
		return etatPointage;
	}

	public void setEtatPointage(String etatPointage) {
		this.etatPointage = etatPointage;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}
}
