package tests;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.stringtree.fetcher.MapFetcher;
import org.stringtree.finder.FetcherStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.model.ParameterParser;

import junit.framework.TestCase;

class StringInputStream extends ByteArrayInputStream
{
	public StringInputStream(String s)
	{
		super(s.getBytes());
	}
}

public class ParameterHeaderTest 
	extends TestCase
{
	StringKeeper store;
	String HEADER = "http.header.";
	String COOKIE = "http.header.";
	
	public void setUp()
	{
		store = new FetcherStringKeeper(new MapFetcher());
	}

	public void testEmpty1()
	{
		ParameterParser.parse("", store);
		assertEquals("", store.get("a"));
	}

	public void testEmpty2()
	{
		ParameterParser.parse("", HEADER, COOKIE, new BufferedReader(new StringReader("")), store);
		assertEquals("", store.get("a"));
	}

	public void testSingleQuery()
	{
		ParameterParser.parse("a=b", store);
		assertEquals("b", store.get("a"));
	}

	public void testDoubleQuery()
	{
		ParameterParser.parse("a=b&c=d", store);
		assertEquals("b", store.get("a"));
		assertEquals("d", store.get("c"));
	}

	public void testDuplicatedQuery()
	{
		ParameterParser.parse("a=b&a=d", store);
		assertEquals("d", store.get("a"));
	}

	public void testPlusQuery()
	{
		ParameterParser.parse("a=b+d", store);
		assertEquals("b d", store.get("a"));
	}

	public void testHexQuery()
	{
		ParameterParser.parse("a=b%3Ad", store);
		assertEquals("b:d", store.get("a"));
	}

	public void testSingleHeader()
	{
		ParameterParser.parse("", HEADER, COOKIE, 
		        new BufferedReader(new StringReader("a: b")), store);
		assertEquals("b", store.get("http.header.a"));
	}

	public void testDoubleHeader()
	{
		ParameterParser.parse("", HEADER, COOKIE, 
                new BufferedReader(new StringReader("a: b\r\nc: d")), store);
		assertEquals("b", store.get("http.header.a"));
		assertEquals("d", store.get("http.header.c"));
	}

	public void testDoubleHeaderAndBody()
	{
		ParameterParser.parse("", HEADER, COOKIE, 
                new BufferedReader(new StringReader("a: b\r\nc: d\r\n\r\nhello: there")), store);
		assertEquals("b", store.get("http.header.a"));
		assertEquals("d", store.get("http.header.c"));
		assertEquals("", store.get("http.header.hello"));
	}

	public void testMultipleCalls()
	{
		ParameterParser.parse("a=b", store);
		assertEquals("b", store.get("a"));
		assertEquals("", store.get("c"));
		
		store.clear();

		ParameterParser.parse("c=d", store);
		assertEquals("", store.get("a"));
		assertEquals("d", store.get("c"));
	}
}
