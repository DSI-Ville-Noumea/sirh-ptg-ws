package nc.noumea.mairie.sirh.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import nc.noumea.mairie.ptg.dto.JsonDateDeserializer;
import nc.noumea.mairie.ptg.dto.JsonDateSerializer;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;

public class AffectationDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer idAffectation;
	private Integer idAgent;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateDebut;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateFin;
	private List<RefPrimeDto> listPrimesAff;
	private RefTypeSaisiCongeAnnuelDto baseConge;

	public AffectationDto() {
		this.listPrimesAff = new ArrayList<RefPrimeDto>();
	}

	public Integer getIdAffectation() {
		return idAffectation;
	}

	public void setIdAffectation(Integer idAffectation) {
		this.idAffectation = idAffectation;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public List<RefPrimeDto> getListPrimesAff() {
		return listPrimesAff;
	}

	public void setListPrimesAff(List<RefPrimeDto> listPrimesAff) {
		this.listPrimesAff = listPrimesAff;
	}

	public RefTypeSaisiCongeAnnuelDto getBaseConge() {
		return baseConge;
	}

	public void setBaseConge(RefTypeSaisiCongeAnnuelDto baseConge) {
		this.baseConge = baseConge;
	}
}
