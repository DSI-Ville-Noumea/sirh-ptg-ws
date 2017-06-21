package nc.noumea.mairie.ptg.reporting;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import nc.noumea.mairie.domain.Spperm;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPrestataire;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatPayeurData;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.ProfilAgentDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

@Service
public class EtatPrestataireTitreRepasReporting {

	private Logger					logger										= LoggerFactory.getLogger(EtatPrestataireTitreRepasReporting.class);

	@Autowired
	private IAlfrescoCMISService	alfrescoCMISService;

	@Autowired
	private ISirhWSConsumer						sirhWsConsumer;

	private static final String		DESCRIPTION_ETAT_PRESTATAIRE_TITRE_REPAS	= "Etat Prestataire des Titres Repas du ";

	private static final String		NEW_LINE_SEPARATOR							= "\n";
	private static final Object[]	FILE_HEADER									= { "Id", "Civilité", "Nom", "Prénom", "Date de naissance",
			"Solde actuels", "Nb tickets", "Valeur faciale" };

	public ReturnMessageDto downloadEtatPrestataireTitreRepas(TitreRepasEtatPrestataire etatPrestataireTR, Map<Integer, TitreRepasDemande> mapAgentTR,
			List<TitreRepasExportEtatPayeurData> listeDataTR, Spperm refPrime,ReturnMessageDto result) throws DocumentException, MalformedURLException, IOException {

		String valeurFaciale = String.valueOf(refPrime.getMontantForfait().intValue());
		String nbTicket = String.valueOf(refPrime.getMontantPlafond().intValue());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ByteArrayOutputStream outB = new ByteArrayOutputStream();
		Writer out = new BufferedWriter(new OutputStreamWriter(outB));
		CSVFormat csvFileFormat = CSVFormat.EXCEL.withRecordSeparator(NEW_LINE_SEPARATOR);
		CSVPrinter csvPrinter = new CSVPrinter(out, csvFileFormat);

		// Create CSV file header
		csvPrinter.printRecord(FILE_HEADER);
		Map<Integer, Integer> mapAgentActifs = new HashMap<>();

		// on parcours les données chargées
		for (TitreRepasExportEtatPayeurData data : listeDataTR) {
			List<String> dataRecord = new ArrayList<>();
			dataRecord.add(data.getIdTitreRepas().toString());
			dataRecord.add(data.getCiviliteTitreRepas());
			dataRecord.add(data.getNomTitreRepas());
			dataRecord.add(data.getPrenomTitreRepas());
			dataRecord.add(sdf.format(data.getDateNaissanceTitreRepas()));
			dataRecord.add("");

			// on recupere la titre demande si il existe
			TitreRepasDemande tr = null;
			try {
				tr = mapAgentTR.get(data.getIdAgent());
				if (tr != null && tr.getCommande()) {
					dataRecord.add(nbTicket);
				} else {
					dataRecord.add("0");
				}

			} catch (Exception e) {
				// pas de demande enregistrée
				dataRecord.add("0");
			}

			dataRecord.add(valeurFaciale + " XPF");
			csvPrinter.printRecord(dataRecord);
			// #38362 : on fait 2 verifications desormais
			mapAgentActifs.put(data.getIdAgent(), data.getIdAgent());

		}
		if(mapAgentActifs.size()<10){
			result.getErrors().add("Erreur dans le fichier prestataire (est-ce que le separateur est bien ';' ?");
			return result;
		}

		// #38362 : on fait un tri sur les agents ayant commandé pour etre sur
		// de n'oublier personne
		for (Integer idAgentAyantCommande : mapAgentTR.keySet()) {
			if (!mapAgentActifs.containsKey(idAgentAyantCommande)) {
				List<String> dataRecord = new ArrayList<>();
				ProfilAgentDto agentSansService = sirhWsConsumer.getEtatCivil(idAgentAyantCommande);
				// c'est un agent non présent dans le fichier issu de TR
				dataRecord.add("0");
				// cas de la civilité
				String civilite = agentSansService.getTitre();
				if (civilite != null && !civilite.trim().equals("")) {
					if (civilite.equals("Monsieur"))
						dataRecord.add("MR");
					else if (civilite.equals("Madame"))
						dataRecord.add("MME");
					else if (civilite.equals("Mademoiselle"))
						dataRecord.add("MLLE");
					else
						dataRecord.add("");

				} else {
					dataRecord.add("");
				}

				dataRecord.add(agentSansService.getAgent().getDisplayNom().toUpperCase().trim());
				dataRecord.add(agentSansService.getAgent().getDisplayPrenom().toUpperCase().trim());
				dataRecord.add(sdf.format(agentSansService.getDateNaissance()));
				dataRecord.add("");
				// on recupere la titre demande si il existe
				TitreRepasDemande tr = null;
				try {
					tr = mapAgentTR.get(idAgentAyantCommande);
					if (tr != null && tr.getCommande()) {
						dataRecord.add(nbTicket);
					} else {
						dataRecord.add("0");
					}
				} catch (Exception e) {
					// pas de demande enregistrée
					dataRecord.add("0");
				}

				dataRecord.add(valeurFaciale + " XPF");
				csvPrinter.printRecord(dataRecord);
			}
			
		}
		

		logger.debug("CSV file was created successfully !!!");
		csvPrinter.close();

		String node = alfrescoCMISService.uploadDocument(etatPrestataireTR.getIdAgent(), outB.toByteArray(), etatPrestataireTR.getFichier(),
				DESCRIPTION_ETAT_PRESTATAIRE_TITRE_REPAS + etatPrestataireTR.getDateEtatPrestataire(),
				TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_TITRE_REPAS);

		etatPrestataireTR.setNodeRefAlfresco(node);
		return result;
	}
}
