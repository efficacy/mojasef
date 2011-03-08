package tests;

import java.io.IOException;

import org.stringtree.http.Document;
import org.stringtree.http.Form;
import org.stringtree.http.HTTPClient;
import org.stringtree.mojasef.standalone.InlineServer;
import org.stringtree.mojasef.standalone.WebServer;

import junit.framework.TestCase;

public class StandalonePostTest extends TestCase {
    
    InlineServer server;
    StandaloneInitTerminateTestApplication app;
    HTTPClient client;
    String baseURL;
    
    public void setUp() throws IOException {
        app = new StandaloneInitTerminateTestApplication();
        server = new InlineServer(app);
        WebServer.ensureStartup(server, 100);
        baseURL = "http://localhost:" + server.getPort() + "/";
        client = new HTTPClient();
    }
    
    public void testPut() throws IOException {
        Form form = new Form();
        form.put("first", "primary");
        form.put("second", "secondary");
        Document result = client.post(baseURL + "action", form);
        assertEquals("200", result.getHeader(HTTPClient.HTTP_RESPONSE_CODE));
        assertEquals("action(primary,secondary)", result.getContentAsString());
    }
    
    public void tearDown() {
        if (server.isRunning()) {
            server.halt();
        }
    }
}
