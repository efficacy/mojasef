package org.stringtree.mojasef.model;

import org.stringtree.mojasef.Alias;

public class TerminalAlias extends Alias {
	public TerminalAlias(String pattern, String replacement) {
		super(pattern, replacement);
	}

	@Override
	public boolean terminates() {
		return true;
	}
}
