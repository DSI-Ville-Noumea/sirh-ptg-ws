package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.repository.IMairieRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageDataConsistencyRules implements IPointageDataConsistencyRules {

	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private HelperService helperService;
	
	public List<String> checkSprircRecuperation(Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		List<String> errors = new ArrayList<String>();
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Sprirc> recups = mairieRepository.getListRecuperationBetween(idAgent, dateLundi, end);
		
		for (Sprirc recup : recups) {

			DateTime recupDateDeb = GetDateDebut(recup.getId().getDatdeb(), recup.getId().getCodem1());
			DateTime recupDateFin = GetDateFin(recup.getDatfin(), recup.getCodem2());
			
			for (Pointage ptg : pointages) {
				DateTime ptgTime = new DateTime(ptg.getDateDebut());
				DateTime ptgTimeEnd = new DateTime(ptg.getDateFin());
				
				if (ptgTime.isAfter(recupDateDeb) && ptgTime.isBefore(recupDateFin))
					errors.add(String.format("%s : L'agent est en récupération sur cette période.", 
							ptgTime.toString("dd/MM/yyyy HH:mm")));
				else if (ptgTimeEnd.isAfter(recupDateDeb) && ptgTimeEnd.isBefore(recupDateFin)) {
					errors.add(String.format("%s : L'agent est en récupération sur cette période.", 
							ptgTimeEnd.toString("dd/MM/yyyy HH:mm")));
				}
			}
		}
		
		return errors;
	}

	@Override
	public List<String> checkSpcongConge(Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		List<String> errors = new ArrayList<String>();
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Spcong> conges = mairieRepository.getListCongeBetween(idAgent, dateLundi, end);
		
		for (Spcong cg : conges) {

			DateTime recupDateDeb = GetDateDebut(cg.getId().getDatdeb(), cg.getCodem1());
			DateTime recupDateFin = GetDateFin(cg.getDatfin(), cg.getCodem2());
			
			for (Pointage ptg : pointages) {
				DateTime ptgTime = new DateTime(ptg.getDateDebut());
				DateTime ptgTimeEnd = new DateTime(ptg.getDateFin());
				
				if (ptgTime.isAfter(recupDateDeb) && ptgTime.isBefore(recupDateFin))
					errors.add(String.format("%s : L'agent est en congé sur cette période.", 
							ptgTime.toString("dd/MM/yyyy HH:mm")));
				else if (ptgTimeEnd.isAfter(recupDateDeb) && ptgTimeEnd.isBefore(recupDateFin)) {
					errors.add(String.format("%s : L'agent est en congé sur cette période.", 
							ptgTimeEnd.toString("dd/MM/yyyy HH:mm")));
				}
			}
		}
		
		return errors;
	}
	
	protected DateTime GetDateFin(Integer dateFin, Integer codem2) {
		DateTime recupDateFin = new DateTime(helperService.getDateFromMairieInteger(dateFin));
		if (codem2.equals(1))
			recupDateFin = recupDateFin.plusMinutes(690); // 11h30
		else
			recupDateFin = recupDateFin.plusMinutes(930); // 15h30
		return recupDateFin;
	}

	protected DateTime GetDateDebut(Integer dateDeb, Integer codem1) {
		DateTime recupDateDeb = new DateTime(helperService.getDateFromMairieInteger(dateDeb));
		if (codem1.equals(1))
			recupDateDeb = recupDateDeb.plusMinutes(435); // 7h15
		else
			recupDateDeb = recupDateDeb.plusMinutes(720); // 12h00
		return recupDateDeb;
	}
	
	
}
