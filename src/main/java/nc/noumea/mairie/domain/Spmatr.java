package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SPMATR")
public class Spmatr {

	@Id
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "PERPRE", columnDefinition = "numeric default 0")
	private Integer perpre = 0;
	
	@Column(name = "PERRAP", columnDefinition = "numeric")
	private Integer perrap;

	@Column(name = "CDVALI", columnDefinition = "char default ' '")
	private String cdvali = " ";

	@Column(name = "CDCHAI", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public Integer getPerpre() {
		return perpre;
	}

	public void setPerpre(Integer perpre) {
		this.perpre = perpre;
	}

	public Integer getPerrap() {
		return perrap;
	}

	public void setPerrap(Integer perrap) {
		this.perrap = perrap;
	}

	public String getCdvali() {
		return cdvali;
	}

	public void setCdvali(String cdvali) {
		this.cdvali = cdvali;
	}

	public TypeChainePaieEnum getTypeChainePaie() {
		return typeChainePaie;
	}

	public void setTypeChainePaie(TypeChainePaieEnum typeChainePaie) {
		this.typeChainePaie = typeChainePaie;
	}
	
	
}
