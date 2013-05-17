// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.ptg.domain.PtgComment;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PtgComment_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "ptgPersistenceUnit")
    transient EntityManager PtgComment.entityManager;
    
    public static final EntityManager PtgComment.entityManager() {
        EntityManager em = new PtgComment().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long PtgComment.countPtgComments() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PtgComment o", Long.class).getSingleResult();
    }
    
    public static List<PtgComment> PtgComment.findAllPtgComments() {
        return entityManager().createQuery("SELECT o FROM PtgComment o", PtgComment.class).getResultList();
    }
    
    public static PtgComment PtgComment.findPtgComment(Integer idPtgComment) {
        if (idPtgComment == null) return null;
        return entityManager().find(PtgComment.class, idPtgComment);
    }
    
    public static List<PtgComment> PtgComment.findPtgCommentEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PtgComment o", PtgComment.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void PtgComment.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void PtgComment.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            PtgComment attached = PtgComment.findPtgComment(this.idPtgComment);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void PtgComment.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void PtgComment.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public PtgComment PtgComment.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PtgComment merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
