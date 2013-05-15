package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.EtatPointage;
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
	private List<EtatPointage> etatsPointage;

	public AbsenceDto() {
		etatsPointage = new ArrayList<EtatPointage>();
	}

	public AbsenceDto(Pointage p) {
		this();
		this.idPointage = p.getIdPointage();
		this.titrePointage = "";
		this.typePrime = p.getType().getLabel();
		this.quantite = p.getQuantite();
		this.heureDebut = p.getDateDebut();
		this.heureFin = p.getDateFin();
		this.motif = "";
		this.commentaire = "";
		this.etatsPointage.addAll(p.getEtats());
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

	public List<EtatPointage> getEtatsPointage() {
		return etatsPointage;
	}

	public void setEtatsPointage(List<EtatPointage> etatsPointage) {
		this.etatsPointage = etatsPointage;
	}
}
