package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SPABSEN")
@NamedQuery(
		name = "getSpabsenForAgentAndPeriod", 
		query = "from Spabsen sp where sp.id.nomatr = :nomatr and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb >= :start and sp.id.datdeb <= :end)")
public class Spabsen {

	@EmbeddedId
	private SpabsenId id;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;

	public SpabsenId getId() {
		return id;
	}

	public void setId(SpabsenId id) {
		this.id = id;
	}

	public Integer getDatfin() {
		return datfin;
	}

	public void setDatfin(Integer datfin) {
		this.datfin = datfin;
	}
	
	
}
