package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FichePointageListDto {

	private List<FichePointageDto> fiches;
	

	public FichePointageListDto() {
		fiches = new ArrayList<FichePointageDto>();
	}

	public List<FichePointageDto> getFiches() {
		return fiches;
	}

	public void setFiches(List<FichePointageDto> fiches) {
		this.fiches = fiches;
	}
}
