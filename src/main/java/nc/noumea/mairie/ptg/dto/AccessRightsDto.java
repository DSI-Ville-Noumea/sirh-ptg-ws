package nc.noumea.mairie.ptg.dto;

import flexjson.JSONSerializer;

public class AccessRightsDto implements IJSONSerialize {

	private boolean gestionDroitsAcces;
	private boolean fiches;
	private boolean saisie;
	private boolean visualisation;
	private boolean approbation;
	private boolean titreRepas;
	
	public boolean isGestionDroitsAcces() {
		return gestionDroitsAcces;
	}
	public void setGestionDroitsAcces(boolean gestionDroitsAcces) {
		this.gestionDroitsAcces = gestionDroitsAcces;
	}
	public boolean isFiches() {
		return fiches;
	}
	public void setFiches(boolean fiches) {
		this.fiches = fiches;
	}
	public boolean isSaisie() {
		return saisie;
	}
	public void setSaisie(boolean saisie) {
		this.saisie = saisie;
	}
	public boolean isVisualisation() {
		return visualisation;
	}
	public void setVisualisation(boolean visualisation) {
		this.visualisation = visualisation;
	}
	public boolean isApprobation() {
		return approbation;
	}
	public void setApprobation(boolean approbation) {
		this.approbation = approbation;
	}
	public boolean isTitreRepas() {
		return titreRepas;
	}
	public void setTitreRepas(boolean titreRepas) {
		this.titreRepas = titreRepas;
	}
	@Override
	public String serializeInJSON() {
		return new JSONSerializer().exclude("*.class").serialize(this);
	}
}
