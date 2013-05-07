package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class AbsenceDto {

	private Integer idPointage;
	private String titrePointage;
	private String typePrime;
	private Integer quantite;
	private Date heureDebut;
	private Date heureFin;
	private String motif;
	private String commentaire;
	private String etatPointage;

	public AbsenceDto() {
	}

	public AbsenceDto(Pointage p) {
		idPointage = p.getIdPointage();
		titrePointage="";
		typePrime=p.getType().getLabel();
		quantite = p.getQuantite();
		heureDebut=p.getDateDebut();
		heureFin=p.getDateFin();
		motif="";
		commentaire ="" ;
		etatPointage = "";
	}

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public String getTitrePointage() {
		return titrePointage;
	}

	public void setTitrePointage(String titrePointage) {
		this.titrePointage = titrePointage;
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
}
