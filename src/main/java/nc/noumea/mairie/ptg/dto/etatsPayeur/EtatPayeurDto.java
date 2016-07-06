package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

@XmlRootElement
public class EtatPayeurDto {

	private String chainePaie;
	private String statut;
	private String periode;
	private String dateVentilation;

	private List<AbstractItemEtatPayeurDto> agents;

	public EtatPayeurDto() {
		agents = new ArrayList<AbstractItemEtatPayeurDto>();
	}

	public EtatPayeurDto(TypeChainePaieEnum chainePaie, AgentStatutEnum statut, Date firstDayOfPeriod,
			Date dateVentilation) {
		this();
		this.chainePaie = chainePaie.toString();
		this.statut = statut.toString();
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
		this.periode = sdf.format(firstDayOfPeriod);
		this.agents = new ArrayList<AbstractItemEtatPayeurDto>();
		SimpleDateFormat sdfVentil = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
		this.dateVentilation = sdfVentil.format(dateVentilation);
	}

	public String getChainePaie() {
		return chainePaie;
	}

	public void setChainePaie(String chainePaie) {
		this.chainePaie = chainePaie;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}

	public List<AbstractItemEtatPayeurDto> getAgents() {
		return agents;
	}

	public void setAgents(List<AbstractItemEtatPayeurDto> agents) {
		this.agents = agents;
	}

	public String getDateVentilation() {
		return dateVentilation;
	}

	public void setDateVentilation(String dateVentilation) {
		this.dateVentilation = dateVentilation;
	}
}
