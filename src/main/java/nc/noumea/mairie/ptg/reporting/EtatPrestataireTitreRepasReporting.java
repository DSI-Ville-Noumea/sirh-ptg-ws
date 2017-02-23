package nc.noumea.mairie.ptg.reporting;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPrestataire;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

@Service
public class EtatPrestataireTitreRepasReporting {

	private Logger					logger										= LoggerFactory.getLogger(EtatPrestataireTitreRepasReporting.class);

	@Autowired
	private IAlfrescoCMISService	alfrescoCMISService;

	private static final String		DESCRIPTION_ETAT_PRESTATAIRE_TITRE_REPAS	= "Etat Prestataire des Titres Repas du ";

	private static final String		NEW_LINE_SEPARATOR							= "\n";
	private static final Object[]	FILE_HEADER									= { "Matricule", "Nom", "Prénom", "Service", "Commande" };

	public void downloadEtatPrestataireTitreRepas(TitreRepasEtatPrestataire etatPrestataireTR, Map<Integer, TitreRepasDemande> mapAgentTR,
			List<AgentWithServiceDto> listeAgentActif) throws DocumentException, MalformedURLException, IOException {

		ByteArrayOutputStream outB = new ByteArrayOutputStream();
		Writer out = new BufferedWriter(new OutputStreamWriter(outB));
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withRecordSeparator(NEW_LINE_SEPARATOR);
		CSVPrinter csvPrinter = new CSVPrinter(out, csvFileFormat);

		// Create CSV file header
		csvPrinter.printRecord(FILE_HEADER);

		// on ecrit tous les agents actifs
		for (AgentWithServiceDto ag : listeAgentActif) {
			List<String> studentDataRecord = new ArrayList<>();
			Integer idAgent = ag.getIdAgent() - 9000000;
			studentDataRecord.add(idAgent.toString());
			studentDataRecord.add(ag.getNom().toUpperCase().trim());
			studentDataRecord.add(ag.getPrenom().toUpperCase().trim());
			studentDataRecord.add(ag.getSigleService());
			// on recupere la titre demande si il existe
			TitreRepasDemande tr = null;
			try {
				tr = mapAgentTR.get(ag.getIdAgent());
				if (tr != null && tr.getCommande()) {
					studentDataRecord.add("oui");
				} else {
					studentDataRecord.add("non");
				}
			} catch (Exception e) {
				// pas de demande enregistrée
				studentDataRecord.add("non");
			}

			csvPrinter.printRecord(studentDataRecord);

		}

		logger.debug("CSV file was created successfully !!!");
		csvPrinter.close();

		String node = alfrescoCMISService.uploadDocument(etatPrestataireTR.getIdAgent(), outB.toByteArray(), etatPrestataireTR.getFichier(),
				DESCRIPTION_ETAT_PRESTATAIRE_TITRE_REPAS + etatPrestataireTR.getDateEtatPrestataire(),
				TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_TITRE_REPAS);

		etatPrestataireTR.setNodeRefAlfresco(node);
	}
}
