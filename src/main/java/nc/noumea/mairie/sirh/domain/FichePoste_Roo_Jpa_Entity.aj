// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import nc.noumea.mairie.sirh.domain.FichePoste;

privileged aspect FichePoste_Roo_Jpa_Entity {
    
    declare @type: FichePoste: @Entity;
    
    declare @type: FichePoste: @Table(schema = "SIRH", name = "FICHE_POSTE");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_FICHE_POSTE")
    private Integer FichePoste.idFichePoste;
    
    public Integer FichePoste.getIdFichePoste() {
        return this.idFichePoste;
    }
    
    public void FichePoste.setIdFichePoste(Integer id) {
        this.idFichePoste = id;
    }
    
}