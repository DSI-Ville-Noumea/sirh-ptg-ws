package nc.noumea.mairie.alfresco.cmis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class AlfrescoCMISServiceTest {
	
	@Test
	public void uploadDocument_objectExistAndOverride() {
		
		String idObjectCMIS = "aze";

		Property<?> property = Mockito.mock(Property.class);
		Document object = Mockito.mock(Document.class);
		object.getProperties().add(property);
				
		Session session = Mockito.mock(Session.class);
		Mockito.when(session.getObject(idObjectCMIS)).thenReturn(object);
				
		CreateSession createSession = Mockito.mock(CreateSession.class);
		Mockito.when(createSession.getSession(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		
		CmisService cmisService = Mockito.mock(CmisService.class);
		Mockito.when(cmisService.getIdObjectCmis(Mockito.anyString(), Mockito.any(Session.class))).thenReturn(idObjectCMIS);
		
		AlfrescoCMISService service = new AlfrescoCMISService();
		ReflectionTestUtils.setField(service, "createSession", createSession);
		ReflectionTestUtils.setField(service, "cmisService", cmisService);
		
		byte[] bFile = new String("data").getBytes();
		
		service.uploadDocument(9005138, bFile, "titreFile", "descriptionFile", TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
		
		Mockito.verify(object, Mockito.times(1)).setContentStream(Mockito.any(ContentStream.class), Mockito.anyBoolean());
		Mockito.verify(object, Mockito.times(1)).updateProperties(Mockito.anyMapOf(String.class, Object.class));
	}
	
	@Test
	public void uploadDocument_folderNotExist() {
		
		String idObjectCMIS = "aze";

		Property<?> property = Mockito.mock(Property.class);
		Document object = Mockito.mock(Document.class);
		object.getProperties().add(property);
				
		Session session = Mockito.mock(Session.class);
		Mockito.when(session.getObject(idObjectCMIS)).thenReturn(null);
				
		CreateSession createSession = Mockito.mock(CreateSession.class);
		Mockito.when(createSession.getSession(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		
		CmisService cmisService = Mockito.mock(CmisService.class);
		Mockito.when(cmisService.getIdObjectCmis(Mockito.anyString(), Mockito.any(Session.class))).thenReturn(idObjectCMIS);
		
		AlfrescoCMISService service = new AlfrescoCMISService();
		ReflectionTestUtils.setField(service, "createSession", createSession);
		ReflectionTestUtils.setField(service, "cmisService", cmisService);
		
		byte[] bFile = new String("data").getBytes();
		
		try {
		service.uploadDocument(9005138, bFile, "titreFile", "descriptionFile", TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
		} catch(FolderAlfrescoNotFoundException e) {
			Mockito.verify(object, Mockito.never()).setContentStream(Mockito.any(ContentStream.class), Mockito.anyBoolean());
			Mockito.verify(object, Mockito.never()).updateProperties(Mockito.anyMapOf(String.class, Object.class));
			return;
		}
		
		fail();
	}
	
	@Test
	public void uploadDocument_DocNotExist_andCreate() {
		
		String idObjectCMIS = "idDoc";
		String idfolderCMIS = "idFolder";
		String titreFile = "titreFile";

		Property<?> property = Mockito.mock(Property.class);
		Document object = Mockito.mock(Document.class);
		object.getProperties().add(property);
		
		Folder folder = Mockito.mock(Folder.class);
		Mockito.when(folder.createDocument(Mockito.anyMapOf(String.class, Object.class), Mockito.any(ContentStream.class), Mockito.any(VersioningState.class)))
			.thenReturn(object);
		
		OperationContext operationContext = Mockito.mock(OperationContext.class);
				
		Session session = Mockito.mock(Session.class);
		Mockito.when(session.getObject(idObjectCMIS)).thenReturn(null);
		Mockito.when(session.getObject(idfolderCMIS)).thenReturn(folder);
		Mockito.when(session.createOperationContext()).thenReturn(operationContext);
				
		CreateSession createSession = Mockito.mock(CreateSession.class);
		Mockito.when(createSession.getSession(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		
		String path = CmisUtils.getPathPointage(TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
		
		CmisService cmisService = Mockito.mock(CmisService.class);
		Mockito.when(cmisService.getIdObjectCmis(path + titreFile, session)).thenReturn(idObjectCMIS);
		Mockito.when(cmisService.getIdObjectCmis(path, session)).thenReturn(idfolderCMIS);
		
		AlfrescoCMISService service = new AlfrescoCMISService();
		ReflectionTestUtils.setField(service, "createSession", createSession);
		ReflectionTestUtils.setField(service, "cmisService", cmisService);
		
		byte[] bFile = new String("data").getBytes();
		
		try {
		service.uploadDocument(9005138, bFile, titreFile, "descriptionFile", TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
		} catch(FolderAlfrescoNotFoundException e) {
			fail();
			return;
		}
		
		Mockito.verify(object, Mockito.never()).setContentStream(Mockito.any(ContentStream.class), Mockito.anyBoolean());
		Mockito.verify(object, Mockito.times(1)).updateProperties(Mockito.anyMapOf(String.class, Object.class));
		Mockito.verify(folder, Mockito.times(1)).createDocument(Mockito.anyMapOf(String.class, Object.class), Mockito.any(ContentStream.class), Mockito.any(VersioningState.class));
	}
	
	@Test
	public void getNodeRefFromPathOfFile_null() {
		
		String titreFile = null;
		
		AlfrescoCMISService service = new AlfrescoCMISService();
		
		assertNull(service.getNodeRefFromPathOfFile(titreFile, TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE));
	}
	
	@Test
	public void getNodeRefFromPathOfFile_notFound() {

		String idObjectCMIS = "idDoc";
		String titreFile = "titreFile";

		Session session = Mockito.mock(Session.class);
		Mockito.when(session.getObject(idObjectCMIS)).thenReturn(null);
		
		String path = CmisUtils.getPathPointage(TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
		
		CmisService cmisService = Mockito.mock(CmisService.class);
		Mockito.when(cmisService.getIdObjectCmis(path + titreFile, session)).thenReturn(idObjectCMIS);
		
		CreateSession createSession = Mockito.mock(CreateSession.class);
		Mockito.when(createSession.getSession(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		
		AlfrescoCMISService service = new AlfrescoCMISService();
		ReflectionTestUtils.setField(service, "createSession", createSession);
		ReflectionTestUtils.setField(service, "cmisService", cmisService);
		
		assertNull(service.getNodeRefFromPathOfFile(titreFile, TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE));
	}
	
	@Test
	public void getNodeRefFromPathOfFile_Found() {

		String idObjectCMIS = "idDoc";
		String titreFile = "titreFile";

		Property<Object> property = Mockito.mock(Property.class);
		Mockito.when(property.getFirstValue()).thenReturn("nodeRef");
		Document object = Mockito.mock(Document.class);
		object.getProperties().add(property);
		Mockito.when(object.getProperty("alfcmis:nodeRef")).thenReturn(property);
		
		Session session = Mockito.mock(Session.class);
		Mockito.when(session.getObject(idObjectCMIS)).thenReturn(object);

		String path = CmisUtils.getPathPointage(TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE);
		
		CmisService cmisService = Mockito.mock(CmisService.class);
		Mockito.when(cmisService.getIdObjectCmis(path + titreFile, session)).thenReturn(idObjectCMIS);
		
		CreateSession createSession = Mockito.mock(CreateSession.class);
		Mockito.when(createSession.getSession(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(session);
		
		AlfrescoCMISService service = new AlfrescoCMISService();
		ReflectionTestUtils.setField(service, "createSession", createSession);
		ReflectionTestUtils.setField(service, "cmisService", cmisService);
		
		assertEquals(service.getNodeRefFromPathOfFile(titreFile, TypeEtatPayeurPointageEnum.TYPE_ETAT_PAYEUR_POINTAGE), "nodeRef");
	}
}
