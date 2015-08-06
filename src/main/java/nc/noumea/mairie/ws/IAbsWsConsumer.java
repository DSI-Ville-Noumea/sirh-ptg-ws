package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IAbsWsConsumer {

	void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes);

	void addReposCompToAgent(Integer idAgent, Date dateLundi, Integer minutes);

	ReturnMessageDto checkRecuperation(Integer idAgent, Date dateDebut, Date dateFin);

	ReturnMessageDto checkReposComp(Integer idAgent, Date dateDebut, Date dateFin);

	ReturnMessageDto checkAbsencesSyndicales(Integer idAgent, Date dateDebut, Date dateFin);

	ReturnMessageDto checkCongesExceptionnels(Integer idAgent, Date dateDebut, Date dateFin);

	ReturnMessageDto checkCongeAnnuel(Integer idAgent, Date dateDebut, Date dateFin);
	
	List<DemandeDto> getListCongeWithoutCongesAnnuelsEtAnnulesBetween(Integer idAgent, Date start, Date end);

	List<RefTypeSaisiDto> getTypeAbsence(Integer idRefTypeAbsence);

	void addRecuperationsToCompteurAgentForOnePointage(Integer idAgent,
			Date date, Integer minutes, Integer idPointage,
			Integer idPointageParent);
}
