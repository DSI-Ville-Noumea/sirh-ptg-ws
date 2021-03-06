package nc.noumea.mairie.ptg.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nc.noumea.mairie.ptg.domain.Pointage;

public abstract class PointageDtoKiosque {

	private Integer	idPointage;
	private Date	heureDebutDate;
	private String	heureDebut;
	private String	minuteDebut;
	private Date	heureFinDate;
	private String	heureFin;
	private String	minuteFin;
	private String	motif;
	private String	commentaire;
	private Integer	idRefEtat;
	private boolean	aSupprimer;
	private String	saisieJ1;

	public PointageDtoKiosque() {

	}

	public PointageDtoKiosque(Pointage p) {
		SimpleDateFormat sdfHeure = new SimpleDateFormat("HH");
		SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
		this.idPointage = p.getIdPointage();
		this.heureDebutDate = p.getDateDebut();
		this.heureDebut = sdfHeure.format(p.getDateDebut());
		this.minuteDebut = sdfMinute.format(p.getDateDebut());
		this.heureFinDate = p.getDateFin();
		this.heureFin = sdfHeure.format(p.getDateFin());
		this.minuteFin = sdfMinute.format(p.getDateFin());
		this.motif = p.getMotif() == null ? "" : p.getMotif().getText();
		this.commentaire = p.getCommentaire() == null ? "" : p.getCommentaire().getText();
		this.idRefEtat = p.getLatestEtatPointage().getEtat().getCodeEtat();
		Calendar calFinSaisieJ1 = Calendar.getInstance();
		calFinSaisieJ1.setTime(p.getDateFin());
		Integer jourFin = calFinSaisieJ1.get(Calendar.DAY_OF_MONTH);
		Calendar calDebutSaisieJ1 = Calendar.getInstance();
		calDebutSaisieJ1.setTime(p.getDateDebut());
		Integer jourDebut = calDebutSaisieJ1.get(Calendar.DAY_OF_MONTH);
		if (jourFin > jourDebut) {
			this.saisieJ1 = "Attention fin de saisie j+1";
		} else {
			this.saisieJ1 = null;
		}
	}

	public PointageDtoKiosque(PointageDtoKiosque pointageDto) {
		this.idPointage = pointageDto.idPointage;
		this.heureDebut = pointageDto.heureDebut;
		this.heureFin = pointageDto.heureFin;
		this.motif = pointageDto.motif;
		this.commentaire = pointageDto.commentaire;
		this.idRefEtat = pointageDto.idRefEtat;
	}

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
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

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public boolean isaSupprimer() {
		return aSupprimer;
	}

	public void setaSupprimer(boolean aSupprimer) {
		this.aSupprimer = aSupprimer;
	}

	public Date getHeureDebutDate() {
		return heureDebutDate;
	}

	public void setHeureDebutDate(Date heureDebutDate) {
		this.heureDebutDate = heureDebutDate;
	}

	public String getMinuteDebut() {
		return minuteDebut;
	}

	public void setMinuteDebut(String minuteDebut) {
		this.minuteDebut = minuteDebut;
	}

	public Date getHeureFinDate() {
		return heureFinDate;
	}

	public void setHeureFinDate(Date heureFinDate) {
		this.heureFinDate = heureFinDate;
	}

	public String getMinuteFin() {
		return minuteFin;
	}

	public void setMinuteFin(String minuteFin) {
		this.minuteFin = minuteFin;
	}

	public void setHeureDebut(String heureDebut) {
		this.heureDebut = heureDebut;
	}

	public void setHeureFin(String heureFin) {
		this.heureFin = heureFin;
	}

	public String getHeureDebut() {
		return heureDebut;
	}

	public String getHeureFin() {
		return heureFin;
	}

	public String getSaisieJ1() {
		return saisieJ1;
	}

	public void setSaisieJ1(String saisieJ1) {
		this.saisieJ1 = saisieJ1;
	}
}
