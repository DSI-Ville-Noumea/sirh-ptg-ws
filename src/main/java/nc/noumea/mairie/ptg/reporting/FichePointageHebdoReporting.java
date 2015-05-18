package nc.noumea.mairie.ptg.reporting;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.reporting.vo.CellVo;
import nc.noumea.mairie.ptg.service.IPointageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

@Service("FichePointageHebdoReporting")
public class FichePointageHebdoReporting extends AbstractReporting {

	private SimpleDateFormat sdfddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat sdfNomJour = new SimpleDateFormat("E");
	private SimpleDateFormat sdfHHmm = new SimpleDateFormat("HH:mm");

	@Autowired
	private IPointageService pointageService;

	public void getFichePointageHebdoReporting() throws DocumentException, IOException {

		EtatPayeurDto dto = new EtatPayeurDto();
		dto.setChainePaie("SHC");
		dto.setPeriode("mars 2015");

		Document document = new Document(PageSize.A4);

		// http://blog.infin-it.fr/2010/08/05/sample-generation-de-document-pdf-avec-itext-1ere-partie/
		// http://www.jmdoudoux.fr/java/dej/chap-generation-documents.htm
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);

		// on ouvre le document
		document.open();

		// on ecrit dans le document
		writeDocument(document);

		// on ferme le document
		document.close();

