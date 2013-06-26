package nc.noumea.mairie.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPBASE", versionField = "")
public class Spbase {

	@Id
	@Column(name = "CDBASE", columnDefinition = "char", updatable = false, insertable = false)
	private String cdBase;
	
	@Column(name = "LIBASE", columnDefinition = "char")
	private String liBase;

	@Column(name = "NBASHH", columnDefinition = "numeric")
	private double nbashh;
	
	@Column(name = "CDCBAS", columnDefinition = "numeric")
	private double cdcbas;
	
	@Column(name = "NBAHSA", columnDefinition = "numeric")
	private double nbahsa;
	
	@Column(name = "NBAHDI", columnDefinition = "numeric")
	private double nbahdi;
	
	@Column(name = "NBAHLU", columnDefinition = "numeric")
	private double nbahlu;
	
	@Column(name = "NBAHMA", columnDefinition = "numeric")
	private double nbahma;
	
	@Column(name = "NBAHME", columnDefinition = "numeric")
	private double nbahme;
	
	@Column(name = "NBAHJE", columnDefinition = "numeric")
	private double nbahje;
	
	@Column(name = "NBAHVE", columnDefinition = "numeric")
	private double nbahve;
	
	@Column(name = "NBASCH", columnDefinition = "numeric")
	private double nbasch;
	
	@Transient
	public double getDayBase(int day) {
		
		switch(day) {
			case 0:
				return getNbahlu();
			case 1:
				return getNbahma();
			case 2:
				return getNbahme();
			case 3:
				return getNbahje();
			case 4:
				return getNbahve();
			case 5:
				return getNbahsa();
			case 6:
				return getNbahdi();
				default:
					return 0.0;
		}
	}
	
	@Transient
	public int getDayBaseInMinutes(int day) {
		
		Double baseMairie = 0d;
		
		switch(day) {
			case 0:
				baseMairie = getNbahlu();
				break;
			case 1:
				baseMairie = getNbahma();
				break;
			case 2:
				baseMairie = getNbahme();
				break;
			case 3:
				baseMairie = getNbahje();
				break;
			case 4:
				baseMairie = getNbahve();
				break;
			case 5:
				baseMairie = getNbahsa();
				break;
			case 6:
				baseMairie = getNbahdi();
				break;
		}
		
		return convertMairieNbHeuresFormatToMinutes(baseMairie);
	}

	public int getWeekBaseInMinutes() {
		return convertMairieNbHeuresFormatToMinutes(getNbashh());
	}
	
	public static int convertMairieNbHeuresFormatToMinutes(Double nbHeuresMairies) {
		
		BigDecimal v = new BigDecimal(String.valueOf(nbHeuresMairies));
		int nbHours = v.intValue();
		int nbMinutes = v.multiply(BigDecimal.TEN).subtract(new BigDecimal(nbHours).multiply(BigDecimal.TEN)).intValue() * 10;
		
		return nbHours * 60 + nbMinutes;
	}
}
