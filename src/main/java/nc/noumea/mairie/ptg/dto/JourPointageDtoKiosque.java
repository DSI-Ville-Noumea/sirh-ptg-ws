package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JourPointageDtoKiosque {

	private Date date;
	private List<PrimeDtoKiosque> primes;
	private List<HeureSupDtoKiosque> heuresSup;
	private List<AbsenceDtoKiosque> absences;

	public JourPointageDtoKiosque() {
		primes = new ArrayList<PrimeDtoKiosque>();
		heuresSup = new ArrayList<HeureSupDtoKiosque>();
		absences = new ArrayList<AbsenceDtoKiosque>();
	}

	public JourPointageDtoKiosque(JourPointageDtoKiosque jourPointageTemplate) {
		this();

		// copy all primes every day
		for (PrimeDtoKiosque prime : jourPointageTemplate.getPrimes())
			primes.add(new PrimeDtoKiosque(prime));
	}

	public List<PrimeDtoKiosque> getPrimes() {
		return primes;
	}

	public void setPrimes(List<PrimeDtoKiosque> primes) {
		this.primes = primes;
	}

	public List<HeureSupDtoKiosque> getHeuresSup() {
		return heuresSup;
	}

	public void setHeuresSup(List<HeureSupDtoKiosque> heuresSup) {
		this.heuresSup = heuresSup;
	}

	public List<AbsenceDtoKiosque> getAbsences() {
		return absences;
	}

	public void setAbsences(List<AbsenceDtoKiosque> absences) {
		this.absences = absences;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
