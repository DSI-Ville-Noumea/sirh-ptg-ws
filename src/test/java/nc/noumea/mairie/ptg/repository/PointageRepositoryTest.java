package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;

import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;

import org.junit.Test;

public class PointageRepositoryTest {

	@Test
	public void getIdPointagesParents_noPointageParent_ReturnEmptyList() {
		
		// Given
		Pointage p = new Pointage();
		p.setIdPointage(12);
		
		PointageRepository repo = new PointageRepository();
		
		// When
		List<Integer> result = repo.getIdPointagesParents(p);
		
		// Then
		assertEquals(0, result.size());
	}
}
