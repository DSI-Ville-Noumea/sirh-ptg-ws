// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.domain.Spbhor;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Spbhor_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager Spbhor.entityManager;
    
    public static final EntityManager Spbhor.entityManager() {
        EntityManager em = new Spbhor().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Spbhor.countSpbhors() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Spbhor o", Long.class).getSingleResult();
    }
    
    public static List<Spbhor> Spbhor.findAllSpbhors() {
        return entityManager().createQuery("SELECT o FROM Spbhor o", Spbhor.class).getResultList();
    }
    
    public static Spbhor Spbhor.findSpbhor(Integer cdthor) {
        if (cdthor == null) return null;
        return entityManager().find(Spbhor.class, cdthor);
    }
    
    public static List<Spbhor> Spbhor.findSpbhorEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Spbhor o", Spbhor.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Spbhor.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Spbhor.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Spbhor attached = Spbhor.findSpbhor(this.cdthor);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Spbhor.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Spbhor.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Spbhor Spbhor.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Spbhor merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
