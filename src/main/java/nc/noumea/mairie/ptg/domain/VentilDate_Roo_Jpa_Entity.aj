// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import nc.noumea.mairie.ptg.domain.VentilDate;

privileged aspect VentilDate_Roo_Jpa_Entity {
    
    declare @type: VentilDate: @Entity;
    
    declare @type: VentilDate: @Table(name = "PTG_VENTIL_DATE");
    
    @Id
    @SequenceGenerator(name = "ventilDateGen", sequenceName = "PTG_S_VENTIL_DATE")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ventilDateGen")
    @Column(name = "ID_VENTIL_DATE")
    private Integer VentilDate.idVentilDate;
    
    @Version
    @Column(name = "version")
    private Integer VentilDate.version;
    
    public Integer VentilDate.getIdVentilDate() {
        return this.idVentilDate;
    }
    
    public void VentilDate.setIdVentilDate(Integer id) {
        this.idVentilDate = id;
    }
    
    public Integer VentilDate.getVersion() {
        return this.version;
    }
    
    public void VentilDate.setVersion(Integer version) {
        this.version = version;
    }
    
}
