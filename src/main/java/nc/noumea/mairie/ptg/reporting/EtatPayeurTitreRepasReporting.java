package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
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

	public void downloadEtatPayeurTitreRepas(TitreRepasEtatPayeur etatPayeurTR, List<TitreRepasDemandeDto> listDemandeTR)
			throws DocumentException, MalformedURLException, IOException {

		// on crée le document
		Document document = new Document(PageSize.A4);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);

		// on genere les metadata
		addMetaData(document, getTitreDocument(etatPayeurTR), etatPayeurTR.getIdAgent());

		// on ouvre le document
		document.open();

		// on ecrit dans le document
		writeDocument(document, etatPayeurTR, listDemandeTR);

		// on ferme le document
		document.close();

		// on génere les numeros de page
		baos = genereNumeroPageA3Paysage(baos);

		String node = alfrescoCMISService.uploadDocument(etatPayeurTR.getIdAgent(), baos.toByteArray(), etatPayeurTR.getFichier(),
				DESCRIPTION_ETAT_PAYEUR_TITRE_REPAS + etatPayeurTR.getDateEtatPayeur(), TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_TITRE_REPAS);

		etatPayeurTR.setNodeRefAlfresco(node);
	}

	private void writeDocument(Document document, TitreRepasEtatPayeur etatPayeurTR, List<TitreRepasDemandeDto> listDemandeTR)
			throws DocumentException {

		// on ajoute le titre, le logo sur le document
		writeTitle(document, getTitreDocument(etatPayeurTR), this.getClass().getClassLoader().getResource("images/logo_mairie.png"), false, false);

		// on ecrit le tableau
		writeTableau(document, listDemandeTR);

		// on ecrit : "Vérification DRH
		// le
		// Cachet"
		writeCachet(document);
	}

	private String getTitreDocument(TitreRepasEtatPayeur dto) {
		return "ETAT PAYEUR DES TITRES REPAS SUR " + sdfMMMMyyyy.format(dto.getDateEtatPayeur());
	}

	private void writeTableau(Document document, List<TitreRepasDemandeDto> listDemandeTR) throws DocumentException {

		PdfPTable table = writeTableau(document, new float[] { 2, 5, 3, 3 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		// 1er ligne : entete
		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		listValuesLigne2.add(new CellVo("Matricule", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Nom Prénom", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Service", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Titre Repas", 1, Element.ALIGN_CENTER));
		writeLine(table, 3, listValuesLigne2);

		// on boucle sur les agents
		for (TitreRepasDemandeDto demandeTR : listDemandeTR) {
			writeLineByAgent(table, demandeTR);
		}

		document.add(table);
	}

	private void writeLineByAgent(PdfPTable table, TitreRepasDemandeDto demandeTR) {

		List<CellVo> listValuesByAgent = new ArrayList<CellVo>();

		// on ecrit les donnees de l agent
		listValuesByAgent.add(new CellVo(agentMatriculeConverterService.tryConvertIdAgentToNomatr(demandeTR.getAgent().getIdAgent()).toString(), 1,
				Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(demandeTR.getAgent().getNom() + " " + demandeTR.getAgent().getPrenom()));
		listValuesByAgent.add(new CellVo(demandeTR.getAgent().getSigleService()));
		listValuesByAgent.add(new CellVo(demandeTR.getCommande() ? "Oui" : "Non", 1, Element.ALIGN_CENTER));

		writeLine(table, 3, listValuesByAgent);
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
