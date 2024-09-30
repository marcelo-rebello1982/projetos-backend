package br.com.cadastroit.services.controllers.model;

import java.io.Serializable;

public class QSInput implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6637724310813511261L;
	private String qs;
	private int draw;
	private int start;
	private int length;
	
	public String getQs() {
		return qs;
	}
	public void setQs(String qs) {
		this.qs = qs;
	}
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
}
