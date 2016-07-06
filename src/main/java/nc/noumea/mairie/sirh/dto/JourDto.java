package nc.noumea.mairie.sirh.dto;

import java.util.Date;

import nc.noumea.mairie.ptg.dto.JsonDateDeserializer;
import nc.noumea.mairie.ptg.dto.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class JourDto {

	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date jour;
	private boolean isFerie;
	private boolean isChome;
	
	public Date getJour() {
		return jour;
	}
	public void setJour(Date jour) {
		this.jour = jour;
	}
	public boolean isFerie() {
		return isFerie;
	}
	public void setFerie(boolean isFerie) {
		this.isFerie = isFerie;
	}
	public boolean isChome() {
		return isChome;
	}
	public void setChome(boolean isChome) {
		this.isChome = isChome;
	}
	
}
