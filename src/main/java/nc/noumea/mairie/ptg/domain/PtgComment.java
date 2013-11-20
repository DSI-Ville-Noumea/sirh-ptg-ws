package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_COMMENT")
public class PtgComment {

	@Id 
	@Column(name = "ID_COMMENT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPtgComment;
	
	@Column(name = "TEXT", columnDefinition="text")
	private String text;
	
	public PtgComment() {
		
	}
	
	public PtgComment(String comment) {
		this.text = comment;
	}
}
