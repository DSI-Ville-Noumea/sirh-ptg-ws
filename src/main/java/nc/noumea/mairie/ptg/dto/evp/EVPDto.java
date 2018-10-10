package nc.noumea.mairie.ptg.dto.evp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

public class EVPDto {
	
	private final String societeCle = "18";
	private String chainePaie;
	private Date datePeriodePaie;
	private Map<AgentWithServiceDto, List<EVPElementDto>> elements;

	public EVPDto() {
		this.elements = Maps.newHashMap();
	}
	
	public Date getDatePeriodePaie() {
		return datePeriodePaie;
	}
	public void setDatePeriodePaie(Date datePeriodePaie) {
		this.datePeriodePaie = datePeriodePaie;
	}
	public String getSocieteCle() {
		return societeCle;
	}
	public Map<AgentWithServiceDto, List<EVPElementDto>> getElements() {
		return elements;
	}
	public void setElements(Map<AgentWithServiceDto, List<EVPElementDto>> elements) {
		this.elements = elements;
	}
	public String getChainePaie() {
		return chainePaie;
	}
	public void setChainePaie(String chainePaie) {
		this.chainePaie = chainePaie;
	}
	
	
}
