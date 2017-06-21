package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
import nc.noumea.mairie.domain.Spperm;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.reporting.vo.CellVo;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;

@Service
public class EtatPayeurTitreRepasReporting extends AbstractReporting {

	@Autowired
	private IAgentMatriculeConverterService	agentMatriculeConverterService;

	@Autowired
	private IAlfrescoCMISService			alfrescoCMISService;

	private static final String				DESCRIPTION_ETAT_PAYEUR_TITRE_REPAS	= "Etat Payeur des Titres Repas du ";

	private SimpleDateFormat				sdfMMMMyyyy							= new SimpleDateFormat("MMMM yyyy");

	public void downloadEtatPayeurTitreRepas(TitreRepasEtatPayeur etatPayeurTR, List<TitreRepasDemandeDto> listDemandeTRConventions,List<TitreRepasDemandeDto> listDemandeTRHorsConventions, Spperm refPrime)
			throws DocumentException, MalformedURLException, IOException {

		// on crée le document
		Document document = new Document(PageSize.A4);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, baos);

		// on genere les metadata
		addMetaData(document, getTitreDocument(etatPayeurTR, null, false), etatPayeurTR.getIdAgent());

		// on ouvre le document
		document.open();

		// on ecrit la page des hors conventions (fonctionnaire/contractuels...)
		if (listDemandeTRHorsConventions.size() > 0) {
			writeDocument(document, etatPayeurTR, listDemandeTRHorsConventions, false, refPrime);
			
			// on fait un saut de page
			document.newPage();
			document.add(new Paragraph(""));
			document.newPage();

		}

		// on ecrit la page des conventions collectives
		if (listDemandeTRConventions.size() > 0) {
			writeDocument(document, etatPayeurTR, listDemandeTRConventions, true, refPrime);

			// on fait un saut de page
			document.newPage();
			document.add(new Paragraph(""));
			document.newPage();

		}

		// Insertion du récap. global
		insertGlobalSummary(document, listDemandeTRHorsConventions, listDemandeTRConventions, refPrime, etatPayeurTR);

		// on ferme le document
		document.close();

		// on génere les numeros de page
		baos = genereNumeroPageA3Portrait(writer, baos, sdfMMMMyyyy.format(etatPayeurTR.getDateEtatPayeur()));

		String node = alfrescoCMISService.uploadDocument(etatPayeurTR.getIdAgent(), baos.toByteArray(), etatPayeurTR.getFichier(),
				DESCRIPTION_ETAT_PAYEUR_TITRE_REPAS + etatPayeurTR.getDateEtatPayeur(), TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_TITRE_REPAS);

