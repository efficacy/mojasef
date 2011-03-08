package tests;

import java.io.IOException;
import java.util.Date;

import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.apps.URLRouter;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.RemoteSpecCommonContext;

import junit.framework.TestCase;

public class CommonContextMountTest extends TestCase {
	CommonContext context;
	StringFinder finder;
	
	public void setUp() throws IOException {
		context = new RemoteSpecCommonContext("src/test/files/http2.spec");
		finder = new FetcherStringFinder(context);
	}
	
	public void testInitParam() {
		Object obj = context.getObject(MojasefConstants.HTTP_APPLICATION);
		assertTrue(obj instanceof URLRouter);
		assertEquals("bordered", finder.get("page.class.static"));
		Date date = new Date();
		assertEquals(date.toString(), finder.get("mojasef.system.timestamp"));
	}
}
