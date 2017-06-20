package nc.noumea.mairie.ptg.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_EXPORT_ETATS_PAYEUR_DATA")
public class TitreRepasExportEtatPayeurData {

	@Id
	@Column(name = "ID_TR_EXPORT_ETATS_PAYEUR_DATA")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer							idTitreRepasExportEtatsPayeurData;

	@OneToOne
	@JoinColumn(name = "ID_TR_EXPORT_ETATS_PAYEUR_TASK")
	private TitreRepasExportEtatPayeurTask	titreRepasExportEtatsPayeurTask;

	@Column(name = "ID_AGENT")
	private Integer							idAgent;

	@NotNull
	@Column(name = "ID_TR")
	private Integer							idTitreRepas;

	@NotNull
	@Column(name = "CIVILITE_TR")
	private String							civiliteTitreRepas;

	@NotNull
	@Column(name = "NOM_TR")
	private String							nomTitreRepas;

	@NotNull
	@Column(name = "PRENOM_TR")
	private String							prenomTitreRepas;

	@NotNull
	@Column(name = "DATE_NAISS_TR", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date							dateNaissanceTitreRepas;

	@Version
	@Column(name = "version")
	private Integer							version;

	public TitreRepasExportEtatPayeurData() {
		super();
	}

	public TitreRepasExportEtatPayeurData(Integer idAgent, String[] nextLine, TitreRepasExportEtatPayeurTask titreRepasExportEtatsPayeurTask)
			throws ParseException {
		super();
		this.idAgent = idAgent;
		this.titreRepasExportEtatsPayeurTask = titreRepasExportEtatsPayeurTask;
		this.idTitreRepas = new Integer(nextLine[0].trim());
		this.civiliteTitreRepas = nextLine[1].trim();
		this.nomTitreRepas = nextLine[2].trim();
		this.prenomTitreRepas = nextLine[3].trim();
		this.dateNaissanceTitreRepas = new SimpleDateFormat("dd/MM/yyyy").parse(nextLine[4].trim());
	}

	public Integer getIdTitreRepasExportEtatsPayeurData() {
		return idTitreRepasExportEtatsPayeurData;
	}

	public void setIdTitreRepasExportEtatsPayeurData(Integer idTitreRepasExportEtatsPayeurData) {
		this.idTitreRepasExportEtatsPayeurData = idTitreRepasExportEtatsPayeurData;
	}

	public TitreRepasExportEtatPayeurTask getTitreRepasExportEtatsPayeurTask() {
		return titreRepasExportEtatsPayeurTask;
	}

	public void setTitreRepasExportEtatsPayeurTask(TitreRepasExportEtatPayeurTask titreRepasExportEtatsPayeurTask) {
		this.titreRepasExportEtatsPayeurTask = titreRepasExportEtatsPayeurTask;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdTitreRepas() {
		return idTitreRepas;
	}

	public void setIdTitreRepas(Integer idTitreRepas) {
		this.idTitreRepas = idTitreRepas;
	}

	public String getCiviliteTitreRepas() {
		return civiliteTitreRepas;
	}

	public void setCiviliteTitreRepas(String civiliteTitreRepas) {
		this.civiliteTitreRepas = civiliteTitreRepas;
	}

	public String getNomTitreRepas() {
		return nomTitreRepas;
	}

	public void setNomTitreRepas(String nomTitreRepas) {
		this.nomTitreRepas = nomTitreRepas;
	}

	public String getPrenomTitreRepas() {
		return prenomTitreRepas;
	}

	public void setPrenomTitreRepas(String prenomTitreRepas) {
		this.prenomTitreRepas = prenomTitreRepas;
	}

	public Date getDateNaissanceTitreRepas() {
		return dateNaissanceTitreRepas;
	}

	public void setDateNaissanceTitreRepas(Date dateNaissanceTitreRepas) {
		this.dateNaissanceTitreRepas = dateNaissanceTitreRepas;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
