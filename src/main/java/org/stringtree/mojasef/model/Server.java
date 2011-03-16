package org.stringtree.mojasef.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.fetcher.BytesURLFetcher;
import org.stringtree.fetcher.FallbackFetcher;
import org.stringtree.fetcher.PeelbackFetcher;
import org.stringtree.fetcher.TractURLFetcher;
import org.stringtree.finder.FetcherStringFinder;
import org.stringtree.finder.FetcherStringKeeper;
import org.stringtree.mojasef.Alias;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.Mojasef;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.apps.Code404;
import org.stringtree.mojasef.standalone.WebServer;
import org.stringtree.util.LiteralMap;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.URLReadingUtils;

public class Server {
    protected CommonContext common;
    protected Collection<Alias> aliases;
    protected Map<String, String> mimetypes;

    public Server(CommonContext context, Collection<Alias> aliases, Map<String, String> mimetypes) {
        this.mimetypes = mimetypes;
        this.aliases = aliases;
        init(context);
    }

    public Server(URL spec, URL templates, URL pub, Map<String, String> mimetypes, boolean lock) {
        this.mimetypes = mimetypes;
        try {
            CommonContext context = new RemoteSpecCommonContext(
                spec, 
                templates != null ? templates : new URL(spec, "templates/"),
                pub != null ? pub : new URL(spec, "public/"),
                lock);
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(URL spec, URL templates, URL pub, Map<String, String> mimetypes) {
        this(spec, templates, pub, mimetypes, true);
    }

    public Server(String specLocation, String templatesLocation, String publicLocation, Map<String, String> mimetypes, boolean lock) throws IOException {
        this(
            URLReadingUtils.findURL(specLocation, "file"),
            null == templatesLocation ? null : URLReadingUtils.findURL(templatesLocation, "file"),
            null == publicLocation ? null : URLReadingUtils.findURL(publicLocation, "file"),
            null == mimetypes ? new StandardMimeMap() : mimetypes,
            lock
        );
    }

    public Server(String specLocation, String templatesLocation, String publicLocation, Map<String, String> mimetypes) throws IOException {
        this(specLocation, templatesLocation, publicLocation, mimetypes, true);
    }

    public Server(String url, boolean lock) throws IOException {
        this(url, null, null, null, lock);
    }

    public Server(String url) throws IOException {
        this(url, true);
    }

    public Server(Map<String, Object> settings, boolean lock) {
        CommonContext context = new LiteralCommonContext(settings, lock);
        init(context);
    }
    
    public Server(Map<String, Object> settings) {
        CommonContext context = new LiteralCommonContext(settings, true);
        init(context);
    }
    
    public Server(Object app, String port, Fetcher tpl, Fetcher pubfiles, Collection<Alias> aliases) {
    	this(new LiteralCommonContext(new LiteralMap<String, Object>(
    			MojasefConstants.HTTP_APPLICATION, app,
    			WebServer.SERVER_PORT, port
    		), tpl, pubfiles, true), aliases, null);
    }
    
    public Server(Object app, String port, Fetcher tpl, Fetcher pubfiles) {
    	this(app, port, tpl, pubfiles, null);
    }
    
    public Server(Object app, String port, URL tpl, URL pubfiles) {
    	this(app, port, new TractURLFetcher(tpl), new BytesURLFetcher(pubfiles));
    }
    
    public Server(Object app, String port, String tplURL, String pubURL) throws IOException {
    	this(app, port, URLReadingUtils.findURL(tplURL, "file"), URLReadingUtils.findURL(pubURL, "file"));
    }
    
    public Server(Object app, String port, List<String> tplURL, List<String> pubURL) throws IOException {
    	this(app, port, convertStringListToFallbackFetcher(tplURL, true), 
    			convertStringListToFallbackFetcher(pubURL, false));
    }
    
    public Server(Object app, String port, List<String> tplURL, List<String> pubURL, Collection<Alias> aliases) throws IOException {
    	this(app, port, convertStringListToFallbackFetcher(tplURL, true), 
    			convertStringListToFallbackFetcher(pubURL, false), aliases);
    }
    
    public void addAlias(Alias alias) {
    	if (null == aliases) {
    		aliases = new ArrayList<Alias>();
    	}
    	aliases.add(alias);
    }
    
    public void addAlias(String pattern, String replacement) {
    	addAlias(new NonTerminalAlias(pattern, replacement));
    }
    
    private static Fetcher convertStringListToFallbackFetcher(List<String> strings, boolean isTpl) throws IOException {
       	List<Fetcher> fetchers = new ArrayList<Fetcher>(strings.size());
    	for (String string : strings) {
    		URL url = URLReadingUtils.findURL(string, "file");
    		if (isTpl) {
    			fetchers.add(new TractURLFetcher(url));
    		} else {
        		fetchers.add(new BytesURLFetcher(url));
    		}
    	}
    	return new FallbackFetcher(fetchers);
    }

	@SuppressWarnings("unchecked")
    public void init(CommonContext context) {
        this.common = context;
        if (null == mimetypes) {
            mimetypes = (Map<String, String>) context.getObject(MojasefConstants.MOJASEF_CONFIG_MIME_MAP);
            if (null == mimetypes) {
                mimetypes = new StandardMimeMap();
            }
        }
        context.put(MojasefConstants.MOJASEF_CONFIG_MIME_MAP, mimetypes);
        appCall("mojasef_init");
    }

    public void terminate() {
        appCall("mojasef_terminate");
    }

    private void appCall(String method) {
//Diagnostics.dumpFetcher(common, "Server.appCall common");
        Object application = common.getObject(MojasefConstants.HTTP_APPLICATION);
        
        if (application != null) {
            MethodCallUtils.call(application, method, new FetcherStringKeeper(common), false);
        } else {
            System.err.println("Server.appCall unable to locate " + MojasefConstants.HTTP_APPLICATION);
        }
    }
    
    public void request(RequestContext req, OutputCollector res, Object dfl) {
        Fetcher context = new FallbackFetcher(req, common);
        try {
            Object file = null;
            res.start();
            String leaf = (String)context.getObject(MojasefConstants.REQUEST_LOCALPATH);
            if (leaf.indexOf(".") > 0) {
                Fetcher pub = (Fetcher)context.getObject(MojasefConstants.FILE_FETCHER);
                file = pub.getObject(leaf);
            }
            if (file != null) {
                String type = mimetype(leaf);
                req.put(HTTPConstants.RESPONSE_CONTENT_TYPE, type);
                res.write((byte[])file);
            } else {
                Object application = common.getObject(MojasefConstants.HTTP_APPLICATION);
                Mojasef.delegateAndExpand(res, new FetcherStringFinder(new PeelbackFetcher(context)), application, dfl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            res.finish();
        }
    }

	private String mimetype(String leaf) {
        String extension = leaf;
        int offset = leaf.lastIndexOf('.');
        if (offset >= 0) {
            extension = leaf.substring(offset+1);
        }
        return mimetypes.get(extension);
    }

    public void request(RequestContext req, OutputCollector res) {
        request(req, res, Code404.code404);
    }
    
    public CommonContext getCommonContext() {
        return common;
    }

	public Collection<Alias> getAliases() {
		return aliases;
	}
}
