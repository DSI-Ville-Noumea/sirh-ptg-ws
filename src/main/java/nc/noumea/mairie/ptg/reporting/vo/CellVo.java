package nc.noumea.mairie.ptg.reporting.vo;

import java.awt.Color;

public class CellVo {
	
	private String text;
	private boolean isBold;
	private Integer colspan;
	private Color backgroundColor;
	
	public CellVo(String text) {
		this(text, false);
	}
	
	public CellVo(String text, boolean isBold) {
		this(text, isBold, 1, null);
	}
	
	public CellVo(String text, Integer colspan) {
		this(text, false, colspan, null);
	}
	
	public CellVo(String text, Color backgroundColor) {
		this(text, false, 1, backgroundColor);
	}
	
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor) {
		super();
		this.text = text;
		this.isBold = isBold;
		this.colspan = colspan;
		this.backgroundColor = backgroundColor;
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

	
	
}
