package tests;

import java.io.IOException;

import org.stringtree.Tract;
import org.stringtree.mojasef.local.LocalRequest;
import org.stringtree.mojasef.local.LocalServer;
import org.stringtree.tract.ByteTract;

import junit.framework.TestCase;

public class PluginTest extends TestCase {
	LocalServer server;
	
	public void setUp() throws IOException {
		server = new LocalServer("src/test/plugins/http.spec");
	}
    
    public void testNonMatchingRequest() {
        ByteTract request = new LocalRequest("simple");
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        assertEquals("Plugin: []", response.getContent());
    }
    
    public void testMatchingRequest() {
        ByteTract request = new LocalRequest("complex");
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        assertEquals("Plugin: [hello]", response.getContent());
    }
}
