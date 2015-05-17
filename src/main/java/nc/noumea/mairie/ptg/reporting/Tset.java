package nc.noumea.mairie.ptg.reporting;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

public class Tset extends PdfPageEventHelper {

	public static void main(String[] args) throws DocumentException, IOException {

		// on recupere le xml

		// EtatPayeurDto result = new
		// ExportEtatPayeurService().getEtatPayeurDataForStatut(AgentStatutEnum.F);
		// TODO Auto-generated method stub
		EtatPayeurDto dto = new EtatPayeurDto();
		dto.setChainePaie("SHC");
		dto.setPeriode("mars 2015");

		Document document = new Document(PageSize.A4.rotate());

		// http://blog.infin-it.fr/2010/08/05/sample-generation-de-document-pdf-avec-itext-1ere-partie/
		// http://www.jmdoudoux.fr/java/dej/chap-generation-documents.htm
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, baos);

		document.addTitle("Hello World");

		document.addAuthor("JM doudoux");

		document.addSubject("Exemple de génération de PDF.");

		// il faut mettre les header avant l'ouverture du document.
		Paragraph para = new Paragraph();
		para.add("Entete du fichier PDF");
		System.out.println("ici");
		HeaderFooter header = new HeaderFooter(para, false);

		header.setAlignment(HeaderFooter.ALIGN_CENTER);
		document.setHeader(header);

		document.open();
		String chainePaie = "HORS CONVENTION";
		if (dto.getChainePaie().equals("SHC")) {
			chainePaie = "CONVENTION COLLECTIVE";
		}
		String titre = "ETAT DES ELEMENTS DE SALAIRE " + chainePaie + " A PAYER SUR " + dto.getPeriode().toUpperCase();

		Image logo = Image.getInstance("logo_mairie.png");
		logo.scaleToFit(30, 30);

		Paragraph paragraph2 = new Paragraph(titre);
		paragraph2.add(logo);
		paragraph2.setAlignment(Element.ALIGN_CENTER);
		document.add(paragraph2);

		// document.add(new Paragraph("3eme paragraphe mais en couleur", new
		// Font(Font.COURIER, 28, Font.BOLD,
		// Color.RED)));

		// Phrase phrase = new Phrase(new
		// Chunk("Phrase : 4eme en Chunck "));
		// phrase.add(new
		// Chunk(" test de phrase dont la longueur dépasse largement une seule ligne"));
		// phrase.add(new Chunk(" grace à un commentaire assez long"));
		// document.add(phrase);
		//
		// Chapter chapter = new Chapter(new Paragraph("Premier chapitre"),
		// 1);
		// Paragraph paragraph3 = new
		// Paragraph("ligne 1 test de phrase du chapitre");
		// chapter.add(paragraph3);
		// paragraph3 = new Paragraph("ligne 2 test de phrase du chapitre");
		// chapter.add(paragraph3);
		// document.add(chapter);
		//
		// Paragraph paragraph = new
		// Paragraph("Paragraphe avant nouvelle page");
		// document.add(paragraph);
		// document.newPage();
		// document.add(Chunk.NEWLINE);
		// document.newPage();
		// paragraph = new Paragraph("Paragraphe apres nouvelle page");
		// document.add(paragraph);

		Table tableau = new Table(2, 2);
		tableau.addCell("1.1");
		tableau.addCell("1.2");
		tableau.addCell("2.1");
		tableau.addCell("2.2");
		document.add(tableau);

		document.add(Chunk.NEWLINE);

		PdfPTable table = new PdfPTable(new float[] { 2, 1, 2, 5, 1 });
		table.setWidthPercentage(100f);
		table.getDefaultCell().setPadding(3);
		table.getDefaultCell().setUseAscender(true);
		table.getDefaultCell().setUseDescender(true);
		table.getDefaultCell().setColspan(5);
		table.getDefaultCell().setBackgroundColor(Color.RED);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell("nono");
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		table.getDefaultCell().setColspan(1);
		table.getDefaultCell().setBackgroundColor(Color.ORANGE);
		for (int i = 0; i < 2; i++) {
			table.addCell("Location");
			table.addCell("Time");
			table.addCell("Run Length");
			table.addCell("Title");
			table.addCell("Year");
		}
		table.getDefaultCell().setBackgroundColor(null);
		table.setHeaderRows(3);
		table.setFooterRows(1);
		for (int i = 0; i < 3; i++) {
			table.addCell(String.valueOf(i));
			table.addCell("test");
			table.addCell(String.format("%d '", i));
			table.addCell(String.valueOf(i));
			table.addCell(String.valueOf(i));
		}
		document.add(table);
		document.newPage();

		Table tableau2 = new Table(2, 2);
		tableau2.addCell("1.1");
		tableau2.addCell("1.2");
		tableau2.addCell("2.1");
		tableau2.addCell("2.2");
		document.add(tableau2);

//		// on genere les numeros de page
//		PdfReader reader = new PdfReader(baos.toByteArray());
//		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("/home/nicno85/Bureau/etatPayeur.pdf"));
//		PdfContentByte under = null;
//		int totalPages = reader.getNumberOfPages();
//		for (int page = 1; page <= totalPages; page++) {
//			under = stamper.getUnderContent(page);
//			String pageXofY = String.format("Page %d of %d", page, totalPages);
//			ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(pageXofY), 2, 2, 0);
//		}
//		reader.close();
//		stamper.close();
//		


		// on ferme le document
		document.close();
	}
}
