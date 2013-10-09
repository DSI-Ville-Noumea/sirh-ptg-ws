package nc.noumea.mairie.ptg.dto.etatsPayeur;

public class AbsencesEtatPayeurDto extends AbstractItemEtatPayeurDto {

	private String type;
	private Integer quantite;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getQuantite() {
		return quantite;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}
}
