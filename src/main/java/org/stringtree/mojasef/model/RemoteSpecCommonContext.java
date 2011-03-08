package org.stringtree.mojasef.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import org.stringtree.Fetcher;
import org.stringtree.fetcher.BytesURLFetcher;
import org.stringtree.fetcher.ContextSensitiveFetcher;
import org.stringtree.fetcher.TractURLFetcher;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.URLReadingUtils;
import org.stringtree.util.spec.SpecProcessor;

public class RemoteSpecCommonContext extends CommonContext {
    
	private URL specURL;

    public RemoteSpecCommonContext(URL spec, Fetcher templates, Fetcher pub, boolean lock) {
        super(templates, pub, lock);
        this.specURL = spec;
        if (templates instanceof ContextSensitiveFetcher) {
            ((ContextSensitiveFetcher)templates).setContext(this);
        }
        if (pub instanceof ContextSensitiveFetcher) {
            ((ContextSensitiveFetcher)pub).setContext(this);
        }
        load();
    }

    public RemoteSpecCommonContext(URL spec, URL tpl, URL pubfiles, boolean lock) {
        this(spec, new TractURLFetcher(tpl), new BytesURLFetcher(pubfiles), lock);
    }

	public RemoteSpecCommonContext(URL specURL) {
        this(specURL, (Fetcher)null, (Fetcher)null, true);
	}

	public RemoteSpecCommonContext(URL specURL, boolean lock) {
        this(specURL, (Fetcher)null, (Fetcher)null, lock);
	}

    public RemoteSpecCommonContext(String specURL) throws IOException {
        this(URLReadingUtils.findURL(specURL, "file"), (Fetcher)null, (Fetcher)null, true);
    }

    public RemoteSpecCommonContext(String specURL, boolean lock) throws IOException {
        this(URLReadingUtils.findURL(specURL, "file"), (Fetcher)null, (Fetcher)null, lock);
    }

    protected void readExternal(SpecProcessor spec) {
        try {
	        if (specURL != null) {
	            Iterator<String> it = spec.createLineIterator(new InputStreamReader(spec.open(specURL)), true);
	            
	            spec.read(it);

	            StringBuffer content = new StringBuffer();
	            while (it.hasNext()) {
                    String line = it.next();
                    content.append(line);
                    content.append('\n');
                }
                put(MojasefConstants.SPEC_CONTENT, content);
	        }

		} catch (IOException e) {
			put(MojasefConstants.ERROR_TEXT, "could not load initial specification from URL '" + specURL + "'");
			put(MojasefConstants.ERROR_EXCEPTION, e);
		}
    }
}
