package org.stringtree.mojasef.model;

import org.stringtree.mojasef.Alias;

public class NonTerminalAlias extends Alias {
	public NonTerminalAlias(String pattern, String replacement) {
		super(pattern, replacement);
	}

	@Override
	public boolean terminates() {
		return false;
	}
}
