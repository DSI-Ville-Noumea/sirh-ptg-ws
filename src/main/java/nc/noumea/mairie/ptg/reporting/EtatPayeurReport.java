package nc.noumea.mairie.ptg.reporting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class EtatPayeurReport {

	private Logger logger = LoggerFactory.getLogger(EtatPayeurReport.class);

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

			Paragraph paragraph2 = new Paragraph(titre);
			paragraph2.setAlignment(Element.ALIGN_CENTER);
			// on ajoute le titre, le logo sur le document
			try {
				logger.debug("Test ajout image au document");
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				String path = classLoader.getResource("/images/logo_mairie.png").getPath();
				logger.debug("Test ajout image au document PATH " + path);
				Image logo = Image.getInstance(path);
				logo.scaleToFit(30, 30);
				paragraph2.add(logo);
			} catch (Exception e) {

			}
			document.add(paragraph2);

			// TODO Auto-generated method stub

			// on ferme le document
			document.close();
			// on génere les numeros de page
			// genereNumeroPage(ep);
			// TODO faire en sorte d'ajouter les numeros de page
		}

	}

	private void genereNumeroPage(EtatPayeur ep) throws FileNotFoundException, DocumentException, IOException {

		// Create a reader
		PdfReader reader = new PdfReader(Paths.get(storagePathEcriture, ep.getFichier()).toString());
		// Create a stamper
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(Paths
				.get(storagePathEcriture, ep.getFichier()).toString()));
		// Loop over the pages and add a header to each page
		int n = reader.getNumberOfPages();
		for (int i = 1; i <= n; i++) {
			getHeaderTable(i, n).writeSelectedRows(0, -1, 34, 503, stamper.getOverContent(i));
		}
		// Close the stamper
		stamper.close();
		reader.close();

	}

	private PdfPTable getHeaderTable(int x, int y) {
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(527);
		table.setLockedWidth(true);
		table.getDefaultCell().setFixedHeight(20);
		table.getDefaultCell().setBorder(Rectangle.BOTTOM);
		table.addCell("FOOBAR FILMFESTIVAL");
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(String.format("Page %d of %d", x, y));
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
