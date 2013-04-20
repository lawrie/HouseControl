package net.geekgrandad.parser;

public class Token {
	private String value;
	private int type;
	private int index;
	
	public Token(String value, int type, int index) {
		this.value = value;
		this.type = type;
		this.index = index;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getType() {
		return type;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
