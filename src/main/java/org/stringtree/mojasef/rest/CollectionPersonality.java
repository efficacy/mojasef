package org.stringtree.mojasef.rest;

import java.io.IOException;
import java.io.InputStream;

import org.stringtree.Repository;
import org.stringtree.finder.StringKeeper;

public interface CollectionPersonality extends Repository {
	void list(StringKeeper context);
	String createId(StringKeeper context);
	Object parse(StringKeeper context, String id, InputStream in) throws IOException;
	Object empty(StringKeeper context, String id);
	boolean validate(StringKeeper context, String id);
}
