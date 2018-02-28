package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.ProfilAgentDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;

public interface ISirhWSConsumer {

	AgentWithServiceDto getAgentService(Integer idAgent, Date date);

	AgentGeneriqueDto getAgent(Integer idAgent);

	List<AgentGeneriqueDto> listAgentAvecIdTitreRepas();

	boolean isHoliday(LocalDate datePointage);

	boolean isHoliday(DateTime deb);

	List<Integer> getPrimePointagesByAgent(Integer idAgent, Date dateDebut, Date dateFin);

	boolean isJourFerie(DateTime deb);

	BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin);

	EntiteDto getAgentDirection(Integer idAgent, Date date);

	List<AgentWithServiceDto> getListAgentsWithService(List<Integer> listAgentDto, Date date);

	List<AgentGeneriqueDto> getListAgents(List<Integer> listIdsAgent);

	List<BaseHorairePointageDto> getListBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin);

	List<Integer> getListAgentsWithPrimeTIDOnAffectation(Date dateDebut, Date dateFin);

	List<AffectationDto> getListAffectationDtoBetweenTwoDateAndForListAgent(List<Integer> listIdsAgent, Date dateDebut, Date dateFin);

	List<JourDto> getListeJoursFeries(Date dateDebut, Date dateFin);

	ReturnMessageDto isUtilisateurSIRH(Integer idAgent);

	List<AgentWithServiceDto> getListAgentsWithServiceOldAffectation(List<Integer> listIdsAgent);

	RefTypeSaisiCongeAnnuelDto getBaseHoraireAbsence(Integer idAgent, Date date);

	/**
	 * Retourne la liste des agents ayant la prime pointage Indemnité
	 * forfaitaire travail DPM sur leur affectation active. Filtre également
	 * avec les agents passés en parametre. #30544
	 * 
	 * @param Set<Integer>
	 *            Liste des agents pour filtre
	 * @return List<AgentWithServiceDto> La liste des agents avec la prime
	 *         Indemnité forfaitaire travail DPM
	 */
	List<AgentWithServiceDto> getListeAgentWithIndemniteForfaitTravailDPM(Set<Integer> listIdsAgent);

	ProfilAgentDto getEtatCivil(Integer idAgent);

	/**
	 * Retourne la liste des agents en activité sur une periode donnée Utile à
	 * PTG pour la génération du fichier prestataire des titres repas Il faut
	 * une PA et une affectation active sur la periode
	 * 
	 * @return List<AgentWithServiceDto> La liste des agents en activité sur la
	 *         période
	 */

	List<AgentWithServiceDto> getListeAgentsMairieSurPeriode(Date datePremierJourOfMonth, Date dateDernierJourOfMonth);
	
	Spphre getSpphre(Integer idAgent, Date dateLundi);
}
