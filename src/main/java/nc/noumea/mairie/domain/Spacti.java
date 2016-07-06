package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SPACTI")
public class Spacti {

	public static String CODE_ACTIVITE_ABS_NON_CONCERTEE = "A01";
	public static String CODE_ACTIVITE_ABS_CONCERTEE = "A02";
	public static String CODE_ACTIVITE_ABS_IMMEDIATE = "A07";

	@Id
	@Column(name = "CDACT3", insertable = false, updatable = false, columnDefinition = "char")
	private String codeActvite;

	@Column(name = "LIACTI", columnDefinition = "char")
	private String libelleActivite;

	public String getCodeActvite() {
		return codeActvite;
	}

	public void setCodeActvite(String codeActvite) {
		this.codeActvite = codeActvite;
	}

	public String getLibelleActivite() {
		return libelleActivite;
	}

	public void setLibelleActivite(String libelleActivite) {
		this.libelleActivite = libelleActivite;
	}

}
