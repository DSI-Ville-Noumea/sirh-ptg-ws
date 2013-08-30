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
import nc.noumea.mairie.ptg.domain.ExportPaieTask;

privileged aspect ExportPaieTask_Roo_Jpa_Entity {
    
    declare @type: ExportPaieTask: @Entity;
    
    declare @type: ExportPaieTask: @Table(name = "PTG_EXPORT_PAIE_TASK");
    
    @Id
    @SequenceGenerator(name = "exportPaieTaskGen", sequenceName = "PTG_S_EXPORT_PAIE_TASK")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "exportPaieTaskGen")
    @Column(name = "ID_EXPORT_PAIE_TASK")
    private Integer ExportPaieTask.idExportPaieTask;
    
    @Version
    @Column(name = "version")
    private Integer ExportPaieTask.version;
    
    public Integer ExportPaieTask.getIdExportPaieTask() {
        return this.idExportPaieTask;
    }
    
    public void ExportPaieTask.setIdExportPaieTask(Integer id) {
        this.idExportPaieTask = id;
    }
    
    public Integer ExportPaieTask.getVersion() {
        return this.version;
    }
    
    public void ExportPaieTask.setVersion(Integer version) {
        this.version = version;
    }
    
}
