package nc.noumea.mairie.ws;

public class SirhWSConsumerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6378424967920764491L;

	public SirhWSConsumerException() {
		super();
	}
	
	public SirhWSConsumerException(String message) {
		super(message);
	}
	
	public SirhWSConsumerException(String message, Exception innerException) {
		super(message, innerException);
	}
}
