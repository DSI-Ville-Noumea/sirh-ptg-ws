// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Spabsen_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager Spabsen.entityManager;
    
    public static final EntityManager Spabsen.entityManager() {
        EntityManager em = new Spabsen().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Spabsen.countSpabsens() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Spabsen o", Long.class).getSingleResult();
    }
    
    public static List<Spabsen> Spabsen.findAllSpabsens() {
        return entityManager().createQuery("SELECT o FROM Spabsen o", Spabsen.class).getResultList();
    }
    
    public static Spabsen Spabsen.findSpabsen(SpabsenId id) {
        if (id == null) return null;
        return entityManager().find(Spabsen.class, id);
    }
    
    public static List<Spabsen> Spabsen.findSpabsenEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Spabsen o", Spabsen.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Spabsen.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Spabsen.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Spabsen attached = Spabsen.findSpabsen(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Spabsen.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Spabsen.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Spabsen Spabsen.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Spabsen merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
