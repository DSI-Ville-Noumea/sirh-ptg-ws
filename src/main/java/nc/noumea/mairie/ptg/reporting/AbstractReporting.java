package nc.noumea.mairie.ptg.reporting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import nc.noumea.mairie.ptg.reporting.vo.CellVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;

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
}
