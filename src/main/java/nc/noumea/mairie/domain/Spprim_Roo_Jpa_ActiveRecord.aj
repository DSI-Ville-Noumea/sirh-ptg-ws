// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.domain.Spprim;
import nc.noumea.mairie.domain.SpprimId;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Spprim_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager Spprim.entityManager;
    
    public static final List<String> Spprim.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "dateFin", "montantPrime", "refArr", "datArr");
    
    public static final EntityManager Spprim.entityManager() {
        EntityManager em = new Spprim().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Spprim.countSpprims() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Spprim o", Long.class).getSingleResult();
    }
    
    public static List<Spprim> Spprim.findAllSpprims() {
        return entityManager().createQuery("SELECT o FROM Spprim o", Spprim.class).getResultList();
    }
    
    public static List<Spprim> Spprim.findAllSpprims(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Spprim o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Spprim.class).getResultList();
    }
    
    public static Spprim Spprim.findSpprim(SpprimId id) {
        if (id == null) return null;
        return entityManager().find(Spprim.class, id);
    }
    
    public static List<Spprim> Spprim.findSpprimEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Spprim o", Spprim.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Spprim> Spprim.findSpprimEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Spprim o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Spprim.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Spprim.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Spprim.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Spprim attached = Spprim.findSpprim(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Spprim.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Spprim.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Spprim Spprim.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Spprim merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
