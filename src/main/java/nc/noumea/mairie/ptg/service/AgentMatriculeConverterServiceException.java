package nc.noumea.mairie.ptg.service;

public class AgentMatriculeConverterServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public AgentMatriculeConverterServiceException() {
		super();
	}
	
	public AgentMatriculeConverterServiceException(String message) {
		super(message);
	}
	
	public AgentMatriculeConverterServiceException(String message, Exception innerException) {
		super(message, innerException);
	}

}
