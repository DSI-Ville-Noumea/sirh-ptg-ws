package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

public class AbstractItemEtatPayeurDto {

	private AgentWithServiceDto agent;

	private AbsencesEtatPayeurDto absences;
	private HeuresSupEtatPayeurDto heuresSup;
	private List<PrimesEtatPayeurDto> primes;

	public AbstractItemEtatPayeurDto() {
		primes = new ArrayList<PrimesEtatPayeurDto>();
		absences = new AbsencesEtatPayeurDto();
		heuresSup = new HeuresSupEtatPayeurDto();
		agent = new AgentWithServiceDto();
	}

	public List<PrimesEtatPayeurDto> getPrimes() {
		return primes;
	}

	public void setPrimes(List<PrimesEtatPayeurDto> primes) {
		this.primes = primes;
	}

	public AgentWithServiceDto getAgent() {
		return agent;
	}

	public void setAgent(AgentWithServiceDto agent) {
		this.agent = agent;
	}

	public AbsencesEtatPayeurDto getAbsences() {
		return absences;
	}

	public void setAbsences(AbsencesEtatPayeurDto absences) {
		this.absences = absences;
	}

	public HeuresSupEtatPayeurDto getHeuresSup() {
		return heuresSup;
	}

	public void setHeuresSup(HeuresSupEtatPayeurDto heuresSup) {
		this.heuresSup = heuresSup;
	}
}
