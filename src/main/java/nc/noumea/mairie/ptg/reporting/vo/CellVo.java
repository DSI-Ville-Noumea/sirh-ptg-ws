package nc.noumea.mairie.ptg.reporting.vo;

import java.awt.Color;

public class CellVo {
	
	private String text;
	private boolean isBold;
	private Integer colspan;
	private Color backgroundColor;
	private Integer horizontalAlign;

	/**
	 * @param text texte a afficher
	 * @see
	 * Par defaut :
	 *  - text non gras (normal)
	 *  - pas de couleur en fond (backgroundColor = null)
	 *  - colspan = 1
	 *  - aligne a gauche
	 */
	public CellVo(String text) {
		this(text, false);
	}
	
	/**
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @see
	 * Par defaut :
	 *  - pas de couleur en fond (backgroundColor = null)
	 *  - colspan = 1
	 *  - aligne a gauche
	 */
	public CellVo(String text, boolean isBold) {
		this(text, isBold, 1, null, null);
	}
	
	/**
	 * 
	 * @param text texte a afficher
	 * @param colspan colspan pour les colonnes d un tableau
	 * @see
	 * Par defaut :
	 *  - text non gras (normal)
	 *  - pas de couleur en fond (backgroundColor = null)
	 *  - aligne a gauche
	 */
	public CellVo(String text, Integer colspan) {
		this(text, false, colspan, null, null);
	}
	
	/**
	 * 
	 * @param text texte a afficher
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param horizontalAlign alignement horizontal du text 
	 * @see
	 * Par defaut :
	 *  - text non gras (normal)
	 *  - pas de couleur en fond (backgroundColor = null)
	 */
	public CellVo(String text, Integer colspan, Integer horizontalAlign) {
		this(text, false, colspan, null, horizontalAlign);
	}
	
	/**
	 * 
	 * @param text texte a afficher
	 * @param backgroundColor couleur en fond 
	 * @see
	 * Par defaut :
	 *  - text non gras (normal)
	 *  - colspan = 1
	 *  - aligne a gauche
	 */
	public CellVo(String text, Color backgroundColor) {
		this(text, false, 1, backgroundColor, null);
	}
	
	/**
	 * 
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param backgroundColor couleur en fond 
	 * @see
	 * Par defaut :
	 *  - aligne a gauche
	 */
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor) {
		this(text, isBold, colspan, backgroundColor, null);
	}
	
	/**
	 * 
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param backgroundColor couleur en fond 
	 * @param horizontalAlign alignement horizontal du text 
	 */
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor, Integer horizontalAlign) {
		super();
		this.text = text;
		this.isBold = isBold;
		this.colspan = colspan;
		this.backgroundColor = backgroundColor;
		this.horizontalAlign = horizontalAlign;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isBold() {
		return isBold;
	}
	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}
	public Integer getColspan() {
		return colspan;
	}
	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Integer getHorizontalAlign() {
		return horizontalAlign;
	}

	public void setHorizontalAlign(Integer horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
	}

}
