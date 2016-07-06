package nc.noumea.mairie.ptg.dto;

public interface IJSONDeserialize<T> {
	public T deserializeFromJSON(String json);
}
