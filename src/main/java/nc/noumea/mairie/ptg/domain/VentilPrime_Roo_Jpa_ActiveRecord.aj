// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import org.springframework.transaction.annotation.Transactional;

privileged aspect VentilPrime_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "ptgPersistenceUnit")
    transient EntityManager VentilPrime.entityManager;
    
    public static final EntityManager VentilPrime.entityManager() {
        EntityManager em = new VentilPrime().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long VentilPrime.countVentilPrimes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM VentilPrime o", Long.class).getSingleResult();
    }
    
    public static List<VentilPrime> VentilPrime.findAllVentilPrimes() {
        return entityManager().createQuery("SELECT o FROM VentilPrime o", VentilPrime.class).getResultList();
    }
    
    public static VentilPrime VentilPrime.findVentilPrime(Integer idVentilPrime) {
        if (idVentilPrime == null) return null;
        return entityManager().find(VentilPrime.class, idVentilPrime);
    }
    
    public static List<VentilPrime> VentilPrime.findVentilPrimeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM VentilPrime o", VentilPrime.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void VentilPrime.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void VentilPrime.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            VentilPrime attached = VentilPrime.findVentilPrime(this.idVentilPrime);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void VentilPrime.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void VentilPrime.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public VentilPrime VentilPrime.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        VentilPrime merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}