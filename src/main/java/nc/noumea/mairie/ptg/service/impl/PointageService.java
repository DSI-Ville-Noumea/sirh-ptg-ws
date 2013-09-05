package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.NotAMondayException;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageService implements IPointageService {

    private Logger logger = LoggerFactory.getLogger(PointageService.class);
    @Autowired
    private IPointageRepository pointageRepository;
    @Autowired
    private ISirhRepository sirhRepository;
    @Autowired
    private ISirhWSConsumer sirhWSConsumer;
    @Autowired
    private HelperService helperService;
    @Autowired
    private IAgentMatriculeConverterService agentMatriculeConverterService;

    protected FichePointageDto getFichePointageForAgent(Agent agent, Date date) {

        if (!helperService.isDateAMonday(date)) {
            throw new NotAMondayException();
        }

        // Retrieve division service of agent
        SirhWsServiceDto service = sirhWSConsumer.getAgentDirection(agent
                .getIdAgent());

        // on construit le dto de l'agent
        AgentWithServiceDto agentDto = new AgentWithServiceDto(agent);
        agentDto.setCodeService(service.getService());
        agentDto.setService(service.getServiceLibelle());

        // on recherche sa carriere pour avoir son statut (Fonctionnaire,
        // contractuel,convention coll
        Spcarr carr = sirhRepository.getAgentCurrentCarriere(agent,
                helperService.getCurrentDate());
        agentDto.setStatut(carr.getStatutCarriere().name());

        // on construit le DTO de jourPointage
        FichePointageDto result = new FichePointageDto();
        result.setDateLundi(date);
        result.setAgent(agentDto);
        result.setSemaine(helperService.getWeekStringFromDate(date));

        JourPointageDto jourPointageTemplate = new JourPointageDto();
        jourPointageTemplate.setDate(date);
        List<Integer> pps = sirhRepository.getPrimePointagesByAgent(
                agent.getIdAgent(), date);
        if (pps.size() > 0) {
            List<RefPrime> refPrimes = pointageRepository.getRefPrimes(pps,
                    carr.getStatutCarriere());

            for (RefPrime prime : refPrimes) {
                jourPointageTemplate.getPrimes().add(new PrimeDto(prime));
            }
        }

        result.getSaisies().add(jourPointageTemplate);

        // tu as un jour de la semaine type avec toutes les primes

        for (int jour = 1; jour < 7; jour++) {
            JourPointageDto jourSuivant = new JourPointageDto(
                    jourPointageTemplate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(jourPointageTemplate.getDate());
            calendar.add(Calendar.DATE, jour);
            jourSuivant.setDate(calendar.getTime());
            result.getSaisies().add(jourSuivant);
        }

        return result;
    }

    @Override
    public FichePointageListDto getFichesPointageForUsers(String csvIdAgents,
            Date date) {

        List<Agent> listAgent = new ArrayList<Agent>();

        for (String id : csvIdAgents.split(",")) {
            Integer convertedId = agentMatriculeConverterService
                    .tryConvertFromADIdAgentToSIRHIdAgent(Integer.valueOf(id));
            Agent ag = Agent.findAgent(convertedId);
            if (ag != null) {
                listAgent.add(ag);
            }
        }

        FichePointageListDto fiches = new FichePointageListDto();

        for (Agent agent : listAgent) {
            FichePointageDto ficheDto = getFichePointageForAgent(agent, date);
            fiches.getFiches().add(ficheDto);
        }

        return fiches;
    }

    @Override
    public FichePointageDto getFilledFichePointageForAgent(int idAgent,
            Date dateLundi) {

        Agent agent = sirhRepository.getAgent(idAgent);

        FichePointageDto ficheDto = getFichePointageForAgent(agent, dateLundi);

        List<Pointage> agentPointages = getLatestPointagesForSaisieForAgentAndDateMonday(
                idAgent, dateLundi);

        for (Pointage ptg : agentPointages) {

            JourPointageDto jour = ficheDto.getSaisies().get(
                    helperService.getWeekDayFromDateBase0(ptg.getDateDebut()));

            switch (ptg.getTypePointageEnum()) {
                case ABSENCE:
                    AbsenceDto abs = new AbsenceDto(ptg);
                    jour.getAbsences().add(abs);
                    break;

                case H_SUP:
                    HeureSupDto hsup = new HeureSupDto(ptg);
                    jour.getHeuresSup().add(hsup);
                    break;

                case PRIME:
                    // Retrieve related primeDto in JourPointageDto and update it
                    // with value from Pointage
                    PrimeDto thePrimeToUpdate = null;
                    for (PrimeDto pDto : jour.getPrimes()) {
                        if (pDto.getNumRubrique().equals(
                                ptg.getRefPrime().getNoRubr())) {
                            thePrimeToUpdate = pDto;
                        }
                    }
                    thePrimeToUpdate.updateWithPointage(ptg);
                    break;
            }
        }

        return ficheDto;
    }

    @Override
    public Pointage getOrCreateNewPointage(Integer idAgentCreator,
            Integer idPointage, Integer idAgent, Date dateLundi) {
        return getOrCreateNewPointage(idAgentCreator, idPointage, idAgent,
                dateLundi, null);
    }

    @Override
    public Pointage getOrCreateNewPointage(Integer idAgentCreator,
            Integer idPointage, Integer idAgent, Date dateLundi,
            Integer idRefPrime) {

        Pointage ptg = null;
        Pointage parentPointage = null;

        // if the pointage already exists, fetch it
        if (idPointage != null && !idPointage.equals(0)) {
            ptg = pointageRepository.getEntity(Pointage.class, idPointage);

            // if its state is SAISI, return it, otherwise create a new one with
            // this one as parent
            EtatPointage etatPtg = ptg.getLatestEtatPointage();
            if (etatPtg.getEtat() == EtatPointageEnum.SAISI) {
                etatPtg.getEtatPointagePk().setDateEtat(
                        helperService.getCurrentDate());
                etatPtg.setIdAgent(idAgentCreator);
                return ptg;
            }
            parentPointage = ptg;
        }

        ptg = new Pointage();
        ptg.setPointageParent(parentPointage);
        ptg.setIdAgent(idAgent);
        ptg.setDateLundi(dateLundi);
        addEtatPointage(ptg, EtatPointageEnum.SAISI, idAgentCreator);

        // If this pointage is a new version of an existing one,
        // initialize its properties with the parent Pointage
        if (parentPointage != null) {
            ptg.setDateDebut(parentPointage.getDateDebut());
            ptg.setDateFin(parentPointage.getDateFin());
            ptg.setQuantite(parentPointage.getQuantite());
            ptg.setAbsenceConcertee(parentPointage.getAbsenceConcertee());
            ptg.setHeureSupRecuperee(parentPointage.getHeureSupRecuperee());
            ptg.setType(parentPointage.getType());
        }

        // if this is a Prime kind of Pointage, fetch its RefPrime
        if (idRefPrime != null) {
            ptg.setRefPrime(pointageRepository.getEntity(RefPrime.class,
                    idRefPrime));
        }

        return ptg;
    }

    /**
     * Adds an EtatPointage state to a given Pointage
     *
     * @param ptg
     * @param etat
     */
    protected void addEtatPointage(Pointage ptg, EtatPointageEnum etat,
            Integer idAgentCreator) {
        EtatPointagePK pk = new EtatPointagePK();
        pk.setDateEtat(helperService.getCurrentDate());
        pk.setPointage(ptg);
        EtatPointage ep = new EtatPointage();
        ep.setEtat(etat);
        ep.setEtatPointagePk(pk);
        ep.setIdAgent(idAgentCreator);
        ptg.getEtats().add(ep);
    }

    @Override
    public List<RefEtatDto> getRefEtats() {
        List<RefEtatDto> res = new ArrayList<RefEtatDto>();
        List<RefEtat> refEtats = RefEtat.findAllRefEtats();
        for (RefEtat etat : refEtats) {
            RefEtatDto dto = new RefEtatDto(etat);
            res.add(dto);
        }
        return res;
    }

    @Override
    public List<RefTypePointageDto> getRefTypesPointage() {
        List<RefTypePointageDto> res = new ArrayList<RefTypePointageDto>();
        List<RefTypePointage> refTypePointage = RefTypePointage
                .findAllRefTypePointages();
        for (RefTypePointage type : refTypePointage) {
            RefTypePointageDto dto = new RefTypePointageDto(type);
            res.add(dto);
        }
        return res;
    }

    @Override
    public List<Pointage> getLatestPointagesForSaisieForAgentAndDateMonday(
            Integer idAgent, Date dateMonday) {

        List<Pointage> agentPointages = pointageRepository
                .getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateMonday);

        logger.debug("Found {} Pointage for agent {} and date monday {}",
                agentPointages.size(), idAgent, dateMonday);

        return filterOldPointagesAndEtatFromList(agentPointages, Arrays.asList(
                EtatPointageEnum.APPROUVE, EtatPointageEnum.EN_ATTENTE,
                EtatPointageEnum.JOURNALISE, EtatPointageEnum.REFUSE,
                EtatPointageEnum.REJETE, EtatPointageEnum.SAISI,
                EtatPointageEnum.VALIDE, EtatPointageEnum.VENTILE));
    }

    @Override
    public List<Pointage> getLatestPointagesForAgentAndDates(Integer idAgent,
            Date fromDate, Date toDate, RefTypePointageEnum type,
            List<EtatPointageEnum> etats) {
        return getLatestPointagesForAgentsAndDates(Arrays.asList(idAgent),
                fromDate, toDate, type, etats);
    }

    @Override
    public List<Pointage> getLatestPointagesForAgentsAndDates(
            List<Integer> idAgents, Date fromDate, Date toDate,
            RefTypePointageEnum type, List<EtatPointageEnum> etats) {

        List<Pointage> agentPointages = pointageRepository.getListPointages(
                idAgents, fromDate, toDate, type != null ? type.getValue()
                : null);

        logger.debug("Found {} Pointage for agents {} between dates {} and {}",
                agentPointages.size(), idAgents, fromDate, toDate);

        return filterOldPointagesAndEtatFromList(agentPointages, etats);
    }

    @Override
    public List<Pointage> getPointagesVentilesForAgent(Integer idAgent,
            VentilDate ventilDate) {

        List<Pointage> agentPointages = pointageRepository
                .getPointagesVentilesForAgent(idAgent,
                ventilDate.getIdVentilDate());

        logger.debug(
                "Found {} Pointage for agent {} and ventil date {} as of {}",
                agentPointages.size(), idAgent, ventilDate.getIdVentilDate(),
                ventilDate.getDateVentilation());

        return filterOldPointagesAndEtatFromList(agentPointages,
                Arrays.asList(EtatPointageEnum.VENTILE));
    }

    protected List<Pointage> filterOldPointagesAndEtatFromList(
            List<Pointage> pointages, List<EtatPointageEnum> etats) {

        List<Integer> oldPointagesToAvoid = new ArrayList<Integer>();

        List<Pointage> resultList = new ArrayList<Pointage>();

        for (Pointage ptg : pointages) {

            if (ptg.getPointageParent() != null) {
                logger.debug(
                        "Pointage {} has a parent {}, adding it to avoid list.",
                        ptg.getIdPointage(), ptg.getPointageParent()
                        .getIdPointage());
                oldPointagesToAvoid
                        .add(ptg.getPointageParent().getIdPointage());
            }

            if (oldPointagesToAvoid.contains(ptg.getIdPointage())) {
                logger.debug("Not taking Pointage {} because not the latest.",
                        ptg.getIdPointage());
                continue;
            }

            if (etats == null
                    || etats.contains(ptg.getLatestEtatPointage().getEtat())) {
                resultList.add(ptg);
            } else {
                logger.debug(
                        "Not taking Pointage {} because not in the given Etat list : {}.",
                        ptg.getIdPointage(), etats);
            }
        }

        return resultList;
    }

    @Override
    public boolean isPrimeUtiliseePointage(Integer idAgent, List<Integer> idRefPrime) {

        for (int idPrime : idRefPrime) {
            if (pointageRepository.isPrimeSurPointageouPointageCalcule(idAgent, idPrime)) {
                return true;
            }
        }
        return false;
    }
}
