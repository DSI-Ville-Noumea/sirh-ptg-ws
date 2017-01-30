package nc.noumea.mairie.ptg.reporting;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPrestataire;
import nc.noumea.mairie.sirh.dto.ProfilAgentDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

@Service
public class EtatPrestataireTitreRepasReporting {

	private Logger					logger										= LoggerFactory.getLogger(EtatPrestataireTitreRepasReporting.class);

	@Autowired
	private ISirhWSConsumer			sirhWSConsumer;

	@Autowired
	private IAlfrescoCMISService	alfrescoCMISService;

	private static final String		DESCRIPTION_ETAT_PRESTATAIRE_TITRE_REPAS	= "Etat Prestataire des Titres Repas du ";

	private static final String		NEW_LINE_SEPARATOR							= "\n";
	private static final Object[]	FILE_HEADER									= { "Civilité", "Nom", "Prénom", "Date de naissance" };

	private SimpleDateFormat		sdf											= new SimpleDateFormat("dd/MM/yyyy");

	public void downloadEtatPrestataireTitreRepas(TitreRepasEtatPrestataire etatPrestataireTR, List<TitreRepasDemandeDto> listDemandeTR)
			throws DocumentException, MalformedURLException, IOException {

		ByteArrayOutputStream outB = new ByteArrayOutputStream();
		Writer out = new BufferedWriter(new OutputStreamWriter(outB));
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withRecordSeparator(NEW_LINE_SEPARATOR);
		CSVPrinter csvPrinter = new CSVPrinter(out, csvFileFormat);

		// Create CSV file header
		csvPrinter.printRecord(FILE_HEADER);
		for (TitreRepasDemandeDto tr : listDemandeTR) {
			// on recupere les informations de l'agent
			ProfilAgentDto agentDto = sirhWSConsumer.getEtatCivil(tr.getAgent().getIdAgent());

			List<String> studentDataRecord = new ArrayList<>();
			studentDataRecord.add(agentDto.getTitre().toUpperCase().trim());
			studentDataRecord.add(agentDto.getAgent().getDisplayNom().toUpperCase().trim());
			studentDataRecord.add(agentDto.getAgent().getDisplayPrenom().toUpperCase().trim());
			studentDataRecord.add(sdf.format(agentDto.getDateNaissance()));
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
