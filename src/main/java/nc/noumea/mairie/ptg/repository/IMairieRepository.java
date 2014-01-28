package nc.noumea.mairie.ptg.repository;

import nc.noumea.mairie.domain.Spmatr;

public interface IMairieRepository {

	<T> T getEntity(Class<T> Tclass, Object Id);
	
	void persistEntity(Object entity);
	
	void removeEntity(Object obj);
	
	Spmatr findSpmatrForAgent(Integer idAgent);
}
