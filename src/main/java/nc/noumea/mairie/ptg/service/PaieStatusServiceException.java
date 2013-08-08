package nc.noumea.mairie.ptg.service;

public class PaieStatusServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public PaieStatusServiceException() {
		super();
	}
	
	public PaieStatusServiceException(String message) {
		super(message);
	}
	
	public PaieStatusServiceException(String message, Exception innerException) {
		super(message, innerException);
	}
}
