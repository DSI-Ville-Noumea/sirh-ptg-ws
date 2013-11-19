package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_VENTIL_HSUP")
@NamedQuery(name = "getPriorVentilHSupAgentAndDate", query = "select vh from VentilHsup vh where vh.idVentilHSup != :idLatestVentilHSup and vh.idAgent = :idAgent and vh.dateLundi = :dateLundi order by vh.idVentilHSup desc")
public class VentilHsup {

	@Id 
	@Column(name = "ID_VENTIL_HSUP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilHSup;
	
    @Column(name = "ID_AGENT")
    private Integer idAgent;
    
    @Column(name = "DATE_LUNDI")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLundi;
    
    @Column(name = "M_ABS")
    private int mAbsences;
    
    @Column(name = "M_HORS_CONTRAT")
    private int mHorsContrat;
    
    @Column(name = "M_SUP")
    private int mSup;
    
    @Column(name = "M_SUP_25")
    private int mSup25;
    
    @Column(name = "M_SUP_25_R")
    private int mSup25Recup;
    
    @Column(name = "M_SUP_50")
    private int mSup50;
    
    @Column(name = "M_SUP_50_R")
    private int mSup50Recup;
    
    @Column(name = "M_DJF")
    private int msdjf;
    
    @Column(name = "M_DJF_R")
    private int msdjfRecup;
    
    @Column(name = "M_DJF_25")
    private int msdjf25;
    
    @Column(name = "M_DJF_25_R")
    private int msdjf25Recup;
    
    @Column(name = "M_DJF_50")
    private int msdjf50;
    
    @Column(name = "M_DJF_50_R")
    private int msdjf50Recup;
    
    @Column(name = "M_1_MAI")
    private int mMai;
    
    @Column(name = "M_1_MAI_R")
    private int mMaiRecup;
    
    @Column(name = "M_NUIT")
    private int msNuit;
    
    @Column(name = "M_NUIT_R")
    private int msNuitRecup;
    
    @Column(name = "M_NORMALES")
    private int mNormales;
    
    @Column(name = "M_NORMALES_R")
    private int mNormalesRecup;
    
    @Column(name = "M_COMPLEMENTAIRES")
    private int mComplementaires;
    
    @Column(name = "M_COMPLEMENTAIRES_R")
    private int mComplementairesRecup;
    
    @Column(name = "M_SIMPLES")
    private int mSimple;
    
    @Column(name = "M_SIMPLES_R")
    private int mSimpleRecup;
    
    @Column(name = "M_COMPOSEES")
    private int mComposees;
    
    @Column(name = "M_COMPOSEES_R")
    private int mComposeesRecup;
    
    @Column(name = "M_RECUPEREES")
    private int mRecuperees;
    
    @Column(name = "ETAT")
    @Enumerated(EnumType.ORDINAL)
    private EtatPointageEnum etat;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDate;

}
