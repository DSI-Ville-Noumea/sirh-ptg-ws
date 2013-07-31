package nc.noumea.mairie.ptg.repository;

public interface IMairieRepository {

	<T> T getEntity(Class<T> Tclass, Object Id);
}
