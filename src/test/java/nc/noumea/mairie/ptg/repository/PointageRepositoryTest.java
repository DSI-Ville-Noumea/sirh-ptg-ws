package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import nc.noumea.mairie.ptg.domain.Pointage;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PointageRepositoryTest {

	@Test
	public void getIdPointagesParents_noPointageParent_ReturnEmptyList() {
		
		// Given
		Pointage p = new Pointage();
		p.setIdPointage(12);
		
		Query q = Mockito.mock(Query.class);
		Mockito.when(q.getResultList()).thenReturn(new ArrayList<String>());
		
		EntityManager moqEm = Mockito.mock(EntityManager.class);
		Mockito.when(moqEm.createNativeQuery("SELECT t1.ID_POINTAGE FROM PTG_POINTAGE t1 START WITH t1.ID_POINTAGE = :idPointage CONNECT BY PRIOR t1.ID_POINTAGE_PARENT = t1.ID_POINTAGE"))
			.thenReturn(q);
		
		PointageRepository repo = new PointageRepository();
		ReflectionTestUtils.setField(repo, "ptgEntityManager", moqEm);
		
		// When
		List<Integer> result = repo.getIdPointagesParents(p);
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void getIdPointagesParents_2LevelPointagesParent_ReturnListOf2() {
		
		// Given
		Pointage p = new Pointage();
		p.setIdPointage(12);
		
		Query q = Mockito.mock(Query.class);
		Mockito.when(q.getResultList()).thenReturn(Arrays.asList(112, 54));
		
		EntityManager moqEm = Mockito.mock(EntityManager.class);
		Mockito.when(moqEm.createNativeQuery("SELECT t1.ID_POINTAGE FROM PTG_POINTAGE t1 START WITH t1.ID_POINTAGE = :idPointage CONNECT BY PRIOR t1.ID_POINTAGE_PARENT = t1.ID_POINTAGE"))
			.thenReturn(q);
		
		PointageRepository repo = new PointageRepository();
		ReflectionTestUtils.setField(repo, "ptgEntityManager", moqEm);
		
		// When
		List<Integer> result = repo.getIdPointagesParents(p);
		
		// Then
		assertEquals(2, result.size());
		assertEquals(112, (int) result.get(0));
		assertEquals(54, (int) result.get(1));
	}
}
