package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IVentilationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;

@Controller
@RequestMapping("/ventilation")
public class VentilationController {

    private Logger logger = LoggerFactory.getLogger(VentilationController.class);
    @Autowired
    private IVentilationService ventilationService;

    @ResponseBody
    @RequestMapping(value = "/run", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @Transactional("ptgTransactionManager")
    public ResponseEntity<String> runVentilation(
            @RequestParam("idAgent") Integer idAgent,
            @RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate,
            @RequestParam(value = "typePointage", required = false) Integer idRefTypePointage,
            @RequestParam(value = "statut") String statut,
            @RequestBody String agentsJson) {

        logger.debug(
                "entered POST [ventilation/run] => runVentilation with parameters date = {}, agents = {}, typePointage = {}, statut = {}",
                ventilationDate, agentsJson, idRefTypePointage, statut);

        // Deserializing integer list
        List<Integer> agents = new JSONDeserializer<List<Integer>>().use(null, ArrayList.class).use("values", Integer.class).deserialize(agentsJson);

        // Running ventilation
        ReturnMessageDto result = ventilationService.processVentilation(idAgent, agents, ventilationDate, AgentStatutEnum.valueOf(statut), RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage));

        if (result.getErrors().size() != 0) {
            return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/show", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
    @Transactional("ptgTransactionManager")
    public ResponseEntity<String> showVentilation(
            @RequestParam("csvIdAgents") String csvIdAgents,
            @RequestParam("idDateVentil") Integer idDateVentil,
            @RequestParam("typePointage") Integer idRefTypePointage) {

        RefTypePointageEnum typepointage = RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage);
        logger.debug("entered GET [ventilation/show] => showVentilation with parameters idDateVentil = {}, agents = {}, typePointage = {}", idDateVentil, csvIdAgents, typepointage.name());

        // Deserializing integer list
        List<Integer> agents = new ArrayList<>();

        for (String ag : csvIdAgents.split(",")) {
            agents.add(Integer.parseInt(ag));
        }

        // Request show depending on typepointage
        List result = ventilationService.showVentilation(agents, idDateVentil, typepointage);

        /**
         * List result; switch (typepointage) { case ABSENCE: { result =
         * ventilationService.showVentilation(agents, ventilationDate,
         * AgentStatutEnum.valueOf(statut), typepointage,
         * VentilAbsenceDto.class); break; } case H_SUP: { result =
         * ventilationService.showVentilation(agents, ventilationDate,
         * AgentStatutEnum.valueOf(statut), typepointage, VentilHSupDto.class);
         * break; } case PRIME: default: { result =
         * ventilationService.showVentilation(agents, ventilationDate,
         * AgentStatutEnum.valueOf(statut), typepointage, VentilPrimeDto.class);
         * break; } }*
         */
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class).deepSerialize(result), HttpStatus.OK);
    }
}
