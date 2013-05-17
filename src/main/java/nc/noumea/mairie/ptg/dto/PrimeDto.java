package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;

@XmlRootElement
public class PrimeDto {

	private Integer idPointage;
	private String titre;
	private String typeSaisie;
	private Integer quantite;
	private Date heureDebut;
	private Date heureFin;
	private String motif;
	private String commentaire;
	private String etat;
	private Integer numRubrique;
	private Integer idRefPrime;

	public PrimeDto() {
	}

	public PrimeDto(RefPrime prime) {
		this.titre = prime.getLibelle();
		this.idRefPrime = prime.getIdRefPrime();
		this.numRubrique = prime.getNoRubr();
		this.typeSaisie = prime.getTypeSaisie().name();
	}
	
	public PrimeDto(PrimeDto primeDto) {
		this();
		this.idPointage = primeDto.idPointage;
		this.titre = primeDto.titre;
		this.typeSaisie = primeDto.typeSaisie;
		this.quantite = primeDto.quantite;
		this.heureDebut = primeDto.heureDebut;
		this.heureFin = primeDto.heureFin;
		this.motif = primeDto.motif;
		this.commentaire = primeDto.commentaire;
		this.etat = primeDto.etat;
		this.numRubrique = primeDto.numRubrique;
		this.idRefPrime = primeDto.idRefPrime;
	}

	public void updateWithPointage(Pointage ptg) {
		this.idPointage = ptg.getIdPointage();
		this.etat = ptg.getLatestEtatPointage().getEtat().name();
		this.motif = ptg.getMotif() == null ? "" : ptg.getMotif().getText();
		this.commentaire = ptg.getCommentaire() == null ? "" : ptg.getCommentaire().getText();
		
		switch(ptg.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_HEURES:
			case NB_INDEMNITES:
				this.quantite = ptg.getQuantite();
				break;
			case PERIODE_HEURES:
				this.heureDebut = ptg.getDateDebut();
				this.heureFin = ptg.getDateFin();
				break;
		}
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

	public String getTypeSaisie() {
		return typeSaisie;
	}

	public void setTypeSaisie(String typeSaisie) {
		this.typeSaisie = typeSaisie;
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

	public Integer getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(Integer idRefPrime) {
		this.idRefPrime = idRefPrime;
	}
}
