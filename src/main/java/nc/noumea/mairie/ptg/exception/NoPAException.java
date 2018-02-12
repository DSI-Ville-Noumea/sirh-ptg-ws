package nc.noumea.mairie.ptg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason = "Aucune position administrative trouvée sur cette période.")
public class NoPAException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

}
