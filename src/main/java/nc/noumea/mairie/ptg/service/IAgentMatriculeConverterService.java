package nc.noumea.mairie.ptg.service;

public interface IAgentMatriculeConverterService {

	/***
	 * This helpers takes 6 digits a agent IDs and converts it into a new 7 digits agent ID
	 * @param adIdAgent on 6 digits
	 * @return IdAgent on 7 digits
	 */
	Integer fromADIdAgentToSIRHIdAgent(Integer adIdAgent) throws AgentMatriculeConverterServiceException;
	
	/***
	 * This helpers takes 6 digits a agent IDs and tries to convert it into a new 7 digits agent ID
	 * @param adIdAgent on 6 digits
	 * @return IdAgent on 7 digits
	 */
	Integer tryConvertFromADIdAgentToSIRHIdAgent(Integer adIdAgent);

	/***
	 * This helpers takes 7 digits a agent IDs and tries to convert it into a new 4 digits agent nomatr
	 * @param idAgent on 7 digits
	 * @return nomatr on 4 digits
	 */
	Integer tryConvertIdAgentToNomatr(Integer idAgent)throws AgentMatriculeConverterServiceException;
}
