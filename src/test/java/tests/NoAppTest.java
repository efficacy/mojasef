package tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.stringtree.template.Templater;

public class NoAppTest extends WebAppTestCase {
	Map<String, Object> params;

	public void setUp() {
		params = new HashMap<String, Object>();
		params.put(Templater.TEMPLATE + ".index", "hello");
		init(params);
	}

	public void testSingleSlash() throws IOException {
		assertEquals("hello", request("/"));
	}
	
	public void testKnownName() throws IOException {
		assertEquals("hello", request("/index"));
	}
	
	public void testUnknownName() throws IOException {
		assertEquals("Page '/ugh' not found..", request("/ugh"));
	}
}
