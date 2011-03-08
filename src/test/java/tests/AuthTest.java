package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.local.LocalRequest;
import org.stringtree.mojasef.local.LocalServer;
import org.stringtree.tract.ByteTract;

public class AuthTest extends TestCase {
    LocalServer server;
    ByteTract request;
    ByteTract response;
    
    public void setUp() throws IOException {
        server = new LocalServer("src/test/files/auth/http.spec");
    }

    public void testUnknown() {
        request = new LocalRequest("private/12346");
        response = server.request(request);
        assertEquals("404", response.get(HTTPConstants.RESPONSE_CODE));
    }

    public void testKnownButUnauthorized() {
        request = new LocalRequest("private/12345");
        response = server.request(request);
        assertEquals("401", response.get(HTTPConstants.RESPONSE_CODE));
        assertEquals("Basic realm=\"private:12345\"", response.get("WWW-Authenticate"));
    }

    public void testIncorrectlyAuthorized() {
        request = new LocalRequest("private/12345");
        request.put("Authorization", "Basic 12345:2222");
        response = server.request(request);
        assertEquals("401", response.get(HTTPConstants.RESPONSE_CODE));
        assertEquals("Basic realm=\"private:12345\"", response.get("WWW-Authenticate"));
    }

    public void testCorrectlyAuthorized() {
        request = new LocalRequest("private/12345");
        request.put("Authorization", "Basic 12345:1111");
        response = server.request(request);
        assertEquals("200", response.get(HTTPConstants.RESPONSE_CODE));
        assertEquals("hello", response.getContent());
    }
}
