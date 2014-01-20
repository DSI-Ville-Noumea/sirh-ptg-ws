// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.sirh.domain.JourFerie;
import org.springframework.transaction.annotation.Transactional;

privileged aspect JourFerie_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager JourFerie.entityManager;
    
    public static final List<String> JourFerie.fieldNames4OrderClauseFilter = java.util.Arrays.asList("dateJour", "description", "typeJour");
    
    public static final EntityManager JourFerie.entityManager() {
        EntityManager em = new JourFerie().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long JourFerie.countJourFeries() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JourFerie o", Long.class).getSingleResult();
    }
    
    public static List<JourFerie> JourFerie.findAllJourFeries() {
        return entityManager().createQuery("SELECT o FROM JourFerie o", JourFerie.class).getResultList();
    }
    
    public static List<JourFerie> JourFerie.findAllJourFeries(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM JourFerie o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, JourFerie.class).getResultList();
    }
    
    public static JourFerie JourFerie.findJourFerie(Integer idJourFerie) {
        if (idJourFerie == null) return null;
        return entityManager().find(JourFerie.class, idJourFerie);
    }
    
    public static List<JourFerie> JourFerie.findJourFerieEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JourFerie o", JourFerie.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<JourFerie> JourFerie.findJourFerieEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM JourFerie o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, JourFerie.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void JourFerie.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void JourFerie.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            JourFerie attached = JourFerie.findJourFerie(this.idJourFerie);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void JourFerie.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void JourFerie.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public JourFerie JourFerie.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JourFerie merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
