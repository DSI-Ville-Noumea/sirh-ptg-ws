package nc.noumea.mairie.ptg.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.AbstractItemEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.PrimesEtatPayeurDto;
import nc.noumea.mairie.ptg.reporting.vo.CellVo;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;

@Service
public class EtatPayeurReporting extends AbstractReporting {

	@Autowired
	private IExportEtatPayeurService	exportEtatPayeurService;

	@Autowired
	private IAlfrescoCMISService		alfrescoCMISService;

	private SimpleDateFormat			sdfddMMyyyy	= new SimpleDateFormat("dd/MM/yyyy");
	
	private static final String DESCRIPTION_ETAT_PAYEUR = "Etat Payeur Pointages du ";

	public void downloadEtatPayeurByStatut(AgentStatutEnum statut, EtatPayeur ep) throws DocumentException, MalformedURLException, IOException {

		// on recupere le DTO
		EtatPayeurDto result = exportEtatPayeurService.getEtatPayeurDataForStatut(statut);
		if (result.getStatut() != null) {
			// on crée le document
			Document document = new Document(PageSize.A3.rotate());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// on genere les metadata
			addMetaData(document, getTitreDocument(result), ep.getIdAgent());

			// on ouvre le document
			document.open();

			// on ecrit dans le document
			writeDocument(document, result);

			// on ferme le document
			document.close();

			// on génere les numeros de page
			baos = genereNumeroPageA3Paysage(baos);

			String node = alfrescoCMISService.uploadDocument(ep.getIdAgent(), baos.toByteArray(), ep.getFichier(),
					DESCRIPTION_ETAT_PAYEUR + ep.getDateEtatPayeur(), TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);

			ep.setNodeRefAlfresco(node);

		}
	}

	private void writeDocument(Document document, EtatPayeurDto result) throws DocumentException {

		// on ajoute le titre, le logo sur le document
		writeTitle(document, getTitreDocument(result), this.getClass().getClassLoader().getResource("images/logo_mairie.png"), false, false);

		// on ecrit le tableau
		writeTableau(document, result);

		// on ecrit : "Vérification DRH
		// le
		// Cachet"
		writeCachet(document);
	}

	private String getTitreDocument(EtatPayeurDto dto) {
		String chainePaie = "HORS CONVENTION";
		if (dto.getChainePaie().equals(TypeChainePaieEnum.SCV.toString())) {
			chainePaie = "CONVENTION COLLECTIVE";
		}
		return "ETAT DES ELEMENTS DE SALAIRE " + chainePaie + " A PAYER SUR " + dto.getPeriode().toUpperCase();
	}

	private void writeTableau(Document document, EtatPayeurDto result) throws DocumentException {

		PdfPTable table = writeTableau(document, new float[] { 2, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 10, 3, 2 });
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);

		// 1er ligne : entete
		List<CellVo> listValuesLigne1 = new ArrayList<CellVo>();
		listValuesLigne1.add(new CellVo("VENTILATION DU " + result.getDateVentilation(), true, 3, null, Element.ALIGN_LEFT, false));
		listValuesLigne1.add(new CellVo("HEURES SUPPLEMENTAIRES", true, 6, null, Element.ALIGN_CENTER));
		listValuesLigne1.add(new CellVo("Nombre d'absences", true, 3, null, Element.ALIGN_CENTER));
		listValuesLigne1.add(new CellVo("PRIMES", true, 3, null, Element.ALIGN_CENTER));
		writeLine(table, 3, listValuesLigne1);

		// 2e ligne : entete
		List<CellVo> listValuesLigne2 = new ArrayList<CellVo>();
		listValuesLigne2.add(new CellVo("Matricule", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Nom Prénom", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Service", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Normales", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("25% ou simples", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("50% ou composées", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Nuit", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Dimanche \n Jour Férié", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("01-mai", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("< 1 heure", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("1< heure(s) <4", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("> 4 heures", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Désignation", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Date", 1, Element.ALIGN_CENTER));
		listValuesLigne2.add(new CellVo("Quantité", 1, Element.ALIGN_CENTER));
		writeLine(table, 3, listValuesLigne2);

		// on boucle sur les agents
		for (AbstractItemEtatPayeurDto agentPayeurDto : result.getAgents()) {
			writeLineByAgent(table, agentPayeurDto);
		}

		document.add(table);
	}

	private void writeLineByAgent(PdfPTable table, AbstractItemEtatPayeurDto agentPayeurDto) {

		List<CellVo> listValuesByAgent = new ArrayList<CellVo>();

		// on ecrit les donnees de l agent
		listValuesByAgent.add(new CellVo(agentPayeurDto.getAgent().getNomatr().toString(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getAgent().getNom() + " " + agentPayeurDto.getAgent().getPrenom()));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getAgent().getSigleService()));

		// on ecrit les heures supp.
		listValuesByAgent.add(new CellVo(agentPayeurDto.getHeuresSup().getNormales(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getHeuresSup().getSup25(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getHeuresSup().getSup50(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getHeuresSup().getNuit(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getHeuresSup().getDjf(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getHeuresSup().getH1Mai(), 1, Element.ALIGN_CENTER));

		// on ecrit les absences
		listValuesByAgent.add(new CellVo(agentPayeurDto.getAbsences().getQuantiteInf1Heure(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getAbsences().getQuantiteEntre1HeureEt4Heure(), 1, Element.ALIGN_CENTER));
		listValuesByAgent.add(new CellVo(agentPayeurDto.getAbsences().getQuantiteSup4Heure(), 1, Element.ALIGN_CENTER));

		// on ecrit les primes (boucle)
		String designationPrimeAgent = "";
		String datePrimeAgent = "";
		String quantitePrimeAgent = "";

		for (PrimesEtatPayeurDto primeDto : agentPayeurDto.getPrimes()) {
			designationPrimeAgent = designationPrimeAgent + primeDto.getType() + "\n";
			datePrimeAgent = datePrimeAgent + sdfddMMyyyy.format(primeDto.getDate()) + "\n";
			quantitePrimeAgent = quantitePrimeAgent + primeDto.getQuantite() + "\n";
		}
		listValuesByAgent.add(new CellVo(designationPrimeAgent, fontNormal8));
		listValuesByAgent.add(new CellVo(datePrimeAgent, false, 1, null, Element.ALIGN_CENTER, true, fontNormal8));
		listValuesByAgent.add(new CellVo(quantitePrimeAgent, false, 1, null, Element.ALIGN_CENTER, true, fontNormal8));

		writeLine(table, 3, listValuesByAgent);
	}

	private void writeCachet(Document document) throws DocumentException {

		Phrase phrase = new Phrase("Vérification DRH \n le \n Cachet", fontNormal10);

		Paragraph paragraph = new Paragraph(phrase);

		PdfPCell cellVide = new PdfPCell();
		cellVide.setBorder(Rectangle.NO_BORDER);

		PdfPCell cellCachet = new PdfPCell();
		cellCachet.addElement(paragraph);
		cellCachet.setBorder(Rectangle.NO_BORDER);

		PdfPTable table = null;
		table = new PdfPTable(new float[] { 12, 6 });

		table.setWidthPercentage(100f);
		table.addCell(cellVide);
		table.addCell(cellCachet);

		document.add(table);
	}
}
