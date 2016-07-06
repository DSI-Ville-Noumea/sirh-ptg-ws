package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SPBAREM")
public class Spbarem {

	@Id
	@Column(name = "IBAN", columnDefinition = "char")
	private String iban;
	
	@Column(name = "INA", columnDefinition = "numeric")
	private Integer ina;

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public Integer getIna() {
		return ina;
	}

	public void setIna(Integer ina) {
		this.ina = ina;
	}
	
	
}
