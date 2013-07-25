// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.SppprmId;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Sppprm_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager Sppprm.entityManager;
    
    public static final EntityManager Sppprm.entityManager() {
        EntityManager em = new Sppprm().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Sppprm.countSppprms() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Sppprm o", Long.class).getSingleResult();
    }
    
    public static List<Sppprm> Sppprm.findAllSppprms() {
        return entityManager().createQuery("SELECT o FROM Sppprm o", Sppprm.class).getResultList();
    }
    
    public static Sppprm Sppprm.findSppprm(SppprmId id) {
        if (id == null) return null;
        return entityManager().find(Sppprm.class, id);
    }
    
    public static List<Sppprm> Sppprm.findSppprmEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Sppprm o", Sppprm.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Sppprm.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Sppprm.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Sppprm attached = Sppprm.findSppprm(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Sppprm.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Sppprm.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Sppprm Sppprm.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Sppprm merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
