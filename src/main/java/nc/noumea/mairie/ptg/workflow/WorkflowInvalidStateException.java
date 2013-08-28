package nc.noumea.mairie.ptg.workflow;

public class WorkflowInvalidStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2494940449936213969L;

	public WorkflowInvalidStateException() {
		super();
	}
	
	public WorkflowInvalidStateException(String message) {
		super(message);
	}
	
	public WorkflowInvalidStateException(String message, Exception innerException) {
		super(message, innerException);
	}
}
