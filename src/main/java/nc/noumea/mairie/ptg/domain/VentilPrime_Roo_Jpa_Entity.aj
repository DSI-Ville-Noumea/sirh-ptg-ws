// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;
import nc.noumea.mairie.ptg.domain.VentilPrime;

privileged aspect VentilPrime_Roo_Jpa_Entity {
    
    declare @type: VentilPrime: @Entity;
    
    declare @type: VentilPrime: @Table(name = "PTG_VENTIL_PRIME");
    
    @Version
    @Column(name = "version")
    private Integer VentilPrime.version;
    
    public Integer VentilPrime.getVersion() {
        return this.version;
    }
    
    public void VentilPrime.setVersion(Integer version) {
        this.version = version;
    }
    
}
