package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.stringtree.Tract;
import org.stringtree.mojasef.local.LocalRequest;
import org.stringtree.mojasef.local.LocalServer;
import org.stringtree.mojasef.model.NonTerminalAlias;
import org.stringtree.mojasef.model.TerminalAlias;
import org.stringtree.tract.ByteTract;

import stubs.StringRet;

public class AliasTest extends TestCase {
    LocalServer server;
    
    public void setUp() throws IOException {
        server = new LocalServer(new StringRet());
    }
    
    private String request(String url) {
        ByteTract request = new LocalRequest(url);
        Tract response = server.request(request);
        assertTrue(response.hasContent());
        return response.getContent();
    }

	public void testNoAlias() throws IOException {
		assertEquals("StringRet index worked", request("/"));
		assertEquals("StringRet index worked", request("/index"));
		assertEquals("StringRet GET worked", request("/aa/index"));
		assertEquals("StringRet GET worked", request("/index_blah"));
		assertEquals("StringRet ugh worked", request("/ugh"));
		assertEquals("StringRet GET worked", request("/ugh_blah"));
		assertEquals("StringRet GET worked", request("/zot"));
	}

	public void testAliasOnSlash() throws IOException {
		server.addAlias("/", "/ugh");
		assertEquals("StringRet ugh worked", request("/"));
		assertEquals("StringRet index worked", request("/index"));
		assertEquals("StringRet GET worked", request("/index_blah"));
		assertEquals("StringRet ugh worked", request("/ugh"));
		assertEquals("StringRet GET worked", request("/ugh_blah"));
		assertEquals("StringRet GET worked", request("/zot"));
	}

	public void testAliasOnSlashIndex() throws IOException {
		server.addAlias("/index", "/ugh");
		assertEquals("StringRet index worked", request("/"));
		assertEquals("StringRet ugh worked", request("/index"));
		assertEquals("StringRet GET worked", request("/index_blah"));
		assertEquals("StringRet ugh worked", request("/ugh"));
		assertEquals("StringRet GET worked", request("/ugh_blah"));
		assertEquals("StringRet GET worked", request("/zot"));
	}

	public void testRegexToLiteral() throws IOException {
		server.addAlias("/(index)?", "/ugh");
		assertEquals("StringRet ugh worked", request("/"));
		assertEquals("StringRet ugh worked", request("/index"));
		assertEquals("StringRet GET worked", request("/index_blah"));
		assertEquals("StringRet ugh worked", request("/ugh"));
		assertEquals("StringRet GET worked", request("/ugh_blah"));
		assertEquals("StringRet GET worked", request("/zot"));
	}

	public void testRegexReplace() throws IOException {
		server.addAlias("/([^_]*)_blah", "/$1");
		assertEquals("StringRet index worked", request("/"));
		assertEquals("StringRet index worked", request("/index"));
		assertEquals("StringRet index worked", request("/index_blah"));
		assertEquals("StringRet ugh worked", request("/ugh"));
		assertEquals("StringRet ugh worked", request("/ugh_blah"));
		assertEquals("StringRet GET worked", request("/zot"));
	}

	public void testParameterExtraction() throws IOException {
		server.addAlias("/aa/([^/]+)", "/pp?a=$1");
		assertEquals("StringRet index worked", request("/"));
		assertEquals("StringRet index worked", request("/aa/"));
		assertEquals("StringRet finder worked a='hello' b=''", request("/aa/hello"));
	}

	public void testTerminalAndNonTerminalReplace() throws IOException {
		server.addAlias(new TerminalAlias("/blue", "/index"));
		server.addAlias(new NonTerminalAlias("/([^\\/]*)/(.*)", "/$2"));
		server.addAlias(new NonTerminalAlias("/", "/ugh"));
		server.addAlias(new NonTerminalAlias("/index", "/ugh"));
		assertEquals("StringRet ugh worked", request("/"));
		assertEquals("StringRet ugh worked", request("/index"));
		assertEquals("StringRet ugh worked", request("/aa/index"));
		assertEquals("StringRet index worked", request("/blue"));
	}

}
