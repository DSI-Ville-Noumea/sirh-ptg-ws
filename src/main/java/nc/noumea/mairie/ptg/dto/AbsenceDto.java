package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class AbsenceDto {

	private Integer idPointage;
	private Date heureDebut;
	private Date heureFin;
	private Boolean concertee;
	private String motif;
	private String commentaire;
	private String etat;

	public AbsenceDto() {
	}

	public AbsenceDto(Pointage p) {
		this.idPointage = p.getIdPointage();
		this.heureDebut = p.getDateDebut();
		this.heureFin = p.getDateFin();
		this.motif = p.getMotif() == null ? "" : p.getMotif().getText();
		this.commentaire = p.getCommentaire() == null ? "" : p.getCommentaire().getText();
		this.etat = p.getLatestEtatPointage().getEtat().name();
		this.concertee = p.getAbsenceConcertee();
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
	
	public Boolean getConcertee() {
		return concertee;
	}

	public void setConcertee(Boolean concertee) {
		this.concertee = concertee;
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
