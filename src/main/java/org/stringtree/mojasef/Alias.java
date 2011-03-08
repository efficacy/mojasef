package org.stringtree.mojasef;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Alias {
	private Pattern pattern;
	private String replacement;
	
	public Alias(String pattern, String replacement) {
		this.pattern = Pattern.compile(pattern);
		this.replacement = replacement;
	}

	public Matcher match(String url) {
		Matcher matcher = pattern.matcher(url);
		return matcher.matches() ? matcher : null;
	}
	
	public String replace(Matcher matcher, String url) {
		return matcher.replaceAll(replacement);
	}

	public abstract boolean terminates();
}
