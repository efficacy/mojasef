package org.stringtree.mojasef.local;

import java.io.IOException;
import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.mojasef.model.ModalOutputCollector;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.mojasef.model.Server;
import org.stringtree.tract.ByteTract;
import org.stringtree.tract.MapByteTract;

public class LocalServer extends Server {

	public LocalServer(String spec, String templates, String pub, Map<String, String> mimetypes) throws IOException {
        super(spec, templates, pub, mimetypes);
	}

    public LocalServer(String spec, String templates, String pub) throws IOException {
        super(spec, templates, pub, (Map<String, String>)null);
    }

    public LocalServer(String spec, String templates) throws IOException {
        super(spec, templates, null, (Map<String, String>)null);
    }

    public LocalServer(String spec) throws IOException {
        super(spec);
    }

    public LocalServer(String spec, boolean lock) throws IOException {
        super(spec, lock);
    }
    
    public LocalServer(Object app, String templatesUrl, String pubUrl) throws IOException {
    	super(app, "9999", templatesUrl, pubUrl);
    }
    
    public LocalServer(Object app, Fetcher tpl, Fetcher pub) throws IOException {
    	super(app, "9999", tpl, pub);
    }
    
    public LocalServer(Object app) throws IOException {
    	super(app, "9999", (Fetcher)null, (Fetcher)null);
    }

	public ByteTract request(ByteTract req) {
        ByteTract ret = new MapByteTract();
        RequestContext context = new LocalRequestContext(common, aliases, req, ret);
        ModalOutputCollector output = new LocalOutputCollector(ret, context);
        request(context, output);        
        return ret;
    }
}
