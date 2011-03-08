package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.Fetcher;
import org.stringtree.Tract;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.local.LocalRequest;
import org.stringtree.mojasef.local.LocalServer;
import org.stringtree.mojasef.model.CommonContext;
import org.stringtree.mojasef.model.RemoteSpecCommonContext;
import org.stringtree.tract.ByteTract;

public class CommonContextFallbackTest extends TestCase {
    
    public void testGetFromContext() throws IOException {
        CommonContext context = new RemoteSpecCommonContext("src/test/files/fb/fa.spec");
        Fetcher tpls = (Fetcher)context.getObject(MojasefConstants.TEMPLATE_SOURCE);
        assertEquals("I am aa: ${*hello}", tpls.getObject("home"));
        assertEquals("Hello, ${value}!", tpls.getObject("hello"));
    }

    private void check(LocalServer server, String expected, String page) {
        ByteTract request = new LocalRequest(page);
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        assertEquals(expected, response.getContent());
    }

    public void testUseInServerA() throws IOException {
        LocalServer server = new LocalServer("src/test/files/fb/fa.spec");
        check(server, "I am aa: Hello, thing!", "home");
    }

    public void testUseInServerB() throws IOException {
        LocalServer server = new LocalServer("src/test/files/fb/fb.spec");
        check(server, "I am bb: Hello, thing!", "home");
    }
    
    public void testDynamicSelection() throws IOException {
        LocalServer server = new LocalServer("src/test/files/fb/fx.spec");
        check(server, "I am bb: Hello, wotsit!", "select?type=bb");
        check(server, "I am aa: Hello, wotsit!", "select?type=aa");
    }
}
