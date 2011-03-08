package tests;

import org.stringtree.Repository;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.finder.FetcherStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.ByteArrayStringCollector;
import org.stringtree.template.StringCollector;

import junit.framework.TestCase;

public class StringCollectionTest extends TestCase {

	protected StringCollector collector;
	protected Repository context;
	protected StringKeeper keeper;

	public void assertCollected(String text) {
	    assertEquals(text, collector.toString());
	}

    public void setUp() {
        context = new MapFetcher();
        keeper = new FetcherStringKeeper(context);
        collector = new ByteArrayStringCollector();
        context.put(MojasefConstants.OUTPUT_COLLECTOR, collector);
    }
}