		etatPayeurTR.setNodeRefAlfresco(node);
		
	}

	private ByteArrayOutputStream genereNumeroPageA3Portrait(PdfWriter writer, ByteArrayOutputStream baos, String text)
			throws FileNotFoundException, DocumentException, IOException {

		int pageCount = writer.getPageNumber();
		PdfReader reader = new PdfReader(baos.toByteArray());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(outputStream);
		PdfStamper stamper = new PdfStamper(reader, output);
		for (int i = 1; i <= pageCount; i++) {
			//à droite le numero de page
			ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_CENTER, new Phrase(" page " + i + "/" + pageCount, fontNormal10), 550,
					30, 0);
			//à gauche le rappel du mois
			ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_CENTER, new Phrase(text, fontNormal10), 50, 30, 0);
		}
		stamper.close();
		return outputStream;
	}

	private void writeDocument(Document document, TitreRepasEtatPayeur etatPayeurTR, List<TitreRepasDemandeDto> listDemandeTR,
			boolean isConventionCollective, Spperm refPrime) throws DocumentException {

		// on ajoute le titre, le logo sur le document
		writeTitle(document, getTitreDocument(etatPayeurTR, isConventionCollective, false),
				this.getClass().getClassLoader().getResource("images/logo_mairie.png"), false, false);

		// on ecrit le tableau en récupérant le nombre d'agent réel : Si des agents sont en erreur, il ne faut pas les prendre en compte dans le calcul des sommes !!
		Integer nbAgents = writeTableau(document, listDemandeTR, refPrime);

		// on insert le récapitulatif
		String typeChainePaie = isConventionCollective ? "convention collective" : "hors convention";
		insertSummary(document, nbAgents, refPrime, typeChainePaie);

		// on ecrit : "Vérification DRH
		// le
		// Cachet"
		writeCachet(document);
	}

	private String getTitreDocument(TitreRepasEtatPayeur dto, Boolean isConventionCollective, boolean recap) {
		if (recap) {
			return "ETAT PAYEUR DES TITRES REPAS\n" + "  RECAPITULATIF - " + sdfMMMMyyyy.format(dto.getDateEtatPayeur()).toUpperCase();
		} else {
			return "ETAT PAYEUR DES TITRES REPAS\n"
					+ (isConventionCollective == null ? "" : isConventionCollective ? "CONVENTION COLLECTIVE" : "HORS CONVENTION") + " - "
					+ sdfMMMMyyyy.format(dto.getDateEtatPayeur()).toUpperCase();
		}
	}

	/**
	 * Ecrit le tableau des titres repas, et retourne le nombre d'agent valides.
	 */
	private Integer writeTableau(Document document, List<TitreRepasDemandeDto> listDemandeTR, Spperm refPrime) throws DocumentException {
		Integer nbAgentsValides = 0;

		// Le tableau de float permet de spécifier le nombre de colonnes, avec leur taille.
		PdfPTable table = writeTableau(document, new float[] { 2, 4, 2, 2, 2, 2 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		// 1er ligne : entete
		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		listValuesLigne2.add(new CellVo("Matricule", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Nom Prénom", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Service", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Part patronale", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Part salariale", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Somme part patronale + part salariale", 1, Element.ALIGN_CENTER));
		writeLine(table, 3, listValuesLigne2);

		// Calcul des parts avant la boucle sur agents, car les montants sont tous les même.
		Integer somme = (int) (refPrime.getMontantForfait() * refPrime.getMontantPlafond());
		Integer partPatr = (int) (somme * refPrime.getTauxPatronal());
		Integer partSal = (int) (somme * refPrime.getTauxSalarial());
		
		// on boucle sur les agents
		for (TitreRepasDemandeDto demandeTR : listDemandeTR) {
			// Si l'insertion a été fructueuse, on met à jour les compteurs
			if(writeLineByAgent(table, demandeTR, partPatr, partSal, somme))
				++nbAgentsValides;
		}

		document.add(table);

		return nbAgentsValides;
	}

	private Boolean writeLineByAgent(PdfPTable table, TitreRepasDemandeDto demandeTR, Integer partPatr, Integer partSal, Integer somme) {
		Boolean isInsertOk = false;
		
		List<CellVo> listValuesByAgent = new ArrayList<CellVo>();
		if (demandeTR.getAgent() == null || demandeTR.getAgent().getIdAgent() == null) {
			// on ecrit les donnees de l agent
			listValuesByAgent.add(new CellVo("Erreur sur demande " + demandeTR.getIdTrDemande(), 1, Element.ALIGN_CENTER));
			listValuesByAgent.add(new CellVo("Erreur"));
			listValuesByAgent.add(new CellVo("Erreur"));
			listValuesByAgent.add(new CellVo("Erreur"));
			listValuesByAgent.add(new CellVo("Erreur"));
			listValuesByAgent.add(new CellVo("Erreur"));
		} else {

			// on ecrit les donnees de l agent
			listValuesByAgent.add(new CellVo(agentMatriculeConverterService.tryConvertIdAgentToNomatr(demandeTR.getAgent().getIdAgent()).toString(),
					1, Element.ALIGN_CENTER));
			listValuesByAgent.add(new CellVo(demandeTR.getAgent().getNom() + " " + demandeTR.getAgent().getPrenom()));
			listValuesByAgent.add(new CellVo(demandeTR.getAgent().getSigleService()));
			listValuesByAgent.add(new CellVo(formatMillier(partPatr)));
			listValuesByAgent.add(new CellVo(formatMillier(partSal)));
			listValuesByAgent.add(new CellVo(formatMillier(somme)));

			isInsertOk = true;
		}

		writeLine(table, 3, listValuesByAgent);
		
		return isInsertOk;
	}

	private void insertSummary(Document document, Integer nbAgents, Spperm refPrime, String typeChainePaie) throws DocumentException {

		// Le tableau de float permet de spécifier le nombre de colonnes, avec leur taille.
		PdfPTable table = writeTableau(document, new float[] { 10, 3 });
		table.setSpacingBefore(5);
		table.setSpacingAfter(5);

		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		// Nombre d'agents
		listValuesLigne2.add(new CellVo("Nombre d'agents", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo(String.valueOf(nbAgents)));
		// Somme des montants
		listValuesLigne2.add(new CellVo("Total chaine de paie " + typeChainePaie, 1, Element.ALIGN_CENTER));
		int totalChainPaie = nbAgents * (int) (refPrime.getMontantForfait() * refPrime.getMontantPlafond());
		listValuesLigne2.add(new CellVo(formatMillier(totalChainPaie) + " cfp"));
		writeLine(table, 3, listValuesLigne2);

		document.add(table);
	}

	public static String formatMillier(long montantAvantVirgule) {
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		symbols.setGroupingSeparator(' ');
		DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
		return formatter.format(montantAvantVirgule);
	}

	private void insertGlobalSummary(Document document, List<TitreRepasDemandeDto> list, List<TitreRepasDemandeDto> listCC, Spperm refPrime,
			TitreRepasEtatPayeur etatPayeurTR) throws DocumentException {

		// On récupère le nombre réel d'agents ayant demandé les TR
		Integer nbTotalAgent = 0;
		for (TitreRepasDemandeDto tr : list) {
			if (tr.getAgent() != null && tr.getAgent().getIdAgent() != null)
				++nbTotalAgent;
		}
		for (TitreRepasDemandeDto tr : listCC) {
			if (tr.getAgent() != null && tr.getAgent().getIdAgent() != null)
				++nbTotalAgent;
		}
		
		// On écrit les infos
		writeTitle(document, getTitreDocument(etatPayeurTR, null, true), null, false, false);

		// Le tableau de float permet de spécifier le nombre de colonnes, avec
		// leur taille.
		PdfPTable table = writeTableau(document, new float[] { 2, 2 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		// Nombre d'agents
		listValuesLigne2.add(new CellVo("Nombre global d'agents", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo(String.valueOf(nbTotalAgent)));
		// Somme des montants
		int totalGlobal = nbTotalAgent * (int) (refPrime.getMontantForfait() * refPrime.getMontantPlafond());
		listValuesLigne2.add(new CellVo("Somme globale du montant des titres repas", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo(formatMillier(totalGlobal) + " cfp"));

		writeLine(table, 3, listValuesLigne2);

		document.add(table);
	}

	private void writeCachet(Document document) throws DocumentException {

		Phrase phrase = new Phrase("Vérification DRH \n le \n Cachet", fontNormal10);

		Paragraph paragraph = new Paragraph(phrase);

		PdfPCell cellVide = new PdfPCell();
		cellVide.setBorder(Rectangle.NO_BORDER);

		PdfPCell cellCachet = new PdfPCell();
		cellCachet.addElement(paragraph);
		cellCachet.setBorder(Rectangle.NO_BORDER);

		PdfPTable table = null;
		table = new PdfPTable(new float[] { 12, 6 });

		table.setWidthPercentage(100f);
		table.addCell(cellVide);
		table.addCell(cellCachet);

		document.add(table);
	}
}
