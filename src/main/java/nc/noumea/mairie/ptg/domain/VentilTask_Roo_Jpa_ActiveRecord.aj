// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ptg.domain.VentilTask;
import org.springframework.transaction.annotation.Transactional;

privileged aspect VentilTask_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "ptgPersistenceUnit")
    transient EntityManager VentilTask.entityManager;
    
    public static final EntityManager VentilTask.entityManager() {
        EntityManager em = new VentilTask().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long VentilTask.countVentilTasks() {
        return entityManager().createQuery("SELECT COUNT(o) FROM VentilTask o", Long.class).getSingleResult();
    }
    
    public static List<VentilTask> VentilTask.findAllVentilTasks() {
        return entityManager().createQuery("SELECT o FROM VentilTask o", VentilTask.class).getResultList();
    }
    
    public static VentilTask VentilTask.findVentilTask(Integer idVentilTask) {
        if (idVentilTask == null) return null;
        return entityManager().find(VentilTask.class, idVentilTask);
    }
    
    public static List<VentilTask> VentilTask.findVentilTaskEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM VentilTask o", VentilTask.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void VentilTask.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void VentilTask.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            VentilTask attached = VentilTask.findVentilTask(this.idVentilTask);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void VentilTask.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void VentilTask.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public VentilTask VentilTask.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        VentilTask merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}