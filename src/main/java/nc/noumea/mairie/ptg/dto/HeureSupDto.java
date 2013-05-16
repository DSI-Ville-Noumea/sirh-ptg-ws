package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class HeureSupDto {

	private Integer idPointage;
	private Date heureDebut;
	private Date heureFin;
	private Boolean payee;
	private String motif;
	private String commentaire;
	private String etat;

	public HeureSupDto() {
	
	}
	
	public HeureSupDto(Pointage p) {
		this();
		this.idPointage = p.getIdPointage();
		this.heureDebut = p.getDateDebut();
		this.heureFin = p.getDateFin();
		this.payee = p.getHeureSupPayee();
		this.motif = "";
		this.commentaire = "";
		this.etat = p.getLatestEtatPointage().getEtat().name();
	}
	
	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
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
	
	public Boolean getPayee() {
		return payee;
	}

	public void setPayee(Boolean payee) {
		this.payee = payee;
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
}
