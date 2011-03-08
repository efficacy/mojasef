package tests;

import java.net.URL;

import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.RemoteSpecCommonContext;
import org.stringtree.util.logging.Logger;

import junit.framework.TestCase;

public class CommonContextEmptyTest extends TestCase {
	CommonContext context;
	StringFinder finder;
	
	public void setUp() {
		context = new RemoteSpecCommonContext((URL)null);
		finder = new FetcherStringFinder(context);
	}
	
	public void testEnvironment() {
		assertEquals(System.getProperty("os.name"), finder.get("os.name"));
	}
	
	public void testDefaults() {
		assertTrue(context.getObject(MojasefConstants.SERVER_LOGGER) instanceof Logger);
		assertEquals("text/html", finder.get(HTTPConstants.RESPONSE_CONTENT_TYPE));
	}
}
