package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;

@XmlRootElement
public class PrimeDto extends PointageDto  {

	private String titre;
	private String typeSaisie;
	private Integer quantite;
	private Integer numRubrique;
	private Integer idRefPrime;

	public PrimeDto() {
	}

	public PrimeDto(RefPrime prime) {
		this.titre = prime.getLibelle();
		this.idRefPrime = prime.getIdRefPrime();
		this.numRubrique = prime.getNoRubr();
		this.typeSaisie = prime.getTypeSaisie().name();
	}
	
	public PrimeDto(PrimeDto primeDto) {
		super((PointageDto) primeDto);
		
		this.titre = primeDto.titre;
		this.typeSaisie = primeDto.typeSaisie;
		this.quantite = primeDto.quantite;
		this.numRubrique = primeDto.numRubrique;
		this.idRefPrime = primeDto.idRefPrime;
	}

	public void updateWithPointage(Pointage ptg) {
		this.setIdPointage(ptg.getIdPointage());
		this.setEtat(ptg.getLatestEtatPointage().getEtat().name());
		this.setMotif(ptg.getMotif() == null ? "" : ptg.getMotif().getText());
		this.setCommentaire(ptg.getCommentaire() == null ? "" : ptg.getCommentaire().getText());
		
		switch(ptg.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_HEURES:
			case NB_INDEMNITES:
				this.quantite = ptg.getQuantite();
				break;
			case PERIODE_HEURES:
				this.setHeureDebut(ptg.getDateDebut());
				this.setHeureFin(ptg.getDateFin());
				break;
		}
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getTypeSaisie() {
		return typeSaisie;
	}

	public void setTypeSaisie(String typeSaisie) {
		this.typeSaisie = typeSaisie;
	}

	public Integer getQuantite() {
		return quantite;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}

	public Integer getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(Integer idRefPrime) {
		this.idRefPrime = idRefPrime;
	}
}
