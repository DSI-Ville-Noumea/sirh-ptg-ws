package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

public class EtatPayeurDto {

	private String chainePaie;
	private String statut;
	private String periode;

	private List<AbsencesEtatPayeurDto> absences;
	private List<HeuresSupEtatPayeurDto> heuresSup;
	private List<PrimesEtatPayeurDto> primes;

	public EtatPayeurDto() {
		absences = new ArrayList<AbsencesEtatPayeurDto>();
		heuresSup = new ArrayList<HeuresSupEtatPayeurDto>();
		primes = new ArrayList<PrimesEtatPayeurDto>();
	}
	
	public EtatPayeurDto(TypeChainePaieEnum chainePaie, AgentStatutEnum statut, Date firstDayOfPeriod) {
		this();
		this.chainePaie = chainePaie.toString();
		this.statut = statut.toString();
//		this.periode = chainePaie.toString();
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

	public List<AbsencesEtatPayeurDto> getAbsences() {
		return absences;
	}

	public void setAbsences(List<AbsencesEtatPayeurDto> absences) {
		this.absences = absences;
	}

	public List<HeuresSupEtatPayeurDto> getHeuresSup() {
		return heuresSup;
	}

	public void setHeuresSup(List<HeuresSupEtatPayeurDto> heuresSup) {
		this.heuresSup = heuresSup;
	}

	public List<PrimesEtatPayeurDto> getPrimes() {
		return primes;
	}

	public void setPrimes(List<PrimesEtatPayeurDto> primes) {
		this.primes = primes;
	}
}
