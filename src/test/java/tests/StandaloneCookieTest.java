package tests;

import java.util.Map;

import org.stringtree.mojasef.model.RequestContext;

import junit.framework.TestCase;

public class StandaloneCookieTest extends TestCase {
	Map<String, String> cookies;
	
	public void testEmpty() {
		assertTrue(RequestContext.extractCookies(null).isEmpty());
		assertTrue(RequestContext.extractCookies("").isEmpty());
	}
	
	public void testSingleWithNoPadding() {
		cookies = RequestContext.extractCookies("a=b");
		assertEquals(1, cookies.size());
		assertEquals("b", cookies.get("a"));
	}
	
	public void testSingleWithPaddingSpaces() {
		cookies = RequestContext.extractCookies(" a = b ");
		assertEquals(1, cookies.size());
		assertEquals("b", cookies.get("a"));
	}
	
	public void testSingleWithEscapedValue() {
		cookies = RequestContext.extractCookies("a=%3b");
		assertEquals(1, cookies.size());
		assertEquals(";", cookies.get("a"));
	}
	
	public void testMultipleWithNoPadding() {
		cookies = RequestContext.extractCookies("a=b;c=d");
		assertEquals(2, cookies.size());
		assertEquals("b", cookies.get("a"));
		assertEquals("d", cookies.get("c"));
	}
	
	public void testMultipleWithPaddingSpaces() {
		cookies = RequestContext.extractCookies("  a  =  b  ;  c  =  d  ");
		assertEquals(2, cookies.size());
		assertEquals("b", cookies.get("a"));
		assertEquals("d", cookies.get("c"));
	}
}
