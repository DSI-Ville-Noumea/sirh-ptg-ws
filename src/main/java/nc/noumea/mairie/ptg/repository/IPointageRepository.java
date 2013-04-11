package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;

public interface IPointageRepository {

	List<Integer> getIdPointagesParents(Pointage pointage);
}
