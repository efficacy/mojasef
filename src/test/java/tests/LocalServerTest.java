package tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.stringtree.Tract;
import org.stringtree.mojasef.local.LocalPostRequest;
import org.stringtree.mojasef.local.LocalRequest;
import org.stringtree.mojasef.local.LocalServer;
import org.stringtree.tract.ByteTract;
import org.stringtree.util.testing.TestHelper;

import junit.framework.TestCase;

public class LocalServerTest extends TestCase {
    LocalServer server;
    
    public void setUp() throws IOException {
        server = new LocalServer("src/test/files/local/http.spec");
    }
    
    public void testGetRequest() {
        ByteTract request = new LocalRequest("ugh");
        request.setContent("<request>xx</request>");
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        assertEquals("hello xx", response.getContent());
    }
    
    public void testGetRequestWithSystemOutPollution() {
        ByteTract request = new LocalRequest("polluted");
        request.setContent("<request>xx</request>");
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        TestHelper.assertSameish("hello aa\r\nbb", response.getContent());
    }
    
    public void testPostMapRequest() {
        Map<String, String> args = new HashMap<String, String>();
        args.put("TEXT", "hello");
        args.put("twins", "Castor & Pollux");
        ByteTract request = new LocalPostRequest("ptest", args);
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        assertEquals("hello from Castor & Pollux", response.getContent());
    }
    
    public void testPostStringRequest() {
        ByteTract request = new LocalPostRequest("pstest", "thing", "text/crinkly");
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        assertEquals("hello from thing (text/crinkly)", response.getContent());
    }
}
