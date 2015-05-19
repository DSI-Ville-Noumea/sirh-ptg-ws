package nc.noumea.mairie.ptg.reporting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class EtatPayeurReporting extends AbstractReporting {

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

			// on genere les metadata
			addMetaData(document, getTitreDocument(result), ep.getIdAgent());

			// on ouvre le document
			document.open();

			// on ecrit dans le document
			writeDocument(document, result);

			// on ferme le document
			document.close();
			// on génere les numeros de page
			genereNumeroPageA3Paysage(Paths.get(storagePathEcriture, ep.getFichier()).toString());
		}

	}

	private void writeDocument(Document document, EtatPayeurDto result) throws DocumentException {

		// on ajoute le titre, le logo sur le document
		writeTitle(document, getTitreDocument(result),
				this.getClass().getClassLoader().getResource("images/logo_mairie.png"), false, false);

	}

	private String getTitreDocument(EtatPayeurDto dto) {
		String chainePaie = "HORS CONVENTION";
		if (dto.getChainePaie().equals(TypeChainePaieEnum.SHC.toString())) {
			chainePaie = "CONVENTION COLLECTIVE";
		}
		return "ETAT DES ELEMENTS DE SALAIRE " + chainePaie + " A PAYER SUR " + dto.getPeriode().toUpperCase();
	}

}
