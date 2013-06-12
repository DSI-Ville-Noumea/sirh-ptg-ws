package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.Pointage;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class ConsultPointageDto {

	private Integer idPointage;
	private AgentDto agent;
	private String typePointage;
	private Date date;
	private Date debut;
	private Date fin;
	private String quantite;
	private String motif;
	private String commentaire;
	private Integer idRefEtat;
	private Date dateSaisie;

	private static PeriodFormatter formatter = new PeriodFormatterBuilder()
	    .appendHours()
	    .appendSuffix("h")
	    .appendMinutes()
	    .appendSuffix("m")
	    .toFormatter();
	
	public ConsultPointageDto() {

	}

	public ConsultPointageDto(Pointage ptg) {

		idPointage = ptg.getIdPointage();
		typePointage = ptg.getType().getLabel();
		date = ptg.getDateDebut();
		debut = ptg.getDateDebut();
		fin = ptg.getDateFin();
		motif = ptg.getMotif() == null ? "" : ptg.getMotif().getText();
		commentaire = ptg.getCommentaire() == null ? "" : ptg.getCommentaire()
				.getText();
		
		EtatPointage etat = ptg.getLatestEtatPointage();
		idRefEtat = etat.getEtat().getCodeEtat();
		dateSaisie = etat.getEtatPointagePk().getDateEtat();

		switch (ptg.getTypePointageEnum()) {
		case ABSENCE:
		case H_SUP:
			quantite = formatHourInterval(debut, fin);
			break;
		case PRIME:
			switch (ptg.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_INDEMNITES:
				quantite = formatNumberOf(ptg.getQuantite(), "");
				break;
			case NB_HEURES:
				quantite = formatNumberOf(ptg.getQuantite(), "h");
				break;
			case PERIODE_HEURES:
				quantite = formatHourInterval(debut, fin);
				break;
			}
		}

	}

	private String formatHourInterval(Date debut, Date fin) {
		return formatter.print(new Period(new DateTime(debut), new DateTime(fin)));
	}

	private String formatNumberOf(Integer quantite, String suffix) {
		if (suffix != null && suffix != "")
			return String.format("%s%s", quantite, suffix);

		return String.format("%s", quantite);
	}

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public AgentDto getAgent() {
		return agent;
	}

	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}

	public String getTypePointage() {
		return typePointage;
	}

	public void setTypePointage(String typePointage) {
		this.typePointage = typePointage;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDebut() {
		return debut;
	}

	public void setDebut(Date debut) {
		this.debut = debut;
	}

	public Date getFin() {
		return fin;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
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

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}
}
