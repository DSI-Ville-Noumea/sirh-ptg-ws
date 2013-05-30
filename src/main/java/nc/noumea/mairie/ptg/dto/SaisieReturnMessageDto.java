package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.List;

public class SaisieReturnMessageDto {

	private List<String> errors;
	private List<String> infos;

	public SaisieReturnMessageDto() {
		errors = new ArrayList<String>();
		infos = new ArrayList<String>();
	}
	
	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}
}
