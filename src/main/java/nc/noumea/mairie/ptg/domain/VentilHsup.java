package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class VentilHsup {

	private Integer idAgent;
	
	private Date dateLundi;
	
	private double hAbsences;
	
	private double hTotal;
	
	private double hHorsContrat;
	
	private double hComplementaires;

	private double hsdjf;
	
	private double hsdjf25;
	
	private double hsdjf50;
	
	private double hMai;
	
	private double hSup;
	
	private double hsNuit;
	
	private double hsJour;
	
	private double hSup25;
	
	private double hSup50;
	
	private double hComposees;

	private double hNormales;

	private double hSimple;
}
