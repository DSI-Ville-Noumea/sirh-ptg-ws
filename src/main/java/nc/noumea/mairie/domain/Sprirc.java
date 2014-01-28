package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SPRIRC")
@NamedQuery(
		name = "getSprircForAgentAndPeriod", 
		query = "from Sprirc sp where sp.id.nomatr = :nomatr and sp.cdvali = 'V' and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb >= :start and sp.id.datdeb <= :end)")
public class Sprirc {

	@EmbeddedId
	private SprircId id;
	
	@Column(name = "CDVALI", columnDefinition = "char")
	private String cdvali;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
	
	@Column(name = "CODEM2", columnDefinition = "numeric")
	private Integer codem2;
	
	@Column(name = "NBRCP", columnDefinition = "decimal")
	private Double nbRcp;
	
	@Column(name = "DATREP", columnDefinition = "numeric")
	private Integer datRep;
	
	@Column(name = "CODEMA", columnDefinition = "numeric")
	private Integer codema;

	public SprircId getId() {
		return id;
	}

	public void setId(SprircId id) {
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

	public Integer getCodem2() {
		return codem2;
	}

	public void setCodem2(Integer codem2) {
		this.codem2 = codem2;
	}

	public Double getNbRcp() {
		return nbRcp;
	}

	public void setNbRcp(Double nbRcp) {
		this.nbRcp = nbRcp;
	}

	public Integer getDatRep() {
		return datRep;
	}

	public void setDatRep(Integer datRep) {
		this.datRep = datRep;
	}

	public Integer getCodema() {
		return codema;
	}

	public void setCodema(Integer codema) {
		this.codema = codema;
	}
	
}
