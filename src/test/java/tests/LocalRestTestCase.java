package tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.stringtree.Tract;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.local.LocalRequest;
import org.stringtree.mojasef.local.LocalServer;
import org.stringtree.util.testing.TestHelper;

public class LocalRestTestCase extends TestCase {

    protected LocalServer server;
    protected Tract response;
    protected LocalRequest request;
    protected String specURL;
    protected String fileRoot;
    
    public LocalRestTestCase(String specURL, String fileRoot) {
        this.specURL = specURL;
        this.fileRoot = fileRoot;
    }

    public void setUp() throws IOException {
        server = new LocalServer(specURL);
    }
    
    private static Map<String, String> types = new HashMap<String, String>();
    static {
        types.put(HTTPConstants.XML_CONTENT_TYPE, ".xml");
        types.put(HTTPConstants.JSON_CONTENT_TYPE, ".json");
        types.put(HTTPConstants.HTML_CONTENT_TYPE, ".html");
    }

    protected void callAndCheck(LocalRequest request, String name) {
        response = server.request(request);
        assertEquals("200", response.get(HTTPConstants.RESPONSE_CODE));
        assertTrue(response.hasContent());
        String filename = fileRoot + name;
        TestHelper.assertFileString(filename, response.getContent());
    }

    protected void callAndCheck(LocalRequest request, String name, String type) {
        String extension = types.get(type);
        callAndCheck(request, name + extension);
        TestHelper.assertSameish(type, response.get(HTTPConstants.RESPONSE_CONTENT_TYPE));
    }

    protected void callAndCheckJSON(LocalRequest request, String name) {
        callAndCheck(request, name, HTTPConstants.JSON_CONTENT_TYPE);
    }

    protected void callAndCheckHTML(LocalRequest request, String name) {
        callAndCheck(request, name, HTTPConstants.HTML_CONTENT_TYPE);
    }

    protected void callAndCheckXML(LocalRequest request, String name) {
        callAndCheck(request, name, HTTPConstants.XML_CONTENT_TYPE);
    }

    protected void callAndCheckCode(LocalRequest request, String code) {
        response = server.request(request);
        assertEquals(code, response.get(HTTPConstants.RESPONSE_CODE));
    }
}