		// on gere le nombre de page avant la fermeture du document
		// Create a reader
		PdfReader reader = new PdfReader(baos.toByteArray());
		// Create a stamper
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(
				"/home/rebjo84/Bureau/FichePointageHebdoReporting.pdf"));

		// Close the stamper
		stamper.close();
		reader.close();
	}

	protected void writeDocument(Document document) throws DocumentException {

		// le titre
		writeTitle(document, "Fiche de pointage hebdomadaire",
				this.getClass().getClassLoader().getResource("images/logo_mairie.png"));

		// on recupere les donnees
		FichePointageListDto fiches = pointageService.getFichesPointageForUsers("9004117", new Date());

		if (null != fiches) {
			for (FichePointageDto fiche : fiches.getFiches()) {
				// tableau informations
				writeTableauInformation(document, fiches.getFiches().get(0));
				// tableau fiche hebdo
				writeTableauPointageHebdo(document, fiche);
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
			listValuesLigne1.add(new CellVo(sdfddMMyyyy.format(jour.getDate()), true, 1, Color.GRAY));
		}
		writeLine(table, 3, Element.ALIGN_CENTER, listValuesLigne1);

		// 2e ligne : nom jour
		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		listValuesLigne2.add(new CellVo("", false));
		for (JourPointageDto jour : fiche.getSaisies()) {
			listValuesLigne2.add(new CellVo(sdfNomJour.format(jour.getDate()), true, 1, Color.GRAY));
		}
		writeLine(table, 3, Element.ALIGN_CENTER, listValuesLigne2);

		// on ecrit les heures supp.
		writeHeuresSup(table, fiche);
		// on ecrit les absences
		writeAbsences(table, fiche);

		document.add(table);
	}

	protected void writeHeuresSup(PdfPTable table, FichePointageDto fiche) {

		// 3e ligne : titre Heures Supplementaires
		writeLine(table, 3, Element.ALIGN_CENTER,
				Arrays.asList(new CellVo(""), new CellVo("Heures Supplémentaires", true, 7, Color.ORANGE)));

		// on traite les donnees
		Integer nombreLigneHeuresSup = 1;
		for (JourPointageDto jour : fiche.getSaisies()) {
			nombreLigneHeuresSup = jour.getHeuresSup().size() > nombreLigneHeuresSup ? jour.getHeuresSup().size()
					: nombreLigneHeuresSup;
		}

		List<CellVo> ligneHeureDebut = new ArrayList<CellVo>();
		ligneHeureDebut.add(new CellVo("Heure de début"));
		List<CellVo> ligneHeureFin = new ArrayList<CellVo>();
		ligneHeureFin.add(new CellVo("Heure de fin"));
		List<CellVo> lignePayeRecupere = new ArrayList<CellVo>();
		lignePayeRecupere.add(new CellVo("Payé/Récupéré"));
		List<CellVo> ligneMotif = new ArrayList<CellVo>();
		ligneMotif.add(new CellVo("Motif"));
		List<CellVo> ligneCommentaire = new ArrayList<CellVo>();
		ligneCommentaire.add(new CellVo("Commentaire"));

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
		// 2e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureFin);
		// 3e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, lignePayeRecupere);
		// 4e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneMotif);
		// 5e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneCommentaire);
	}

	protected void writeAbsences(PdfPTable table, FichePointageDto fiche) {

		// 3e ligne : titre Heures Supplementaires
		writeLine(table, 3, Element.ALIGN_CENTER,
				Arrays.asList(new CellVo(""), new CellVo("Absences", true, 7, Color.ORANGE)));

		// on traite les donnees
		Integer nombreLigneAbsence = 1;
		for (JourPointageDto jour : fiche.getSaisies()) {
			nombreLigneAbsence = jour.getAbsences().size() > nombreLigneAbsence ? jour.getAbsences().size()
					: nombreLigneAbsence;
		}

		List<CellVo> ligneHeureDebut = new ArrayList<CellVo>();
		ligneHeureDebut.add(new CellVo("Heure de début"));
		List<CellVo> ligneHeureFin = new ArrayList<CellVo>();
		ligneHeureFin.add(new CellVo("Heure de fin"));
		List<CellVo> lignePayeRecupere = new ArrayList<CellVo>();
		lignePayeRecupere.add(new CellVo("TypeAbsence"));
		List<CellVo> ligneMotif = new ArrayList<CellVo>();
		ligneMotif.add(new CellVo("Motif"));
		List<CellVo> ligneCommentaire = new ArrayList<CellVo>();
		ligneCommentaire.add(new CellVo("Commentaire"));

		for (JourPointageDto jour : fiche.getSaisies()) {
			for (int i = 0; i < nombreLigneAbsence; i++) {
				if (i < jour.getAbsences().size() && null != jour.getAbsences().get(i)) {
					ligneHeureDebut.add(new CellVo(sdfHHmm.format(jour.getAbsences().get(i).getHeureDebut())));
					ligneHeureFin.add(new CellVo(sdfHHmm.format(jour.getAbsences().get(i).getHeureFin())));
					lignePayeRecupere.add(new CellVo(RefTypeAbsenceEnum.getRefTypeAbsenceEnum(
							jour.getAbsences().get(i).getIdRefTypeAbsence()).toString()));
					ligneMotif.add(new CellVo(jour.getAbsences().get(i).getMotif()));
					ligneCommentaire.add(new CellVo(jour.getAbsences().get(i).getCommentaire()));
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
		// 2e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureFin);
		// 3e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, lignePayeRecupere);
		// 4e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneMotif);
		// 5e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneCommentaire);
	}

	protected void writePrimes(PdfPTable table, FichePointageDto fiche) {

		// 3e ligne : titre Heures Supplementaires
		writeLine(table, 3, Element.ALIGN_CENTER,
				Arrays.asList(new CellVo(""), new CellVo("Primes", true, 7, Color.ORANGE)));

		// on traite les donnees
		Integer nombreLigneAbsence = 1;
		for (JourPointageDto jour : fiche.getSaisies()) {
			nombreLigneAbsence = jour.getAbsences().size() > nombreLigneAbsence ? jour.getAbsences().size()
					: nombreLigneAbsence;
		}

		List<CellVo> ligneHeureDebut = new ArrayList<CellVo>();
		ligneHeureDebut.add(new CellVo("Heure de début"));
		List<CellVo> ligneHeureFin = new ArrayList<CellVo>();
		ligneHeureFin.add(new CellVo("Heure de fin"));
		List<CellVo> lignePayeRecupere = new ArrayList<CellVo>();
		lignePayeRecupere.add(new CellVo("TypeAbsence"));
		List<CellVo> ligneMotif = new ArrayList<CellVo>();
		ligneMotif.add(new CellVo("Motif"));
		List<CellVo> ligneCommentaire = new ArrayList<CellVo>();
		ligneCommentaire.add(new CellVo("Commentaire"));

		for (JourPointageDto jour : fiche.getSaisies()) {
			for (int i = 0; i < nombreLigneAbsence; i++) {
				if (i < jour.getAbsences().size() && null != jour.getAbsences().get(i)) {
					ligneHeureDebut.add(new CellVo(sdfHHmm.format(jour.getAbsences().get(i).getHeureDebut())));
					ligneHeureFin.add(new CellVo(sdfHHmm.format(jour.getAbsences().get(i).getHeureFin())));
					lignePayeRecupere.add(new CellVo(RefTypeAbsenceEnum.getRefTypeAbsenceEnum(
							jour.getAbsences().get(i).getIdRefTypeAbsence()).toString()));
					ligneMotif.add(new CellVo(jour.getAbsences().get(i).getMotif()));
					ligneCommentaire.add(new CellVo(jour.getAbsences().get(i).getCommentaire()));
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
		// 2e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneHeureFin);
		// 3e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, lignePayeRecupere);
		// 4e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneMotif);
		// 5e ligne : Heure de debut
		writeLine(table, 3, Element.ALIGN_CENTER, ligneCommentaire);
	}

	protected void writeTableauInformation(Document document, FichePointageDto fiche) throws DocumentException {

		PdfPTable table = writeTableau(document, new float[] { 2, 6 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		writeLine(table, 3, Element.ALIGN_LEFT,
				Arrays.asList(new CellVo("Entité :", true), new CellVo(fiche.getAgent().getService())));
		writeLine(
				table,
				3,
				Element.ALIGN_LEFT,
				Arrays.asList(new CellVo("Agent :", true), new CellVo(fiche.getAgent().getNomatr() + " - "
						+ fiche.getAgent().getNom() + " " + fiche.getAgent().getPrenom())));
		writeLine(table, 3, Element.ALIGN_LEFT,
				Arrays.asList(new CellVo("Semaine :", true), new CellVo(fiche.getSemaine())));
		document.add(table);
	}
}
