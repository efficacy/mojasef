package org.stringtree.mojasef.model;

import org.stringtree.Fetcher;
import org.stringtree.mojasef.MojasefConstants;

public class ContextFileFetcher implements Fetcher {

	private Fetcher context;

	public ContextFileFetcher(Fetcher context) {
		this.context = context;
	}

	public Object getObject(String name) {
		return context.getObject(MojasefConstants.FILE_FETCHER + "." + name);
	}

}
