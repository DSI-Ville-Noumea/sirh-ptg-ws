package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import nc.noumea.mairie.ptg.reporting.vo.CellVo;

public abstract class AbstractReporting extends PdfPageEventHelper {

	private Logger logger = LoggerFactory.getLogger(AbstractReporting.class);

	protected Font fontNormal8 = FontFactory.getFont("Arial", 8, Font.NORMAL);
	protected Font fontNormal10 = FontFactory.getFont("Arial", 9, Font.NORMAL);
	protected Font fontBold10 = FontFactory.getFont("Arial", 9, Font.BOLD);

	protected void writeTitle(Document document, String title, URL urlImage, boolean border, boolean isBold)
			throws DocumentException {

		PdfPCell cellLogo = null;
		if (null != urlImage) {
			Image logo = null;
			try {
				logo = Image.getInstance(urlImage);
				logo.scaleToFit(100, 100);
			} catch (BadElementException e) {
				logger.debug(e.getMessage());
			} catch (MalformedURLException e) {
				logger.debug(e.getMessage());
			} catch (IOException e) {
				logger.debug(e.getMessage());
			}

			cellLogo = new PdfPCell();
			cellLogo.addElement(logo);
			
			if(!border)
				cellLogo.setBorder(Rectangle.NO_BORDER);
		}
		Paragraph paragraph = null;
		if (isBold)
			paragraph = new Paragraph(title, fontBold10);
		else
			paragraph = new Paragraph(title);

		paragraph.setAlignment(Element.ALIGN_CENTER);

		PdfPCell cellTitre = new PdfPCell();
		cellTitre.addElement(paragraph);
		cellTitre.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cellTitre.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellTitre.setFixedHeight(40);

		if (!border)
			cellTitre.setBorder(Rectangle.NO_BORDER);

		PdfPTable table = null;
		if (null != urlImage) {
			table = new PdfPTable(new float[] { 1, 7 });
			table.addCell(cellLogo);
		} else {
			table = new PdfPTable(new float[] { 1 });
		}

		table.setWidthPercentage(100f);
		table.addCell(cellTitre);

		document.add(table);
	}

	protected PdfPTable writeTableau(Document document, float[] relativeWidth) throws DocumentException {

		PdfPTable table = new PdfPTable(relativeWidth);
		table.setWidthPercentage(100f);
		return table;
	}

	protected void writeLine(PdfPTable table, Integer padding, List<CellVo> values) {

		for (CellVo value : values) {
			table.addCell(writeCell(padding, null, value));
		}
	}

	protected void writeLine(PdfPTable table, Integer padding, Integer horizontalAlign, List<CellVo> values) {

		for (CellVo value : values) {
			table.addCell(writeCell(padding, horizontalAlign, value));
		}
	}

	protected PdfPCell writeCell(Integer padding, CellVo value) {
		return writeCell(padding, null, value);
	}

	protected PdfPCell writeCell(Integer padding, Integer horizontalAlign, CellVo value) {

		PdfPCell pdfWordCell = new PdfPCell();
		pdfWordCell.setPadding(padding);
		pdfWordCell.setUseAscender(true);
		pdfWordCell.setUseDescender(true);
		pdfWordCell.setBackgroundColor(value.getBackgroundColor());

		// /!\ COLSPAN
		pdfWordCell.setColspan(value.getColspan());
		
		// border
		if(!value.isBorder())
			pdfWordCell.setBorder(Rectangle.NO_BORDER);
		
		// on cree la phrase pour le FONT
		Phrase phrase = null;
		if(null != value.getFont()) {
			phrase = new Phrase(value.getText(), value.getFont());
		}else if (value.isBold()) {
			phrase = new Phrase(value.getText(), fontBold10);
		} else {
			phrase = new Phrase(value.getText(), fontNormal10);
		}
		// on cree le Paragraph pour l alignement
		Paragraph paragraph = new Paragraph(phrase);
		if(null != value.getHorizontalAlign()) {
			paragraph.setAlignment(value.getHorizontalAlign());
		}else if(null != horizontalAlign) {
			paragraph.setAlignment(horizontalAlign);
		}
		
		pdfWordCell.addElement(paragraph);

		return pdfWordCell;
	}



	protected ByteArrayOutputStream genereNumeroPageA3Paysage(ByteArrayOutputStream baosOrigin) throws FileNotFoundException, DocumentException,
			IOException {

		// Create a reader
		PdfReader reader = new PdfReader(baosOrigin.toByteArray());
		// Create a stamper
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, baos);
		// Loop over the pages and add a header to each page
		int n = reader.getNumberOfPages();
		for (int i = 1; i <= n; i++) {
			getHeaderTableA3Paysage(i, n).writeSelectedRows(0, -1, 34, 830, stamper.getOverContent(i));
		}
		// Close the stamper
		stamper.close();
		reader.close();

		return baos;
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
		if (idAuthor != null)
			document.addAuthor(idAuthor.toString());
		
		document.addSubject(titre);
	}
}
