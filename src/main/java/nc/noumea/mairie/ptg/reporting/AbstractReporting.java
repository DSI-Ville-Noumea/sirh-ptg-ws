package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import nc.noumea.mairie.ptg.reporting.vo.CellVo;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public abstract class AbstractReporting extends PdfPageEventHelper {

	private Logger logger = LoggerFactory.getLogger(AbstractReporting.class);

	private Font fontNormal = FontFactory.getFont("Arial", 8, Font.NORMAL);
	private Font fontBold = FontFactory.getFont("Arial", 8, Font.BOLD);

	protected void writeTitle(Document document, String title, URL urlImage) throws DocumentException {

		Image logo = null;
		try {
			logo = Image.getInstance(urlImage);
			logo.scaleToFit(50, 50);
		} catch (BadElementException e) {
			logger.debug(e.getMessage());
		} catch (MalformedURLException e) {
			logger.debug(e.getMessage());
		} catch (IOException e) {
			logger.debug(e.getMessage());
		}

		Paragraph paragraph = new Paragraph(title);
		paragraph.add(logo);
		paragraph.setAlignment(Element.ALIGN_CENTER);

		document.add(paragraph);
	}

	protected PdfPTable writeTableau(Document document, float[] relativeWidth) throws DocumentException {

		PdfPTable table = new PdfPTable(relativeWidth);
		table.setWidthPercentage(100f);
		return table;
	}

	protected void writeLine(PdfPTable table, Integer padding, int horizontalAlign, List<CellVo> values) {

		for (CellVo value : values) {
			table.addCell(writeCell(padding, horizontalAlign, value));
		}
	}

	protected PdfPCell writeCell(Integer padding, int horizontalAlign, CellVo value) {

		PdfPCell pdfWordCell = new PdfPCell();
		pdfWordCell.setPadding(padding);
		pdfWordCell.setUseAscender(true);
		pdfWordCell.setUseDescender(true);
		pdfWordCell.setBackgroundColor(value.getBackgroundColor());
		pdfWordCell.setHorizontalAlignment(horizontalAlign);

		// /!\ COLSPAN
		pdfWordCell.setColspan(value.getColspan());

		if (value.isBold()) {
			pdfWordCell.addElement(new Phrase(value.getText(), fontBold));
		} else {
			pdfWordCell.addElement(new Phrase(value.getText(), fontNormal));
		}

		return pdfWordCell;
	}

	protected void genereNumeroPageA3Paysage(String chemin) throws FileNotFoundException, DocumentException,
			IOException {

		// Create a reader
		PdfReader reader = new PdfReader(chemin);
		// Create a stamper
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, baos);
		// Loop over the pages and add a header to each page
		int n = reader.getNumberOfPages();
		for (int i = 1; i <= n; i++) {
			getHeaderTableA3Paysage(i, n).writeSelectedRows(0, -1, 34, 800, stamper.getOverContent(i));
		}
		// Close the stamper
		stamper.close();
		reader.close();
		FileOutputStream fileoutputstream = new FileOutputStream(chemin);
		IOUtils.write(baos.toByteArray(), fileoutputstream);
		fileoutputstream.close();

	}

	private PdfPTable getHeaderTableA3Paysage(int x, int y) {
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(PageSize.A3.rotate().getWidth() - 100);
		table.setLockedWidth(true);
		table.getDefaultCell().setFixedHeight(20);
		table.getDefaultCell().setBorder(0);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(String.format("Page %d / %d", x, y));
		return table;
	}

	protected void addMetaData(Document document, String titre, Integer idAuthor) {

		document.addTitle(titre);
		document.addAuthor(idAuthor.toString());
		document.addSubject("Etat du payeur");

	}
}
