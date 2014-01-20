package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;

public abstract class AbstractItemEtatPayeurDto {

	private static SimpleDateFormat periodMonthSdf = new SimpleDateFormat("MMMM YYYY", Locale.FRENCH);

	private String approbateurNom;
	private String approbateurPrenom;
	private Integer approbateurIdAgent;
	private String approbateurServiceLabel;

	private String nom;
	private String prenom;
	private Integer idAgent;

	private Date date;
	private String periode;

	public AbstractItemEtatPayeurDto() {

	}

	public AbstractItemEtatPayeurDto(VentilHsup vh) {
		idAgent = vh.getIdAgent();
		date = vh.getDateLundi();
		periode = periodMonthSdf.format(date);
	}

	public AbstractItemEtatPayeurDto(VentilAbsence va) {
		idAgent = va.getIdAgent();
		date = va.getDateLundi();
		periode = periodMonthSdf.format(date);
	}

	public AbstractItemEtatPayeurDto(VentilPrime vp) {
		idAgent = vp.getIdAgent();
		date = vp.getDateDebutMois();
		periode = periodMonthSdf.format(date);
	}

	public String getApprobateurNom() {
		return approbateurNom;
	}

	public void setApprobateurNom(String approbateurNom) {
		this.approbateurNom = approbateurNom;
	}

	public String getApprobateurPrenom() {
		return approbateurPrenom;
	}

	public void setApprobateurPrenom(String approbateurPrenom) {
		this.approbateurPrenom = approbateurPrenom;
	}

	public Integer getApprobateurIdAgent() {
		return approbateurIdAgent;
	}

	public void setApprobateurIdAgent(Integer approbateurIdAgent) {
		this.approbateurIdAgent = approbateurIdAgent;
	}

	public String getApprobateurServiceLabel() {
		return approbateurServiceLabel;
	}

	public void setApprobateurServiceLabel(String approbateurServiceLabel) {
		this.approbateurServiceLabel = approbateurServiceLabel;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}
}
