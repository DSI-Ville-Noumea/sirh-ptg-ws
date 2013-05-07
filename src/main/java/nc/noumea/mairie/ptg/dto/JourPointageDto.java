package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JourPointageDto {

	private Date date;
	private List<PrimeDto> primes;
	private List<HeureSupDto> heuresSup;
	private List<AbsenceDto> absences;

	public JourPointageDto() {
	}

	public JourPointageDto(JourPointageDto jourPointageTemplate) {
		// TODO Auto-generated constructor stub
	}

	public List<PrimeDto> getPrimes() {
		return primes == null ? new ArrayList<PrimeDto>() : primes;
	}

	public void setPrimes(List<PrimeDto> primes) {
		this.primes = primes;
	}

	public List<HeureSupDto> getHeuresSup() {
		return heuresSup == null ? new ArrayList<HeureSupDto>() : heuresSup;
	}

	public void setHeuresSup(List<HeureSupDto> heuresSup) {
		this.heuresSup = heuresSup;
	}

	public List<AbsenceDto> getAbsences() {
		return absences == null ? new ArrayList<AbsenceDto>() : absences;
	}

	public void setAbsences(List<AbsenceDto> absences) {
		this.absences = absences;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
