package tests;

import org.stringtree.mojasef.local.LocalRequest;

public class AuthRestTest extends LocalRestTestCase {

    public AuthRestTest() {
        super("src/test/files/authrest/http.spec", "src/test/expected/authrest/");
    }

    public void testGetPublicUnknown() {
        request = new LocalRequest("public/12344");
        callAndCheckXML(request, "item12344");
    }

    public void testGetPublicKnown() {
        request = new LocalRequest("public/12345");
        callAndCheckXML(request, "item12345");
    }

    public void testGetPrivateUnknown() {
        request = new LocalRequest("private/12344");
        callAndCheckCode(request, "404");
    }

    public void testGetPrivateKnownButUnauthenticated() {
        request = new LocalRequest("private/12345");
        request.put("Authorization", "Basic 12345:1112");
        callAndCheckCode(request, "401");
        assertEquals("Basic realm=\"private:12345\"", response.get("WWW-Authenticate"));
    }

    public void testGetPrivateKnownButWrongPIN() {
        request = new LocalRequest("private/12345");
        callAndCheckCode(request, "401");
        assertEquals("Basic realm=\"private:12345\"", response.get("WWW-Authenticate"));
    }

    public void testGetPrivateKnownAndAuthenticated() {
        request = new LocalRequest("private/12345");
        request.put("Authorization", "Basic 12345:1111");
        callAndCheckXML(request, "item12345");
    }

    public void testGetPrivateIdUnknown() {
        request = new LocalRequest("id/12344/details");
        callAndCheckCode(request, "404");
    }

    public void testGetPrivateIdKnownButUnauthenticated() {
        request = new LocalRequest("id/12345/details");
        request.put("Authorization", "Basic 12345:1112");
        callAndCheckCode(request, "401");
        assertEquals("Basic realm=\"id:12345\"", response.get("WWW-Authenticate"));
    }

    public void testGetPrivateIdKnownButWrongPIN() {
        request = new LocalRequest("id/12345/details");
        callAndCheckCode(request, "401");
        assertEquals("Basic realm=\"id:12345\"", response.get("WWW-Authenticate"));
    }

    public void testGetPrivateIdKnownAndAuthenticated() {
        request = new LocalRequest("id/12345/details");
        request.put("Authorization", "Basic 12345:1111");
        callAndCheckXML(request, "item12345");
    }
}
