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
import nc.noumea.mairie.ptg.domain.VentilAbsence;

privileged aspect VentilAbsence_Roo_Jpa_Entity {
    
    declare @type: VentilAbsence: @Entity;
    
    declare @type: VentilAbsence: @Table(name = "PTG_VENTIL_ABSENCE");
    
    @Id
    @SequenceGenerator(name = "ventilAbsenceGen", sequenceName = "PTG_S_VENTIL_ABSENCE")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ventilAbsenceGen")
    @Column(name = "ID_VENTIL_ABSENCE")
    private Integer VentilAbsence.idVentilAbsence;
    
    @Version
    @Column(name = "version")
    private Integer VentilAbsence.version;
    
    public Integer VentilAbsence.getIdVentilAbsence() {
        return this.idVentilAbsence;
    }
    
    public void VentilAbsence.setIdVentilAbsence(Integer id) {
        this.idVentilAbsence = id;
    }
    
    public Integer VentilAbsence.getVersion() {
        return this.version;
    }
    
    public void VentilAbsence.setVersion(Integer version) {
        this.version = version;
    }
    
}
