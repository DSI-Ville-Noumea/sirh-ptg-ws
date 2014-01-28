package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_RC_HISTO")
@NamedQuery(name = "findReposCompHistoByAgentAndDate", query = "select rch from ReposCompHisto rch where rch.idAgent = :idAgent and rch.dateLundi = :dateLundi")
public class ReposCompHisto {

	@Id
	@Column(name = "ID_RC_HISTO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRcHisto;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "DATE_LUNDI")
	private Date dateLundi;
	
	@Column(name = "M_SUP")
	private Integer mSup;

	@Column(name = "M_BASE_HORAIRE")
	private Integer mBaseHoraire;

	@Version
    @Column(name = "version")
	private Integer version;
	
	public Integer getIdRcHisto() {
		return idRcHisto;
	}

	public void setIdRcHisto(Integer idRcHisto) {
		this.idRcHisto = idRcHisto;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
	}

	public Integer getMSup() {
		return mSup;
	}

	public void setMSup(Integer mSup) {
		this.mSup = mSup;
	}

	public Integer getMBaseHoraire() {
		return mBaseHoraire;
	}

	public void setMBaseHoraire(Integer mBaseHoraire) {
		this.mBaseHoraire = mBaseHoraire;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
