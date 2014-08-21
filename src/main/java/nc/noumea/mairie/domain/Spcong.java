package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SPCONG")
@NamedQueries({
@NamedQuery(
		name = "getSpcongForAgentAndPeriod", 
		query = "from Spcong sp where sp.id.nomatr = :nomatr and sp.cdvali = 'V' and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb >= :start and sp.id.datdeb <= :end)"),
@NamedQuery(
		name = "getSpcongWithoutCongesAnnuelsEtAnnulesForAgentAndPeriod", 
		query = "from Spcong sp where sp.id.nomatr = :nomatr and sp.cdvali = 'V' and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb >= :start and sp.id.datdeb <= :end)"
				+ " and sp.id.type2 not in (1, 91, 92, 93, 94) ")
})
public class Spcong {

	@EmbeddedId
	private SpcongId id;
	
	@Column(name = "CDVALI", columnDefinition = "char")
	private String cdvali;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
	
	@Column(name = "CODEM1", columnDefinition = "numeric")
	private Integer codem1;
	
	@Column(name = "CODEM2", columnDefinition = "numeric")
	private Integer codem2;

	public SpcongId getId() {
		return id;
	}

	public void setId(SpcongId id) {
		this.id = id;
	}

	public String getCdvali() {
		return cdvali;
	}

	public void setCdvali(String cdvali) {
		this.cdvali = cdvali;
	}

	public Integer getDatfin() {
		return datfin;
	}

	public void setDatfin(Integer datfin) {
		this.datfin = datfin;
	}

	public Integer getCodem1() {
		return codem1;
	}

	public void setCodem1(Integer codem1) {
		this.codem1 = codem1;
	}

	public Integer getCodem2() {
		return codem2;
	}

	public void setCodem2(Integer codem2) {
		this.codem2 = codem2;
	}
	
	
}
