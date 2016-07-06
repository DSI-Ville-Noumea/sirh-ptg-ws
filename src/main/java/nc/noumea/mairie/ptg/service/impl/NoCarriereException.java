package nc.noumea.mairie.ptg.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Pas de carrière active.")
public class NoCarriereException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7404681390105272251L;
}
