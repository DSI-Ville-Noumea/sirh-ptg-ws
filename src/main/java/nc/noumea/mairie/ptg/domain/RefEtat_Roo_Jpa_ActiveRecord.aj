// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ptg.domain.RefEtat;
import org.springframework.transaction.annotation.Transactional;

privileged aspect RefEtat_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "ptgPersistenceUnit")
    transient EntityManager RefEtat.entityManager;
    
    public static final List<String> RefEtat.fieldNames4OrderClauseFilter = java.util.Arrays.asList("idRefEtat", "label");
    
    public static final EntityManager RefEtat.entityManager() {
        EntityManager em = new RefEtat().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long RefEtat.countRefEtats() {
        return entityManager().createQuery("SELECT COUNT(o) FROM RefEtat o", Long.class).getSingleResult();
    }
    
    public static List<RefEtat> RefEtat.findAllRefEtats() {
        return entityManager().createQuery("SELECT o FROM RefEtat o", RefEtat.class).getResultList();
    }
    
    public static List<RefEtat> RefEtat.findAllRefEtats(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RefEtat o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RefEtat.class).getResultList();
    }
    
    public static RefEtat RefEtat.findRefEtat(Integer idRefEtat) {
        if (idRefEtat == null) return null;
        return entityManager().find(RefEtat.class, idRefEtat);
    }
    
    public static List<RefEtat> RefEtat.findRefEtatEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM RefEtat o", RefEtat.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<RefEtat> RefEtat.findRefEtatEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RefEtat o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RefEtat.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void RefEtat.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void RefEtat.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            RefEtat attached = RefEtat.findRefEtat(this.idRefEtat);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void RefEtat.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void RefEtat.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public RefEtat RefEtat.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        RefEtat merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
