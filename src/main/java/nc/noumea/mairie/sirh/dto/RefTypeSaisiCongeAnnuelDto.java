package nc.noumea.mairie.sirh.dto;

import java.io.Serializable;

public class RefTypeSaisiCongeAnnuelDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idRefTypeSaisiCongeAnnuel;
	private String codeBaseHoraireAbsence;
	private String description;
	private Integer idRefTypeDemande;
	private boolean calendarDateDebut;
	private boolean chkDateDebut;
	private boolean calendarDateFin;
	private boolean chkDateFin;
	private boolean calendarDateReprise;
	private Integer quotaMultiple;
	private boolean decompteSamedi;
	private boolean consecutif;

	public RefTypeSaisiCongeAnnuelDto() {
	}

	public RefTypeSaisiCongeAnnuelDto(Integer idBaseConge) {
		this();
		this.idRefTypeSaisiCongeAnnuel = idBaseConge;
	}

	public Integer getIdRefTypeSaisiCongeAnnuel() {
		return idRefTypeSaisiCongeAnnuel;
	}

	public void setIdRefTypeSaisiCongeAnnuel(Integer idRefTypeSaisiCongeAnnuel) {
		this.idRefTypeSaisiCongeAnnuel = idRefTypeSaisiCongeAnnuel;
	}

	public String getCodeBaseHoraireAbsence() {
		return codeBaseHoraireAbsence;
	}

	public void setCodeBaseHoraireAbsence(String codeBaseHoraireAbsence) {
		this.codeBaseHoraireAbsence = codeBaseHoraireAbsence;
	}

	public Integer getIdRefTypeDemande() {
		return idRefTypeDemande;
	}

	public void setIdRefTypeDemande(Integer idRefTypeDemande) {
		this.idRefTypeDemande = idRefTypeDemande;
	}

	public boolean isCalendarDateDebut() {
		return calendarDateDebut;
	}

	public void setCalendarDateDebut(boolean calendarDateDebut) {
		this.calendarDateDebut = calendarDateDebut;
	}

	public boolean isChkDateDebut() {
		return chkDateDebut;
	}

	public void setChkDateDebut(boolean chkDateDebut) {
		this.chkDateDebut = chkDateDebut;
	}

	public boolean isCalendarDateFin() {
		return calendarDateFin;
	}

	public void setCalendarDateFin(boolean calendarDateFin) {
		this.calendarDateFin = calendarDateFin;
	}

	public boolean isChkDateFin() {
		return chkDateFin;
	}

	public void setChkDateFin(boolean chkDateFin) {
		this.chkDateFin = chkDateFin;
	}

	public boolean isCalendarDateReprise() {
		return calendarDateReprise;
	}

	public void setCalendarDateReprise(boolean calendarDateReprise) {
		this.calendarDateReprise = calendarDateReprise;
	}

	public Integer getQuotaMultiple() {
		return quotaMultiple;
	}

	public void setQuotaMultiple(Integer quotaMultiple) {
		this.quotaMultiple = quotaMultiple;
	}

	public boolean isDecompteSamedi() {
		return decompteSamedi;
	}

	public void setDecompteSamedi(boolean decompteSamedi) {
		this.decompteSamedi = decompteSamedi;
	}

	public boolean isConsecutif() {
		return consecutif;
	}

	public void setConsecutif(boolean consecutif) {
		this.consecutif = consecutif;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
