// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ReposCompTask_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "ptgPersistenceUnit")
    transient EntityManager ReposCompTask.entityManager;
    
    public static final EntityManager ReposCompTask.entityManager() {
        EntityManager em = new ReposCompTask().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long ReposCompTask.countReposCompTasks() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ReposCompTask o", Long.class).getSingleResult();
    }
    
    public static List<ReposCompTask> ReposCompTask.findAllReposCompTasks() {
        return entityManager().createQuery("SELECT o FROM ReposCompTask o", ReposCompTask.class).getResultList();
    }
    
    public static ReposCompTask ReposCompTask.findReposCompTask(Integer idRcTask) {
        if (idRcTask == null) return null;
        return entityManager().find(ReposCompTask.class, idRcTask);
    }
    
    public static List<ReposCompTask> ReposCompTask.findReposCompTaskEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ReposCompTask o", ReposCompTask.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void ReposCompTask.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void ReposCompTask.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            ReposCompTask attached = ReposCompTask.findReposCompTask(this.idRcTask);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void ReposCompTask.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void ReposCompTask.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public ReposCompTask ReposCompTask.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ReposCompTask merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}