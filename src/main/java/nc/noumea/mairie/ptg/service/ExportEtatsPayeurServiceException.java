package nc.noumea.mairie.ptg.service;

public class ExportEtatsPayeurServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ExportEtatsPayeurServiceException() {
		super();
	}
	
	public ExportEtatsPayeurServiceException(String message) {
		super(message);
	}
	
	public ExportEtatsPayeurServiceException(String message, Exception innerException) {
		super(message, innerException);
	}

}
