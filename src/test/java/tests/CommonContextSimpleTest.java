package tests;

import java.io.IOException;
import java.util.Date;

import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.RemoteSpecCommonContext;

import junit.framework.TestCase;

public class CommonContextSimpleTest extends TestCase {
	CommonContext context;
	StringFinder finder;
	
	public void setUp() throws IOException {
		context = new RemoteSpecCommonContext("src/test/files/cc1.spec");
		finder = new FetcherStringFinder(context);
	}
	
	public void testInitParam() {
		assertEquals("hello", finder.get("ugh"));
		assertEquals("", finder.get("page.class.static"));
		assertEquals(new Date().toString(), finder.get("dd"));
	}
}
