package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.http.Document;
import org.stringtree.http.HTTPClient;
import org.stringtree.http.MultipartForm;
import org.stringtree.mojasef.standalone.InlineServer;
import org.stringtree.mojasef.standalone.WebServer;

public class StandaloneMultipartTest extends TestCase {
    
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
    	MultipartForm form = new MultipartForm();
        form.put("first", new Document("primary"));
        form.put("second", new Document("secondary"));
System.err.println("posting " + form.dump());
        Document result = client.post(baseURL + "multi", form);
        assertEquals("200", result.getHeader(HTTPClient.HTTP_RESPONSE_CODE));
    }
    
    public void tearDown() {
        if (server.isRunning()) {
            server.halt();
        }
    }
}
