package org.stringtree.mojasef.apps;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.stringtree.Fetcher;
import org.stringtree.Tract;
import org.stringtree.fetcher.FallbackFetcher;
import org.stringtree.fetcher.FetcherHelper;
import org.stringtree.fetcher.StorerHelper;
import org.stringtree.fetcher.TractDirectoryRepository;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.FetcherTractFinder;
import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.finder.TractFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.DirectFetcherTemplater;
import org.stringtree.template.StreamStringCollector;
import org.stringtree.template.Templater;
import org.stringtree.tract.MapTract;

public class TemplateServer {
	protected File root = null;
	protected TractFinder tracts;
	protected TractFinder templates;
	protected Templater engine;
		
	public TemplateServer(String root) {
	    this.root = new File(root);
	}
	
	public TemplateServer() {
	    this.root = null;
	}
	
	public void mojasef_init(StringKeeper context) {
        if (root == null) root = new File(".");
        
		File contentDir = new File(root, "content");
		File pageDir = new File(root, "pages");
		File tplDir = new File(root, "templates");
		context.put(Tract.INDIRECT_ROOT, contentDir);
		
		setup(context, 
			new TractDirectoryRepository(pageDir, false, context), 
			new TractDirectoryRepository(tplDir, false, context));
	}

	public void setup(StringFinder context, Fetcher tracts, Fetcher templates) {
		Fetcher oldTemplates = (Fetcher)context.getObject(Templater.TEMPLATE);
		if (oldTemplates != null) {
			templates = new FallbackFetcher(
		        templates, oldTemplates);
		} else {
			templates = oldTemplates;
		}

		this.tracts = new FetcherTractFinder(tracts);
		this.templates = new FetcherTractFinder(templates);
		this.engine = new DirectFetcherTemplater(templates);
		
		//context.put(MojasefConstants.TEMPLATE_ENGINE, this.engine);
		//context.put(MojasefConstants.TRACT_FINDER, this.tracts);
		//context.put(MojasefConstants.TEMPLATE_FINDER, this.templates);
	}

	public void request(StringFinder context) {
		OutputStream outstream = (OutputStream)context.getObject(MojasefConstants.RESPONSE_STREAM);
		StreamStringCollector collector = new StreamStringCollector(outstream);

	    Tract page = null;
	    String filename = context.get(MojasefConstants.REQUEST_LOCALPATH);
	    page = (Tract)tracts.getObject(filename);

		if (page == null && ("".equals(filename) || filename.endsWith("/"))) {
		    page = (Tract)tracts.getObject(filename+context.get(MojasefConstants.DEFAULT_LEAFNAME));
		}

		if (page == null) {
		    page = (Tract)templates.getObject("404");
		}

		if (page == null) {
		    page = new MapTract("Oops, 404: ${mojasef.request.path.URI}");
		    StorerHelper.put(context, HTTPConstants.RESPONSE_CODE, "404");
		}

		StringFinder result = new FetcherStringFinder(
				new FallbackFetcher(page.getUnderlyingFetcher(), context.getUnderlyingFetcher()));
		Tract template = findTemplate(result);
		engine.expandTemplate(context, template, collector);
		collector.flush();
	}

	public static Tract findTemplate(StringFinder context) {
	    // try a directly specified template name
	    String template = context.get(MojasefConstants.PAGE_TEMPLATE);
	
	    // if that doesn't work, look for a page class, and look that one up
	    if ("".equals(template)) {
		    String pageclass = context.get("page.class");
		    if (!"".equals(pageclass)) {
		        template = context.get("page.class." + pageclass);
		    }
		}
	    
	    // build a list of all the possible templates to use, in priority order
	    List<String> possible = new ArrayList<String>();
	    
	    // build a list of all the possible templates to use, in priority order
	    if (!"".equals(template)) {
	        possible.add(template);
	    }
	    
	    // peel back the local part of the URL
	    String url = context.get(MojasefConstants.REQUEST_URI);
        if (url.startsWith("/")) url = url.substring(1);
	    if (url.endsWith("/")) url += MojasefConstants.DEFAULT_LEAF;
        String tail = getTail(url);
	    while (url.length() > 0) {
	        possible.add(url);
	        int sep = url.lastIndexOf("/");
	        if (sep <= 0) break;
	
	        url = url.substring(0,sep);
	    }
	    
        possible.add(tail);
	    possible.add("default");
        
	    // look up each tract in the list, return the first match
	    String prefix = Templater.TEMPLATE + ".";
	    Iterator<String> it = possible.iterator();
	    while (it.hasNext()) {
	        String fullname = prefix + it.next();
	        Tract tract = (Tract)FetcherHelper.getPeelback(context.getUnderlyingFetcher(), fullname);
	        if (tract != null) {
                return tract;
            }
	    }
	
	    return null;
	}

    private static String getTail(String url) {
        int lastSlash = url.lastIndexOf("/");
        return url.substring(lastSlash+1);
    }
}
