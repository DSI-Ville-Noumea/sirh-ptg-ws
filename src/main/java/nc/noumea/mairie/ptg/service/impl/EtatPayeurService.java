package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nc.noumea.mairie.alfresco.cmis.IAlfrescoCMISService;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.repository.IEtatPayeurRepository;
import nc.noumea.mairie.ptg.service.IEtatPayeurService;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

@Service
public class EtatPayeurService implements IEtatPayeurService {

	private Logger logger = LoggerFactory.getLogger(EtatPayeurService.class);

	@Autowired
	private IEtatPayeurRepository etatPayeurRepository;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	@Autowired
	private IAlfrescoCMISService alfrescoCMISService;

	@Override
	public List<ListEtatsPayeurDto> getListEtatsPayeurByStatut(AgentStatutEnum statutAgent) {

		logger.debug("getListEtatsPayeurByStatut with statutAgent {}" + statutAgent);

		List<ListEtatsPayeurDto> listEtatsPayeurDto = new ArrayList<ListEtatsPayeurDto>();
		ListEtatsPayeurDto etatsPayeurDto = null;

		List<EtatPayeur> listEtatsPayeur = etatPayeurRepository.getListEditionEtatPayeur(statutAgent);

		AgentGeneriqueDto agent = null;

		if (null != listEtatsPayeur) {
			for (EtatPayeur etatPayeur : listEtatsPayeur) {

				agent = new AgentGeneriqueDto();
				agent = sirhWSConsumer.getAgent(etatPayeur.getIdAgent());
				
				String nodeRefAlfresco = etatPayeur.getNodeRefAlfresco();
				if(null == nodeRefAlfresco) {
					nodeRefAlfresco = alfrescoCMISService.getNodeRefFromPathOfFile(etatPayeur.getFichier(), TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
				}
				
				etatsPayeurDto = new ListEtatsPayeurDto(etatPayeur.getIdEtatPayeur(),
						etatPayeur.getStatut().toString(), etatPayeur.getDateEtatPayeur(), etatPayeur.getLabel(),
						etatPayeur.getFichier(), etatPayeur.getIdAgent(), etatPayeur.getDateEdition(),
						agent.getDisplayNom(), agent.getDisplayPrenom(), nodeRefAlfresco);

				listEtatsPayeurDto.add(etatsPayeurDto);
			}
		}

		return listEtatsPayeurDto;
	}

}
