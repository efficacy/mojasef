package tests;

import java.io.IOException;

import org.stringtree.Repository;
import org.stringtree.Tract;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.mojasef.Mojasef;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.DirectFetcherTemplater;
import org.stringtree.template.Templater;
import org.stringtree.tract.MapTract;

import stubs.DflOut;
import stubs.StringRet;
import stubs.SysOut;

public class DelegateAndExpandTest extends StringCollectionTest {
    Object dfl;
    Repository templates;

    public void setUp() {
    	super.setUp();
        dfl = new DflOut();
        templates = new MapFetcher();
        context.put(Templater.TEMPLATE, templates);
        context.put(Templater.TEMPLATER, new DirectFetcherTemplater(templates));
    }

	public void testStringRetNoTemplate() throws IOException {
        Mojasef.delegateAndExpand(collector, keeper, new StringRet(), dfl);
        assertCollected("StringRet worked");
	}

	public void testSysOutNoTemplate() throws IOException {
        Mojasef.delegateAndExpand(collector, keeper, new SysOut(), dfl);
        assertCollected("SysOut worked");
	}

	public void testNoAppNoTemplate() throws IOException {
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("dropped through to DflOut");
	}

	public void testNoAppSimpleTemplateString() throws IOException {
		templates.put("default", "A[${this}]B");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("A[]B");
	}

	public void testNoAppSimpleTemplateTract() throws IOException {
		templates.put("default", new MapTract("START[${this}]END"));
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[]END");
	}

	public void testNoAppOverrideTemplate() throws IOException {
		context.put(Templater.TEMPLATE + ".default", new MapTract("START[${this}]END"));
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[]END");
	}

	public void testNoAppAttributedTemplate() throws IOException {
		Tract tpl = new MapTract("START[${this}+${ugh}]END");
		tpl.put("ugh", "hello");
		templates.put("default", tpl);
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[+hello]END");
	}

	// FIXME add processing to tract templating to explicitly override "this"
	public void x_testNoAppThisOverrideTemplate1() throws IOException {
		Tract tpl = new MapTract("START[${this}]END");
		tpl.put("this", "hello");
		templates.put("default", tpl);
		context.put("this", "huh");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[huh]END");
	}

	public void testNoAppThisOverrideTemplate2() throws IOException {
		Tract tpl = new MapTract("START[${this}]END");
		tpl.put("this", "hello");
		templates.put("default", tpl);
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[hello]END");
	}

	public void testThisPassThrough() throws IOException {
		context.put("this", "fkc");
		Tract tpl = new MapTract("START[${this*xx}]END");
		templates.put("default", tpl);
		templates.put("xx", ">${this}<");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[>fkc<]END");
	}

	public void testPageTemplate() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		context.put(MojasefConstants.PAGE_TEMPLATE, "xx");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("><");
	}

	public void testPageTemplateMissing() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		context.put(MojasefConstants.PAGE_TEMPLATE, "xy");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[]END");
	}

	public void testPageClass() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		context.put("page.class", "quoted");
		context.put("page.class.quoted", "xx");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("><");
	}

	public void testPageClassIncorrect() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		context.put("page.class", "quoted");
		context.put("page.class.quoted", "xy");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[]END");
	}

	public void testPageClassMissing() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		context.put("page.class", "quoted");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("START[]END");
	}

	public void testRequestUriFull() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		templates.put("index", "index:${this}");
		context.put(MojasefConstants.REQUEST_URI, "xx");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("><");
	}

	public void testRequestUriIndex() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		templates.put("index", "index:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("index:");
	}

	public void testRequestUriIndex2() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		templates.put("index", "index:${this}");
		context.put(MojasefConstants.REQUEST_URI, "ugh/");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("index:");
	}

	public void testRequestUriIndex3() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		templates.put("index", "index:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/ugh/");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("index:");
	}

	public void testRequestUriMatch() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		templates.put("index", "index:${this}");
		templates.put("ugh", "!ugh:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/ugh/");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("!ugh:");
	}

	public void testRequestUriMatch2() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("xx", ">${this}<");
		templates.put("index", "index:${this}");
		templates.put("ugh", "!ugh:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/ugh");
        Mojasef.delegateAndExpand(collector, keeper, null, dfl);
        assertCollected("!ugh:");
	}

	public void testJustTemplateDefault() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("index", "index:${this}");
		templates.put("xx", ">${this}<");
		templates.put("ugh", "!ugh:${this}");
        Mojasef.delegateAndExpand(collector, keeper, null, null);
        assertCollected("START[]END");
	}

	public void testJustTemplateIndex() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("index", "index:${this}");
		templates.put("xx", ">${this}<");
		templates.put("ugh", "!ugh:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/");
        Mojasef.delegateAndExpand(collector, keeper, null, null);
        assertCollected("index:");
	}

	public void testJustTemplateFull() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("index", "index:${this}");
		templates.put("xx", ">${this}<");
		templates.put("ugh", "!ugh:${this}");
		context.put(MojasefConstants.REQUEST_URI, "xx");
        Mojasef.delegateAndExpand(collector, keeper, null, null);
        assertCollected("><");
	}

	public void testJustTemplateMatch() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("index", "index:${this}");
		templates.put("xx", ">${this}<");
		templates.put("ugh", "!ugh:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/ugh/");
        Mojasef.delegateAndExpand(collector, keeper, null, null);
        assertCollected("!ugh:");
	}

	public void testJustTemplateMatch2() throws IOException {
		templates.put("default", "START[${this}]END");
		templates.put("index", "index:${this}");
		templates.put("xx", ">${this}<");
		templates.put("ugh", "!ugh:${this}");
		context.put(MojasefConstants.REQUEST_URI, "/ugh");
        Mojasef.delegateAndExpand(collector, keeper, null, null);
        assertCollected("!ugh:");
	}
}
