package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "PTG_MOTIF_HSUP")
public class MotifHeureSup {

	@Id
	@Column(name = "ID_MOTIF_HSUP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMotifHsup;

	@Column(name = "TEXT", columnDefinition = "text")
	private String text;

	@Version
	@Column(name = "version")
	private Integer version;

	public MotifHeureSup() {

	}

	public MotifHeureSup(String comment) {
		this.text = comment;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getIdMotifHsup() {
		return idMotifHsup;
	}

	public void setIdMotifHsup(Integer idMotifHsup) {
		this.idMotifHsup = idMotifHsup;
	}

}
