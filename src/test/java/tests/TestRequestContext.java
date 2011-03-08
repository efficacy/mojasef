package tests;

import org.stringtree.Fetcher;
import org.stringtree.mojasef.model.RequestContext;

public class TestRequestContext extends RequestContext {

	public TestRequestContext(Fetcher context, String method, String uri) {
		super(context, null);
		setRemoteAddress("10.10.10.10");
		setRequest("http", method, uri);
	}

}
