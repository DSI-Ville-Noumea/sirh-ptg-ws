// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nc.noumea.mairie.sirh.domain.Agent;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Agent_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext(unitName = "sirhPersistenceUnit")
    transient EntityManager Agent.entityManager;
    
    public static final EntityManager Agent.entityManager() {
        EntityManager em = new Agent().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Agent.countAgents() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Agent o", Long.class).getSingleResult();
    }
    
    public static List<Agent> Agent.findAllAgents() {
        return entityManager().createQuery("SELECT o FROM Agent o", Agent.class).getResultList();
    }
    
    public static Agent Agent.findAgent(Integer idAgent) {
        if (idAgent == null) return null;
        return entityManager().find(Agent.class, idAgent);
    }
    
    public static List<Agent> Agent.findAgentEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Agent o", Agent.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Agent.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Agent.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Agent attached = Agent.findAgent(this.idAgent);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Agent.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Agent.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Agent Agent.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Agent merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}