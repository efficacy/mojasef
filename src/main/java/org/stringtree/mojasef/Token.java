package org.stringtree.mojasef;

public class Token {
	private String name;
	
    public Token(String string) {
    	this.name = "Token." + string;
	}
    
    public String toString() {
    	return name;
    }
    
	public static final Token CONTINUE = new Token("CONTINUE"); // no match found, keep looking
    public static final Token DONE = new Token("DONE"); // match found, stop looking
}
