package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPPHRE", versionField = "")
public class Spphre {

	@EmbeddedId
	private SpphreId id;

	@Column(name = "CDRECU", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private SpphreRecupEnum spphreRecup;

	@Column(name = "NBH25", columnDefinition = "numeric")
	private double nbh25;

	@Column(name = "NBH50", columnDefinition = "numeric")
	private double nbh50;

	@Column(name = "NBHDIM", columnDefinition = "numeric")
	private double nbhdim;

	@Column(name = "NBHMAI", columnDefinition = "numeric")
	private double nbhmai;

	@Column(name = "NBHNUI", columnDefinition = "numeric")
	private double nbhnuit;

	@Column(name = "NBHSSI", columnDefinition = "numeric")
	private double nbhssimple;

	@Column(name = "NBHSCO", columnDefinition = "numeric")
	private double nbhscomposees;

	@Column(name = "NBHCOM", columnDefinition = "numeric")
	private double nbhcomplementaires;
}
