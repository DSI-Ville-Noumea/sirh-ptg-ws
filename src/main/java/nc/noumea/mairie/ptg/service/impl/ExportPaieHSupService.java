package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.domain.SpphreId;
import nc.noumea.mairie.domain.SpphreRecupEnum;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.service.IExportPaieHSupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportPaieHSupService implements IExportPaieHSupService {

	@Autowired
	private IExportPaieRepository exportPaieRepository;
	
	@Autowired
	private HelperService helperService;
	
	@Override
	public List<Spphre> exportHsupToPaie(List<VentilHsup> ventilHsupOrderedByDateAsc) {
		
		List<Spphre> hsups = new ArrayList<Spphre>();
		
		for (VentilHsup ventilHsup : ventilHsupOrderedByDateAsc) {
			
			// Fetch or create Spphre
			Spphre hre = findOrCreateSpphreRecord(ventilHsup.getIdAgent(), ventilHsup.getDateLundi());
			
			// Compute the hours to export by removing RECUP minutes from SUP minutes
			fillInSpphre(hre, ventilHsup);
			
			// Add the item to the list of hre modified/created
			if (!isSpphreEmpty(hre)) {
				hre.setSpphreRecup(getCodeRecup(hre));
				hsups.add(hre);
			} else {
				exportPaieRepository.removeEntity(hre);
			}
		}
		
		return hsups;
	}
	
	protected SpphreRecupEnum getCodeRecup(Spphre hre) {
		
		if(hre.getNbh25() == 0d
				&& hre.getNbh50() == 0d
				&& hre.getNbhcomplementaires() == 0d
				&& hre.getNbhdim() == 0d
				&& hre.getNbhmai() == 0d
				&& hre.getNbhnuit() == 0d
				&& hre.getNbhscomposees() == 0d
				&& hre.getNbhssimple() == 0d
				&& hre.getNbhrecuperees() != 0d) {
			return SpphreRecupEnum.R;
		}
		
		return SpphreRecupEnum.P;
	}

	protected Spphre findOrCreateSpphreRecord(Integer idAgent, Date dateLundi) {
		Spphre existingHre = exportPaieRepository.getSpphreForDayAndAgent(idAgent, dateLundi);
		
		if (existingHre != null)
			return existingHre;
		
		existingHre = new Spphre();
		existingHre.setId(new SpphreId(
				helperService.getMairieMatrFromIdAgent(idAgent), 
				helperService.getIntegerDateMairieFromDate(dateLundi)));
		
		existingHre.setSpphreRecup(SpphreRecupEnum.P);
		
		return existingHre;
	}
	
	protected Spphre fillInSpphre(Spphre hre, VentilHsup ventilHsup) {

		int mSup25 = ventilHsup.getMSup25() - ventilHsup.getMSup25Recup();
		hre.setNbh25(helperService.convertMinutesToMairieNbHeuresFormat(mSup25));
		
		int mSup50 = ventilHsup.getMSup50() - ventilHsup.getMSup50Recup();
		hre.setNbh50(helperService.convertMinutesToMairieNbHeuresFormat(mSup50));
		
		int mSdjf = ventilHsup.getMsdjf() - ventilHsup.getMsdjfRecup();
		hre.setNbhdim(helperService.convertMinutesToMairieNbHeuresFormat(mSdjf));
		
		int mMai = ventilHsup.getMMai() - ventilHsup.getMMaiRecup();
		hre.setNbhmai(helperService.convertMinutesToMairieNbHeuresFormat(mMai));
		
		int mSNuit = ventilHsup.getMsNuit() - ventilHsup.getMsNuitRecup();
		hre.setNbhnuit(helperService.convertMinutesToMairieNbHeuresFormat(mSNuit));
		
		int mSimple = ventilHsup.getMSimple() - ventilHsup.getMSimpleRecup();
		hre.setNbhssimple(helperService.convertMinutesToMairieNbHeuresFormat(mSimple));
		
		int mComposees = ventilHsup.getMComposees() - ventilHsup.getMComposeesRecup();
		hre.setNbhscomposees(helperService.convertMinutesToMairieNbHeuresFormat(mComposees));
		
		int nbHcomplementaires = ventilHsup.getMComplementaires() == 0 ? ventilHsup.getMNormales() : ventilHsup.getMComplementaires();
		nbHcomplementaires = nbHcomplementaires - (ventilHsup.getMComplementaires() == 0 ? ventilHsup.getMNormalesRecup() : ventilHsup.getMComplementairesRecup());
		hre.setNbhcomplementaires(helperService.convertMinutesToMairieNbHeuresFormat(nbHcomplementaires));
		
		int nbhrecuperees = ventilHsup.getMRecuperees();
		hre.setNbhrecuperees(helperService.convertMinutesToMairieNbHeuresFormat(nbhrecuperees));
		
		return hre;
	}

	protected boolean isSpphreEmpty(Spphre hre) {

		return (hre.getNbh25() == 0d
				&& hre.getNbh50() == 0d
				&& hre.getNbhcomplementaires() == 0d
				&& hre.getNbhdim() == 0d
				&& hre.getNbhmai() == 0d
				&& hre.getNbhnuit() == 0d
				&& hre.getNbhscomposees() == 0d
				&& hre.getNbhssimple() == 0d
				&& hre.getNbhrecuperees() == 0d);
	}
}
