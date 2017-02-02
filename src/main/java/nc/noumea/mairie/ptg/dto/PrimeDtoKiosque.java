package nc.noumea.mairie.ptg.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;

@XmlRootElement
public class PrimeDtoKiosque extends PointageDtoKiosque {

	private String	titre;
	private String	typeSaisie;
	private Integer	quantite;
	private Integer	numRubrique;
	private Integer	idRefPrime;
	private String	aide;

	public PrimeDtoKiosque() {
	}

	public PrimeDtoKiosque(RefPrime prime, String choixAgentDPM) {
		if (choixAgentDPM == null) {
			this.titre = prime.getLibelle();
		} else {
			this.titre = prime.getLibelle() + "\n" + "(" + choixAgentDPM + ")";
		}
		this.idRefPrime = prime.getIdRefPrime();
		this.numRubrique = prime.getNoRubr();
		this.typeSaisie = prime.getTypeSaisie().name();
		this.aide = prime.getAide();
	}

	public PrimeDtoKiosque(PrimeDtoKiosque primeDto) {
		super((PointageDtoKiosque) primeDto);

		this.titre = primeDto.titre;
		this.typeSaisie = primeDto.typeSaisie;
		this.quantite = primeDto.quantite;
		this.numRubrique = primeDto.numRubrique;
		this.idRefPrime = primeDto.idRefPrime;
		this.aide = primeDto.aide;
	}

	public void updateWithPointage(Pointage ptg) {
		this.setIdPointage(ptg.getIdPointage());
		this.setIdRefEtat(ptg.getLatestEtatPointage().getEtat().getCodeEtat());
		this.setMotif(ptg.getMotif() == null ? "" : ptg.getMotif().getText());
		this.setCommentaire(ptg.getCommentaire() == null ? "" : ptg.getCommentaire().getText());

		switch (ptg.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_HEURES:
			case NB_INDEMNITES:
				this.quantite = ptg.getQuantite();
				break;
			case PERIODE_HEURES:
				SimpleDateFormat sdfHeure = new SimpleDateFormat("HH");
				SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
				this.setHeureDebutDate(ptg.getDateDebut());
				this.setHeureDebut(sdfHeure.format(ptg.getDateDebut()));
				this.setMinuteDebut(sdfMinute.format(ptg.getDateDebut()));
				this.setHeureFinDate(ptg.getDateFin());
				this.setHeureFin(sdfHeure.format(ptg.getDateFin()));
				this.setMinuteFin(sdfMinute.format(ptg.getDateFin()));
				Calendar calFinSaisieJ1 = Calendar.getInstance();
				calFinSaisieJ1.setTime(ptg.getDateFin());
				Integer jourFin = calFinSaisieJ1.get(Calendar.DAY_OF_YEAR);
				Calendar calDebutSaisieJ1 = Calendar.getInstance();
				calDebutSaisieJ1.setTime(ptg.getDateDebut());
				Integer jourDebut = calDebutSaisieJ1.get(Calendar.DAY_OF_YEAR);
				if (!jourFin.equals(jourDebut)) {
					this.setSaisieJ1("Attention fin de saisie j+1");
				} else {
					this.setSaisieJ1(null);
				}
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

	public String getAide() {
		return aide;
	}

	public void setAide(String aide) {
		this.aide = aide;
	}

}
