// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import org.springframework.transaction.annotation.Transactional;

privileged aspect VentilHsup_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "ptgPersistenceUnit")
    transient EntityManager VentilHsup.entityManager;
    
    public static final List<String> VentilHsup.fieldNames4OrderClauseFilter = java.util.Arrays.asList("idVentilHSup", "idAgent", "dateLundi", "mAbsences", "mHorsContrat", "mSup", "mSup25", "mSup25Recup", "mSup50", "mSup50Recup", "msdjf", "msdjfRecup", "msdjf25", "msdjf25Recup", "msdjf50", "msdjf50Recup", "mMai", "mMaiRecup", "msNuit", "msNuitRecup", "mNormales", "mNormalesRecup", "mComplementaires", "mComplementairesRecup", "mSimple", "mSimpleRecup", "mComposees", "mComposeesRecup", "mRecuperees", "etat", "ventilDate");
    
    public static final EntityManager VentilHsup.entityManager() {
        EntityManager em = new VentilHsup().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long VentilHsup.countVentilHsups() {
        return entityManager().createQuery("SELECT COUNT(o) FROM VentilHsup o", Long.class).getSingleResult();
    }
    
    public static List<VentilHsup> VentilHsup.findAllVentilHsups() {
        return entityManager().createQuery("SELECT o FROM VentilHsup o", VentilHsup.class).getResultList();
    }
    
    public static List<VentilHsup> VentilHsup.findAllVentilHsups(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM VentilHsup o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, VentilHsup.class).getResultList();
    }
    
    public static VentilHsup VentilHsup.findVentilHsup(Integer idVentilHSup) {
        if (idVentilHSup == null) return null;
        return entityManager().find(VentilHsup.class, idVentilHSup);
    }
    
    public static List<VentilHsup> VentilHsup.findVentilHsupEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM VentilHsup o", VentilHsup.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<VentilHsup> VentilHsup.findVentilHsupEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM VentilHsup o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, VentilHsup.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void VentilHsup.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void VentilHsup.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            VentilHsup attached = VentilHsup.findVentilHsup(this.idVentilHSup);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void VentilHsup.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void VentilHsup.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public VentilHsup VentilHsup.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        VentilHsup merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
