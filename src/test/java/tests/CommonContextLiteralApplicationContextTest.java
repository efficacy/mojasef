package tests;

import java.io.IOException;

import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.RemoteSpecCommonContext;

import junit.framework.TestCase;

public class CommonContextLiteralApplicationContextTest extends TestCase {
    CommonContext context;
    StringFinder finder;
    
    public void setUp() throws IOException {
        context = new RemoteSpecCommonContext("src/test/files/ccac.spec");
        finder = new FetcherStringFinder(context);
    }
    
    public void testBasicInit() {
        assertEquals("value1", finder.get("key1"));
    }
    
    public void testApplicationSimpleValue() {
        assertNotNull(context.getObject("http.application.context"));
        assertEquals("value2", finder.get("key2"));
    }
}
