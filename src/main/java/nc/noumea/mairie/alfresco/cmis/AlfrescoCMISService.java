package nc.noumea.mairie.alfresco.cmis;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;

@Service
public class AlfrescoCMISService implements IAlfrescoCMISService {

	private Logger				logger		= LoggerFactory.getLogger(AlfrescoCMISService.class);

	private static final String	MIME_TYPE	= "application/octet-stream";

	@Autowired
	@Qualifier("alfrescoUrl")
	private String				alfrescoUrl;

	@Autowired
	@Qualifier("alfrescoLogin")
	private String				alfrescoLogin;

	@Autowired
	@Qualifier("alfrescoPassword")
	private String				alfrescoPassword;

	@Autowired
	private CreateSession		createSession;

	@Autowired
	private CmisService			cmisService;

	private static String		staticAlfrescoUrl;

	@PostConstruct
	public void init() {
		AlfrescoCMISService.staticAlfrescoUrl = alfrescoUrl;
	}

	@Override
	public String uploadDocument(Integer idAgentOperateur, byte[] bFile, String titreFile, String descriptionFile,
			TypeEtatPayeurPointageEnum typeEtatPayeur) {

		Session session = null;
		try {
			session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);
		} catch (CmisConnectionException e) {
			logger.debug("Erreur de connexion a Alfresco CMIS : " + e.getMessage());
			throw e;
		}

		Document doc = null;
		String path = CmisUtils.getPathPointage(typeEtatPayeur);
		// on cherche si un etat payeur existe deja
		// si oui on ecrase
		CmisObject object = null;
		try {
			object = session.getObject(cmisService.getIdObjectCmis(path + titreFile, session));
		} catch (Exception e) {

		}

		ByteArrayInputStream stream = new ByteArrayInputStream(bFile);
		ContentStream contentStream = new ContentStreamImpl(titreFile, BigInteger.valueOf(bFile.length), MIME_TYPE, stream);

		if (null != object) {
			doc = (Document) object;
			doc.setContentStream(contentStream, true);
		} else {
			// sinon on cherche le repertoire distant pour creer le fichier
			object = session.getObject(cmisService.getIdObjectCmis(path, session));

			if (null == object) {
				throw new FolderAlfrescoNotFoundException("Path " + path + " non trouvé");
			}

			Folder folder = (Folder) object;
			int maxItemsPerPage = 5;
			OperationContext operationContext = session.createOperationContext();
			operationContext.setMaxItemsPerPage(maxItemsPerPage);

			// properties
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.NAME, titreFile);
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.DESCRIPTION, descriptionFile);

			// create a major version
			try {
				doc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
			} catch (CmisContentAlreadyExistsException e) {
				logger.debug(e.getMessage());
			}
		}

		if (null != doc.getProperty("cmis:secondaryObjectTypeIds")) {
			List<Object> aspects = doc.getProperty("cmis:secondaryObjectTypeIds").getValues();
			if (!aspects.contains("P:mairie:customDocumentAspect")) {
				aspects.add("P:mairie:customDocumentAspect");
				HashMap<String, Object> props = new HashMap<String, Object>();
				props.put("cmis:secondaryObjectTypeIds", aspects);
				doc.updateProperties(props);
				logger.debug("Added aspect");
			} else {
				logger.debug("Doc already had aspect");
			}
		}

		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("mairie:idAgentOwner", idAgentOperateur);
		props.put("mairie:idAgentCreateur", idAgentOperateur);
		doc.updateProperties(props);

		// on renvoi ID NODE ALFRESCO
		if(null != doc.getProperty("alfcmis:nodeRef")){
			return doc.getProperty("alfcmis:nodeRef").getFirstValue().toString();
		}
		return null;
	}

	/**
	 * exemple de nodeRef :
	 * "workspace://SpacesStore/1a344bd7-6422-45c6-94f7-5640048b20ab" exemple d
	 * URL a retourner :
	 * http://localhost:8080/alfresco/service/api/node/workspace/SpacesStore/418c511a-7c0a-4bb1-95a2-37e5946be726/content
	 * 
	 * @param nodeRef
	 *            String
	 * @return String l URL pour acceder au document directement a alfresco
	 */
	public static String getUrlOfDocument(String nodeRef) {

		return CmisUtils.getUrlOfDocument(staticAlfrescoUrl, nodeRef);
	}

	@Override
	public String getNodeRefFromPathOfFile(String titreFile, TypeEtatPayeurPointageEnum typeEtatPayeur) {

		if (null == titreFile)
			return null;

		Session session = createSession.getSession(alfrescoUrl, alfrescoLogin, alfrescoPassword);

		String path = CmisUtils.getPathPointage(typeEtatPayeur);
		CmisObject object = null;

		try {
			object = session.getObject(cmisService.getIdObjectCmis(path + titreFile, session));
		} catch (CmisObjectNotFoundException e) {
			logger.error(e.getMessage());
			return null;
		}

		if (null == object)
			return null;

		Document doc = (Document) object;

		return null != doc.getProperty("alfcmis:nodeRef") ? doc.getProperty("alfcmis:nodeRef").getFirstValue().toString() : null;
	}

}
