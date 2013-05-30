// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.domain.Spbarem;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Spbarem_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager Spbarem.entityManager;
    
    public static final EntityManager Spbarem.entityManager() {
        EntityManager em = new Spbarem().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Spbarem.countSpbarems() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Spbarem o", Long.class).getSingleResult();
    }
    
    public static List<Spbarem> Spbarem.findAllSpbarems() {
        return entityManager().createQuery("SELECT o FROM Spbarem o", Spbarem.class).getResultList();
    }
    
    public static Spbarem Spbarem.findSpbarem(String iban) {
        if (iban == null || iban.length() == 0) return null;
        return entityManager().find(Spbarem.class, iban);
    }
    
    public static List<Spbarem> Spbarem.findSpbaremEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Spbarem o", Spbarem.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Spbarem.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Spbarem.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Spbarem attached = Spbarem.findSpbarem(this.iban);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Spbarem.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Spbarem.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Spbarem Spbarem.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Spbarem merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
