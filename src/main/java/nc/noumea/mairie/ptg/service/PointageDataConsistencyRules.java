package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageDataConsistencyRules implements IPointageDataConsistencyRules {

	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private HelperService helperService;

	@Override
	public List<String> checkMaxAbsenceHebdo(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		double nbHours = 0;
		
		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());
			DateTime fin = new DateTime(ptg.getDateFin());
			
			nbHours += (Minutes.minutesBetween(deb, fin).getMinutes() / 60.0);
		}
		
		if (nbHours == 0)
			return errors;
		
		Agent ag = mairieRepository.getAgent(idAgent);
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(ag, dateLundi);
		
		double agentMaxHours = carr.getSpbhor().getTaux() * carr.getSpbase().getNbashh();
		
		if (nbHours > agentMaxHours)
			errors.add("L'agent dépasse sa base horaire");
		
		return errors;
	}
	
	public List<String> checkSprircRecuperation(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
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
	public List<String> checkSpcongConge(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Spcong> conges = mairieRepository.getListCongeBetween(idAgent, dateLundi, end);
		
		for (Spcong cg : conges) {

			DateTime recupDateDeb = GetDateDebut(cg.getId().getDatdeb(), cg.getCodem1());
			DateTime recupDateFin = GetDateFin(cg.getDatfin(), cg.getCodem2());
			
			for (Pointage ptg : pointages) {
				DateTime ptgTime = new DateTime(ptg.getDateDebut());
				DateTime ptgTimeEnd = new DateTime(ptg.getDateFin());
				
				if (ptgTime.isAfter(recupDateDeb) && ptgTime.isBefore(recupDateFin))
					errors.add(String.format("%s : L'agent est en congés payés sur cette période.", 
							ptgTime.toString("dd/MM/yyyy HH:mm")));
				else if (ptgTimeEnd.isAfter(recupDateDeb) && ptgTimeEnd.isBefore(recupDateFin)) {
					errors.add(String.format("%s : L'agent est en congés payés sur cette période.", 
							ptgTimeEnd.toString("dd/MM/yyyy HH:mm")));
				}
			}
		}
		
		return errors;
	}
	
	@Override
	public List<String> checkSpabsenMaladie(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Spabsen> maladies = mairieRepository.getListMaladieBetween(idAgent, dateLundi, end);
		
		for (Spabsen mal : maladies) {

			DateTime recupDateDeb = GetDateDebut(mal.getId().getDatdeb(), null);
			DateTime recupDateFin = GetDateFin(mal.getDatfin(), null);
			
			for (Pointage ptg : pointages) {
				DateTime ptgTime = new DateTime(ptg.getDateDebut());
				DateTime ptgTimeEnd = new DateTime(ptg.getDateFin());
				
				if (ptgTime.isAfter(recupDateDeb) && ptgTime.isBefore(recupDateFin))
					errors.add(String.format("%s : L'agent est en maladie sur cette période.", 
							ptgTime.toString("dd/MM/yyyy")));
				else if (ptgTimeEnd.isAfter(recupDateDeb) && ptgTimeEnd.isBefore(recupDateFin)) {
					errors.add(String.format("%s : L'agent est en maladie sur cette période.", 
							ptgTimeEnd.toString("dd/MM/yyyy")));
				}
			}
		}
		
		return errors;
	}
	
	protected DateTime GetDateDebut(Integer dateDeb, Integer codem1) {
		DateTime recupDateDeb = new DateTime(helperService.getDateFromMairieInteger(dateDeb));
		
		if (codem1 == null)
			return recupDateDeb;
		
		if (codem1.equals(1))
			recupDateDeb = recupDateDeb.plusMinutes(434); // 7h14
		else
			recupDateDeb = recupDateDeb.plusMinutes(719); // 11h59
		return recupDateDeb;
	}

	protected DateTime GetDateFin(Integer dateFin, Integer codem2) {
		DateTime recupDateFin = new DateTime(helperService.getDateFromMairieInteger(dateFin));
		
		if (codem2 == null)
			return recupDateFin.plusDays(1);
		
		if (codem2.equals(1))
			recupDateFin = recupDateFin.plusMinutes(691); // 11h31
		else
			recupDateFin = recupDateFin.plusMinutes(931); // 15h31
		return recupDateFin;
	}
}
