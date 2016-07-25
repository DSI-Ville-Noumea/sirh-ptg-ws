package nc.noumea.mairie.ptg.domain;

import org.hibernate.envers.RevisionListener;

public class AuditListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		final RevInfo revision = (RevInfo) revisionEntity;
	}

}