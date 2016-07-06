package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.service.impl.HelperService;

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
	private AgentDto operateur;
	private boolean heuresSupRecuperees;
	private boolean heuresSupRappelEnService;

	private boolean isApprobation;
	// # 17613 pour affichage des boutons
	private boolean isAffichageBoutonAccepter;
	private boolean isAffichageBoutonRefuser;
	private boolean isAffichageBoutonRejeter;

	public ConsultPointageDto() {

	}

	public ConsultPointageDto(Pointage ptg, HelperService helper) {

		idPointage = ptg.getIdPointage();
		typePointage = ptg.getType().getLabel();
		date = ptg.getDateDebut();
		debut = ptg.getDateDebut();
		fin = ptg.getDateFin();
		if (ptg.getMotif() == null) {
			if (ptg.getMotifHsup() != null) {
				motif = ptg.getMotifHsup() == null ? "" : ptg.getMotifHsup().getText();
			}
		} else {
			motif = ptg.getMotif() == null ? "" : ptg.getMotif().getText();
		}
		commentaire = ptg.getCommentaire() == null ? "" : ptg.getCommentaire().getText();

		switch (ptg.getTypePointageEnum()) {
			case ABSENCE:
			case H_SUP:
				quantite = helper.formatMinutesToString(debut, fin);
				heuresSupRecuperees = null == ptg.getHeureSupRecuperee() ? false : ptg.getHeureSupRecuperee();
				heuresSupRappelEnService = null == ptg.getHeureSupRappelService() ? false : ptg.getHeureSupRappelService();
				break;
			case PRIME:
				typePointage = ptg.getRefPrime().getLibelle();
				switch (ptg.getRefPrime().getTypeSaisie()) {
					case CASE_A_COCHER:
					case NB_INDEMNITES:
						quantite = String.format("%s", ptg.getQuantite());
						break;
					case NB_HEURES:
						quantite = helper.formatMinutesToString(ptg.getQuantite());
						break;
					case PERIODE_HEURES:
						quantite = helper.formatMinutesToString(debut, fin);
						break;
				}
		}

	}

	public void updateEtat(EtatPointage etat, AgentDto ope) {
		idRefEtat = etat.getEtat().getCodeEtat();
		dateSaisie = etat.getDateEtat();
		operateur = ope;
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

	public AgentDto getOperateur() {
		return operateur;
	}

	public void setOperateur(AgentDto operateur) {
		this.operateur = operateur;
	}

	public boolean isHeuresSupRecuperees() {
		return heuresSupRecuperees;
	}

	public void setHeuresSupRecuperees(boolean heuresSupRecuperees) {
		this.heuresSupRecuperees = heuresSupRecuperees;
	}

	public boolean isHeuresSupRappelEnService() {
		return heuresSupRappelEnService;
	}

	public void setHeuresSupRappelEnService(boolean heuresSupRappelEnService) {
		this.heuresSupRappelEnService = heuresSupRappelEnService;
	}

	public boolean isApprobation() {
		return isApprobation;
	}

	public void setApprobation(boolean isApprobation) {
		this.isApprobation = isApprobation;
	}

	public boolean isAffichageBoutonAccepter() {
		return isAffichageBoutonAccepter;
	}

	public void setAffichageBoutonAccepter(boolean isAffichageBoutonAccepter) {
		this.isAffichageBoutonAccepter = isAffichageBoutonAccepter;
	}

	public boolean isAffichageBoutonRefuser() {
		return isAffichageBoutonRefuser;
	}

	public void setAffichageBoutonRefuser(boolean isAffichageBoutonRefuser) {
		this.isAffichageBoutonRefuser = isAffichageBoutonRefuser;
	}

	public boolean isAffichageBoutonRejeter() {
		return isAffichageBoutonRejeter;
	}

	public void setAffichageBoutonRejeter(boolean isAffichageBoutonRejeter) {
		this.isAffichageBoutonRejeter = isAffichageBoutonRejeter;
	}
}
