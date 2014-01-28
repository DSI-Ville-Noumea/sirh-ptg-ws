package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "PTG_COMMENT")
public class PtgComment {

	@Id 
	@Column(name = "ID_COMMENT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPtgComment;
	
	@Column(name = "TEXT", columnDefinition="text")
	private String text;
	
	@Version
    @Column(name = "version")
	private Integer version;
	
	public PtgComment() {
		
	}
	
	public PtgComment(String comment) {
		this.text = comment;
	}

	public Integer getIdPtgComment() {
		return idPtgComment;
	}

	public void setIdPtgComment(Integer idPtgComment) {
		this.idPtgComment = idPtgComment;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
