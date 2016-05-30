package nc.noumea.mairie.ptg.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

public class DpmIndemniteAnneeDto implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6104807700822860344L;

	private Integer idDpmIndemAnnee;
	private Integer annee;
	private Date dateDebut;
	private Date dateFin;
	private List<DpmIndemniteChoixAgentDto> listDpmIndemniteChoixAgentDto;
	
	public DpmIndemniteAnneeDto() {
		listDpmIndemniteChoixAgentDto = new ArrayList<DpmIndemniteChoixAgentDto>();
	}
	
	public DpmIndemniteAnneeDto(DpmIndemAnnee dpmAnnee, boolean withChoixAgent) {
		this();
		this.idDpmIndemAnnee = dpmAnnee.getIdDpmIndemAnnee();
		this.annee = dpmAnnee.getAnnee();
		this.dateDebut = dpmAnnee.getDateDebut();
		this.dateFin = dpmAnnee.getDateFin();
		
		if(withChoixAgent
				&& null != dpmAnnee.getSetDpmIndemChoixAgent()
				&& !dpmAnnee.getSetDpmIndemChoixAgent().isEmpty()) {
			for(DpmIndemChoixAgent choixAgent : dpmAnnee.getSetDpmIndemChoixAgent()) {
				DpmIndemniteChoixAgentDto choixAgentDto = new DpmIndemniteChoixAgentDto(choixAgent);
				addDpmIndemniteChoixAgentDto(choixAgentDto);
			}
		}
	}
	
	public void addDpmIndemniteChoixAgentDto(DpmIndemniteChoixAgentDto choixAgentDto) {
		if(null != getListDpmIndemniteChoixAgentDto())
			getListDpmIndemniteChoixAgentDto().add(choixAgentDto);
	}

	public Integer getIdDpmIndemAnnee() {
		return idDpmIndemAnnee;
	}

	public void setIdDpmIndemAnnee(Integer idDpmIndemAnnee) {
		this.idDpmIndemAnnee = idDpmIndemAnnee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgentDto() {
		return listDpmIndemniteChoixAgentDto;
	}

	public void setListDpmIndemniteChoixAgentDto(List<DpmIndemniteChoixAgentDto> listDpmIndemniteChoixAgentDto) {
		this.listDpmIndemniteChoixAgentDto = listDpmIndemniteChoixAgentDto;
	}

	@Override
	public String toString() {
		return "DpmIndemniteAnneeDto [idDpmIndemAnnee=" + idDpmIndemAnnee + ", annee=" + annee + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
				+ ", listDpmIndemniteChoixAgentDto=" + listDpmIndemniteChoixAgentDto + "]";
	}
	
}
