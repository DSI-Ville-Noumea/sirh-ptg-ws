package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SPADMN")
@NamedQuery(
		name = "getAgentSpadmnAsOfDate",
		query = "from Spadmn sp where sp.id.nomatr = :nomatr and sp.id.datdeb <= :dateFormatMairie and (sp.datfin >= :dateFormatMairie or sp.datfin = 0)")
public class Spadmn {

	@Id
	private SpadmnId id;

	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
	
	@Column(name = "CDPADM", columnDefinition = "char")
	private String cdpadm;

	public SpadmnId getId() {
		return id;
	}

	public void setId(SpadmnId id) {
		this.id = id;
	}

	public Integer getDatfin() {
		return datfin;
	}

	public void setDatfin(Integer datfin) {
		this.datfin = datfin;
	}

	public String getCdpadm() {
		return cdpadm;
	}

	public void setCdpadm(String cdpadm) {
		this.cdpadm = cdpadm;
	}
	
	
}
