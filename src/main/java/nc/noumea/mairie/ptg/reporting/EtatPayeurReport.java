package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;

import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class EtatPayeurReport extends AbstractReporting {

	@Autowired
	private IExportEtatPayeurService exportEtatPayeurService;

	@Autowired
	@Qualifier("sirhFileEtatPayeurPathWrite")
	private String storagePathEcriture;

	public void downloadEtatPayeurByStatut(AgentStatutEnum statut, EtatPayeur ep) throws DocumentException,
			MalformedURLException, IOException {

		// on recupere le DTO
		EtatPayeurDto result = exportEtatPayeurService.getEtatPayeurDataForStatut(statut);
		if (result.getStatut() != null) {
			// on crée le document
			Document document = new Document(PageSize.A3.rotate());
			PdfWriter.getInstance(document, new FileOutputStream(Paths.get(storagePathEcriture, ep.getFichier())
					.toString()));
			String titre = getTitreDocument(result);

			document.addTitle(titre);
			document.addAuthor(ep.getIdAgent().toString());
			document.addSubject("Etat du payeur");
			document.open();

			// on ajoute le titre, le logo sur le document
			Paragraph paragraph2 = new Paragraph(titre);
			paragraph2.setAlignment(Element.ALIGN_CENTER);
			Image logo = Image.getInstance(this.getClass().getClassLoader().getResource("images/logo_mairie.png"));
			logo.scaleToFit(50, 50);
			paragraph2.add(logo);
			document.add(paragraph2);

			// TODO Auto-generated method stub

			// on ferme le document
			document.close();
			// on génere les numeros de page
			genereNumeroPage(ep);
		}

	}

	private void genereNumeroPage(EtatPayeur ep) throws FileNotFoundException, DocumentException, IOException {

		// Create a reader
		PdfReader reader = new PdfReader(Paths.get(storagePathEcriture, ep.getFichier()).toString());
		// Create a stamper
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfStamper stamper = new PdfStamper(reader, baos);
		// Loop over the pages and add a header to each page
		int n = reader.getNumberOfPages();
		for (int i = 1; i <= n; i++) {
			getHeaderTable(i, n).writeSelectedRows(0, -1, 34, 800, stamper.getOverContent(i));
		}
		// Close the stamper
		stamper.close();
		reader.close();
		FileOutputStream fileoutputstream = new FileOutputStream(Paths.get(storagePathEcriture, ep.getFichier())
				.toString());
		IOUtils.write(baos.toByteArray(), fileoutputstream);
		fileoutputstream.close();

	}

	private PdfPTable getHeaderTable(int x, int y) {
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(PageSize.A3.rotate().getWidth() - 100);
		table.setLockedWidth(true);
		table.getDefaultCell().setFixedHeight(20);
		table.getDefaultCell().setBorder(0);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(String.format("Page %d / %d", x, y));
		return table;
	}

	private String getTitreDocument(EtatPayeurDto dto) {
		String chainePaie = "HORS CONVENTION";
		if (dto.getChainePaie().equals("SHC")) {
			chainePaie = "CONVENTION COLLECTIVE";
		}
		return "ETAT DES ELEMENTS DE SALAIRE " + chainePaie + " A PAYER SUR " + dto.getPeriode().toUpperCase();
	}

}
