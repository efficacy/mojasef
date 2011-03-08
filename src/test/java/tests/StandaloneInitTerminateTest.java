package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.mojasef.standalone.InlineServer;
import org.stringtree.mojasef.standalone.WebServer;
import org.stringtree.util.URLReadingUtils;

public class StandaloneInitTerminateTest extends TestCase {
    
    InlineServer server;
    StandaloneInitTerminateTestApplication app;
    
    public void setUp() throws IOException {
        app = new StandaloneInitTerminateTestApplication();
        assertEquals("", app.out.toString());
        server = new InlineServer(app);
        WebServer.ensureStartup(server, 100);
    }

    public void testInitTerminate() throws IOException, InterruptedException {
        assertEquals("init(),", app.out.toString());
        server.put("example", "thing");
        assertEquals("hello", URLReadingUtils.readRawURL("http://localhost:" + server.getPort() + "/index"));
        assertEquals("init(),index(thing),", app.out.toString());
        server.halt();
        Thread.sleep(1000);
        assertEquals("init(),index(thing),terminate.", app.out.toString());
    }

    public void testNoTraffic() throws IOException, InterruptedException {
    }

    public void testNoTraffic2() throws IOException, InterruptedException {
    }

    public void testSlowProcess() throws IOException, InterruptedException {
        assertEquals("done", URLReadingUtils.readRawURL("http://localhost:" + server.getPort() + "/slow"));
    }

    public void testSlowProcess2() throws IOException, InterruptedException {
        assertEquals("done", URLReadingUtils.readRawURL("http://localhost:" + server.getPort() + "/slow"));
    }
    
    public void tearDown() {
        if (null != server) {
            WebServer.ensureShutdown(server, 1000);
        }
    }
}
