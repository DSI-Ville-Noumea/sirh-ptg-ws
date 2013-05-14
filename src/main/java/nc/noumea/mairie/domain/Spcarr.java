package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPCARR", versionField = "")
@NamedQuery(name = "getCurrentCarriere", query = "select carr from Spcarr carr where carr.id.nomatr = :nomatr and carr.id.datdeb <= :todayFormatMairie and (carr.dateFin = 0 or carr.dateFin >= :todayFormatMairie)")
public class Spcarr {

	@EmbeddedId
	private SpcarrId id;

	public Spcarr() {
	}

	public Spcarr(Integer nomatr, Integer datdeb) {
		this.id = new SpcarrId(nomatr, datdeb);
	}

	@NotNull
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer dateFin;

	@NotNull
	@Column(name = "CDCATE", columnDefinition = "numeric")
	private Integer cdcate;

	@Transient
	public String getStatutCarriere() {
		switch (cdcate) {
			case 1:
			case 2:
			case 6:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
				return "F";
			case 4:
				return "C";
			case 7:
				return "CC";
			default:
				return "A";
		}
	}
}
