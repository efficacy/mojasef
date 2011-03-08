package tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.LiteralCommonContext;
import org.stringtree.util.logging.Logger;

import junit.framework.TestCase;

public class LiteralContextSimpleTest extends TestCase {
	Fetcher context;
	StringFinder finder;
    Map<String, Object> map = new HashMap<String, Object>();
	
	public void setUp() throws IOException {
        map.put("ugh", "thing");
		context = new LiteralCommonContext(map);
		finder = new FetcherStringFinder(context);
	}
	
    public void testEnvironment() {
        assertEquals(System.getProperty("os.name"), finder.get("os.name"));
    }
    
    public void testDefaults() {
        assertTrue(context.getObject(MojasefConstants.SERVER_LOGGER) instanceof Logger);
        assertEquals("text/html", finder.get(HTTPConstants.RESPONSE_CONTENT_TYPE));
    }

	public void testInitLiteral() {
		assertEquals("thing", finder.get("ugh"));
	}
}
