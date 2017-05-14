package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IAbsWsConsumer {

	void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes);

	void addReposCompToAgent(Integer idAgent, Date dateLundi, Integer minutes);

	List<RefTypeSaisiDto> getTypeSaisiAbsence(Integer idRefTypeAbsence);

	void addRecuperationsToCompteurAgentForOnePointage(Integer idAgent,
			Date date, Integer minutes, Integer idPointage,
			Integer idPointageParent);

	List<DemandeDto> getListAbsencesForListAgentsBetween2Dates(
			List<Integer> listIdsAgent, Date start, Date end);

	List<nc.noumea.mairie.abs.dto.RefTypeAbsenceDto> getListeTypAbsenceCongeAnnuel();

	ReturnMessageDto checkAbsences(Integer idAgent, Date dateDebut, Date dateFin);

	List<DemandeDto> getListCongesExeptionnelsEtatPrisBetween(Integer idAgent,
			Date start, Date end);

	List<DemandeDto> getListMaladiesEtatPrisBetween(Integer idAgent,
			Date start, Date end);
}
