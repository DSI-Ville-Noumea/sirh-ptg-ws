package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;
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
	private String etat;
	private Integer numRubrique;

	public PrimeDto() {
	}

	public PrimeDto(RefPrime prime) {
		titre = prime.getLibelle();
		// TODO: compelte ctor
	}
	
	public PrimeDto(PrimeDto primeDto) {
		this();
		this.idPointage = primeDto.idPointage;
		this.titre = primeDto.titre;
		this.typePrime = primeDto.typePrime;
		this.quantite = primeDto.quantite;
		this.heureDebut = primeDto.heureDebut;
		this.heureFin = primeDto.heureFin;
		this.motif = primeDto.motif;
		this.commentaire = primeDto.commentaire;
		this.etat = primeDto.etat;
		this.numRubrique = primeDto.numRubrique;
	}

	public void updateWithPointage(Pointage ptg) {
		this.quantite = ptg.getQuantite();
		this.heureDebut = ptg.getDateDebut();
		this.heureFin = ptg.getDateDebut();
		this.etat = ptg.getLatestEtatPointage().getEtat().name();
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

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}
}
