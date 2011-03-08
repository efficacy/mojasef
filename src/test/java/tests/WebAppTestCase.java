package tests;

import java.io.IOException;
import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.Repository;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.Mojasef;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.apps.Code404;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.LiteralCommonContext;
import org.stringtree.mojasef.model.RemoteSpecCommonContext;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.util.MethodCallUtils;


import junit.framework.TestCase;

public abstract class WebAppTestCase extends TestCase {

	protected CommonContext common;
	protected RequestContext context;
	protected TestOutputCollector output;
	protected Object application;
	
	protected Repository init(String spec) throws IOException {
		common = new RemoteSpecCommonContext(spec, false);
		return common;
	}

	protected Repository init(Map<String, Object> map) {
		common = new LiteralCommonContext(map);
		return common;
	}

	protected Repository init(Map<String, Object> map, Fetcher templates, Fetcher pub) {
		common = new LiteralCommonContext(map, templates, pub, true);
		return common;
	}
	
	protected String request(Repository repos, String method, String uri) throws IOException {
		context = new TestRequestContext(repos, method, uri);
		output = new TestOutputCollector(context);
 		application = context.getObject(MojasefConstants.HTTP_APPLICATION);

		output.start();
			Mojasef.delegateAndExpand(output, new FetcherStringFinder(context), application, Code404.code404);
		output.finish();
		
		return result();
	}

	protected String request(Repository repos, String uri) throws IOException {
		return request(repos, "GET", uri);
	}
	
	public String result() {
		return output.toString();
	}
	
	protected String request(String uri) throws IOException {
		return request(common, uri);
	}
	
	protected String request(String method, String uri) throws IOException {
		return request(common, method, uri);
	}

	protected void putAndInit(StringKeeper keeper, String key, Object obj) {
		keeper.put(key, obj);
	    MethodCallUtils.call(obj, "init", keeper);
	}
}
