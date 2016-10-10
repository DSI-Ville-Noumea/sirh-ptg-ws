package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.reporting.vo.CellVo;
import nc.noumea.mairie.ptg.service.IPointageService;


@Service("FichePointageHebdoReporting")
public class FichePointageHebdoReporting extends AbstractReporting {

	private SimpleDateFormat sdfddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat sdfNomJour = new SimpleDateFormat("EEEE");
	private SimpleDateFormat sdfHHmm = new SimpleDateFormat("HH:mm");

	@Autowired
	private IPointageService pointageService;

	public byte[] getFichePointageHebdoReporting(String csvIdAgent, Date dateLundi, Integer idAgent)
			throws DocumentException, IOException {

		Document document = new Document(PageSize.A4);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);

		// on genere les metadata
		addMetaData(document, "Fiche de pointage hebdomadaire", idAgent);

		// on ouvre le document
		document.open();

		// on ecrit dans le document
		writeDocument(document, csvIdAgent, dateLundi);

		// on ferme le document
		document.close();

		// on envoie le flux
		return baos.toByteArray();
	}

	protected void writeDocument(Document document, String csvIdAgent, Date dateLundi) throws DocumentException {

		// on recupere les donnees
		FichePointageListDto fiches = pointageService.getFichesPointageForUsers(csvIdAgent, dateLundi);

		if (null != fiches) {
			for (FichePointageDto fiche : fiches.getFiches()) {
				// le titre
				writeTitle(document, "Fiche de pointage hebdomadaire", null, true, true);
				// tableau informations
				writeTableauInformation(document, fiche);
				// tableau fiche hebdo
				writeTableauPointageHebdo(document, fiche);
				document.newPage();
			}
		}

	}

	protected void writeTableauPointageHebdo(Document document, FichePointageDto fiche) throws DocumentException {

		PdfPTable table = writeTableau(document, new float[] { 2, 2, 2, 2, 2, 2, 2, 2 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		// 1er ligne : DATE
		List<CellVo> listValuesLigne1 = new ArrayList<CellVo>();
		listValuesLigne1.add(new CellVo(""));
		for (JourPointageDto jour : fiche.getSaisies()) {
			listValuesLigne1.add(new CellVo(sdfddMMyyyy.format(jour.getDate()), true, 1, new BaseColor(233, 233, 233)));
		}
		writeLine(table, 3, Element.ALIGN_CENTER, listValuesLigne1);

		// 2e ligne : nom jour
		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		listValuesLigne2.add(new CellVo("", false));
		for (JourPointageDto jour : fiche.getSaisies()) {
			String nomJour = sdfNomJour.format(jour.getDate());
			nomJour = nomJour.substring(0, 1).toUpperCase() + nomJour.substring(1, nomJour.length());
			listValuesLigne2.add(new CellVo(nomJour, true, 1, new BaseColor(233, 233, 233)));
		}
		writeLine(table, 3, Element.ALIGN_CENTER, listValuesLigne2);

		// on ecrit les primes
		writePrimes(table, fiche);
		// on ecrit les heures supp.
		writeHeuresSup(table, fiche);
		// on ecrit les absences
		writeAbsences(table, fiche);

		document.add(table);
	}

	protected void writeHeuresSup(PdfPTable table, FichePointageDto fiche) {

		// 3e ligne : titre Heures Supplementaires
		writeLine(table, 3, Element.ALIGN_CENTER,
				Arrays.asList(new CellVo(""), new CellVo("Heures supplémentaires", false, 7, new BaseColor(255, 215, 196))));

		// on traite les donnees
		Integer nombreLigneHeuresSup = 1;
		for (JourPointageDto jour : fiche.getSaisies()) {
			nombreLigneHeuresSup = jour.getHeuresSup().size() > nombreLigneHeuresSup ? jour.getHeuresSup().size()
					: nombreLigneHeuresSup;
		}

		List<CellVo> ligneHeureDebut = new ArrayList<CellVo>();
		ligneHeureDebut.add(new CellVo("Heure de début", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneHeureFin = new ArrayList<CellVo>();
		ligneHeureFin.add(new CellVo("Heure de fin", 1, Element.ALIGN_LEFT));
		List<CellVo> lignePayeRecupere = new ArrayList<CellVo>();
		lignePayeRecupere.add(new CellVo("Payé/récupéré", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneMotif = new ArrayList<CellVo>();
		ligneMotif.add(new CellVo("Motif", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneCommentaire = new ArrayList<CellVo>();
		ligneCommentaire.add(new CellVo("Commentaire", 1, Element.ALIGN_LEFT));

		for (JourPointageDto jour : fiche.getSaisies()) {
			for (int i = 0; i < nombreLigneHeuresSup; i++) {
				if (i < jour.getHeuresSup().size() && null != jour.getHeuresSup().get(i)) {
					ligneHeureDebut.add(new CellVo(sdfHHmm.format(jour.getHeuresSup().get(i).getHeureDebut())));
					ligneHeureFin.add(new CellVo(sdfHHmm.format(jour.getHeuresSup().get(i).getHeureFin())));
					lignePayeRecupere.add(new CellVo(jour.getHeuresSup().get(i).getRecuperee() ? "Récupéré" : "Payé"));
					ligneMotif.add(new CellVo(jour.getHeuresSup().get(i).getMotif()));
					ligneCommentaire.add(new CellVo(jour.getHeuresSup().get(i).getCommentaire()));
				} else {
					ligneHeureDebut.add(new CellVo(""));
					ligneHeureFin.add(new CellVo(""));
					lignePayeRecupere.add(new CellVo(""));
					ligneMotif.add(new CellVo(""));
					ligneCommentaire.add(new CellVo(""));
				}
			}
		}

		// 1e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureDebut);
		// 2e ligne : Heure de fin
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureFin);
		// 3e ligne : payé/recup
		writeLine(table, 3, Element.ALIGN_CENTER, lignePayeRecupere);
		// 4e ligne : motif
		writeLine(table, 3, Element.ALIGN_CENTER, ligneMotif);
		// 5e ligne : commentaire
		writeLine(table, 3, Element.ALIGN_CENTER, ligneCommentaire);
	}

	protected void writeAbsences(PdfPTable table, FichePointageDto fiche) {

		// 3e ligne : titre Absences
		writeLine(table, 3, Arrays.asList(new CellVo(""), new CellVo("Absences", false, 7, new BaseColor(255, 215, 196),
				Element.ALIGN_CENTER)));

		// on traite les donnees
		Integer nombreLigneAbsence = 1;
		for (JourPointageDto jour : fiche.getSaisies()) {
			nombreLigneAbsence = jour.getAbsences().size() > nombreLigneAbsence ? jour.getAbsences().size()
					: nombreLigneAbsence;
		}

		List<CellVo> ligneHeureDebut = new ArrayList<CellVo>();
		ligneHeureDebut.add(new CellVo("Heure de début", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneHeureFin = new ArrayList<CellVo>();
		ligneHeureFin.add(new CellVo("Heure de fin", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneTypeAbsence = new ArrayList<CellVo>();
		ligneTypeAbsence.add(new CellVo("Type absence", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneMotif = new ArrayList<CellVo>();
		ligneMotif.add(new CellVo("Motif", 1, Element.ALIGN_LEFT));
		List<CellVo> ligneCommentaire = new ArrayList<CellVo>();
		ligneCommentaire.add(new CellVo("Commentaire", 1, Element.ALIGN_LEFT));

		for (JourPointageDto jour : fiche.getSaisies()) {
			for (int i = 0; i < nombreLigneAbsence; i++) {
				if (i < jour.getAbsences().size() && null != jour.getAbsences().get(i)) {
					ligneHeureDebut.add(new CellVo(sdfHHmm.format(jour.getAbsences().get(i).getHeureDebut())));
					ligneHeureFin.add(new CellVo(sdfHHmm.format(jour.getAbsences().get(i).getHeureFin())));
					ligneTypeAbsence.add(new CellVo(RefTypeAbsenceEnum.getRefTypeAbsenceEnum(
							jour.getAbsences().get(i).getIdRefTypeAbsence()).toString()));
					ligneMotif.add(new CellVo(jour.getAbsences().get(i).getMotif()));
					ligneCommentaire.add(new CellVo(jour.getAbsences().get(i).getCommentaire()));
				} else {
					ligneHeureDebut.add(new CellVo(""));
					ligneHeureFin.add(new CellVo(""));
					ligneTypeAbsence.add(new CellVo(""));
					ligneMotif.add(new CellVo(""));
					ligneCommentaire.add(new CellVo(""));
				}
			}
		}

		// 1e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureDebut);
		// 2e ligne : Heure de fin
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureFin);
		// 3e ligne : typeAbsence
		writeLine(table, 3, Element.ALIGN_CENTER, ligneTypeAbsence);
		// 4e ligne : motif
		writeLine(table, 3, Element.ALIGN_CENTER, ligneMotif);
		// 5e ligne : commentaire
		writeLine(table, 3, Element.ALIGN_CENTER, ligneCommentaire);
	}

	protected void writePrimes(PdfPTable table, FichePointageDto fiche) {

		// on traite les donnees
		Integer nombreLignePrimes = fiche.getSaisies().get(0).getPrimes().size();
		for (int prime = 0; prime < nombreLignePrimes; prime++) {
			// 3e ligne : titre Primes
			writeLine(
					table,
					3,
					Element.ALIGN_CENTER,
					Arrays.asList(new CellVo(""), new CellVo(fiche.getSaisies().get(0).getPrimes().get(prime)
							.getTitre(), false, 7, new BaseColor(255, 215, 196))));

			List<CellVo> ligneNombre = new ArrayList<CellVo>();
			ligneNombre.add(new CellVo("Nombre", 1, Element.ALIGN_LEFT));
			List<CellVo> ligneMotif = new ArrayList<CellVo>();
			ligneMotif.add(new CellVo("Motif", 1, Element.ALIGN_LEFT));
			List<CellVo> ligneCommentaire = new ArrayList<CellVo>();
			ligneCommentaire.add(new CellVo("Commentaire", 1, Element.ALIGN_LEFT));

			for (@SuppressWarnings("unused")
			JourPointageDto jour : fiche.getSaisies()) {
				ligneNombre.add(new CellVo(""));
				ligneMotif.add(new CellVo(""));
				ligneCommentaire.add(new CellVo(""));
			}

			// 1e ligne : quantite
			writeLine(table, 3, Element.ALIGN_CENTER, ligneNombre);
			// 2e ligne : motif
			writeLine(table, 3, Element.ALIGN_CENTER, ligneMotif);
			// 3e ligne : commentaire
			writeLine(table, 3, Element.ALIGN_CENTER, ligneCommentaire);
		}
	}

	protected void writeTableauInformation(Document document, FichePointageDto fiche) throws DocumentException {

		PdfPTable table = writeTableau(document, new float[] { 1, 7 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		writeLine(table, 3, Element.ALIGN_LEFT, Arrays.asList(
				new CellVo("Entité :", true, 1, new BaseColor(233, 233, 233)), new CellVo(fiche.getAgent().getService())));
		writeLine(
				table,
				3,
				Element.ALIGN_LEFT,
				Arrays.asList(new CellVo("Agent :", true, 1, new BaseColor(233, 233, 233)), new CellVo(fiche.getAgent()
						.getNomatr() + " - " + fiche.getAgent().getNom() + " " + fiche.getAgent().getPrenom())));
		writeLine(table, 3, Element.ALIGN_LEFT, Arrays.asList(
				new CellVo("Semaine :", true, 1, new BaseColor(233, 233, 233)), new CellVo(fiche.getSemaine())));
		document.add(table);
	}
}
