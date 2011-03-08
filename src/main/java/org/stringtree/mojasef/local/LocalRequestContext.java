package org.stringtree.mojasef.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.stringtree.Fetcher;
import org.stringtree.Listable;
import org.stringtree.Tract;
import org.stringtree.mojasef.Alias;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.tract.ByteTract;

public class LocalRequestContext extends RequestContext {

	public LocalRequestContext(Fetcher context, Collection<Alias> aliases, ByteTract req, ByteTract res) {
		super(context, aliases);
		setRequestValues(req, res);
	}

	public LocalRequestContext(Fetcher context, ByteTract req, ByteTract res) {
		this(context, null, req, res);
	}

	@SuppressWarnings("unchecked")
    void setRequestValues(ByteTract req, ByteTract res) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] contentBytes = req.getContentBytes();
        InputStream in = new ByteArrayInputStream(contentBytes);
		setStreams(in, out);
		setRemoteAddress(req.get(HTTPConstants.REMOTE_ADDRESS));
        String query = null;
        if ("application/x-www-form-urlencoded".equals(req.get(HTTPConstants.REQUEST_CONTENT_TYPE))) {
            query = req.getContent();
            req.put(Tract.CONTENT, "");
        }
        String url = req.get(MojasefConstants.REQUEST_URI);
        put(MojasefConstants.REQUEST_SELF, withoutQuery(url));
        Fetcher headers = req.getUnderlyingFetcher();
        if (headers instanceof Listable) {
            Listable<String> listable = (Listable<String>)headers;
            Iterator<String> it = listable.list();
            while (it.hasNext()) {
                String key = it.next();
                Object object = headers.getObject(key);
                if (object instanceof String) {
                    String value = (String)object;
                    if (key.startsWith(HTTPConstants.REQUEST_HEADER)) {
                        key = key.substring(HTTPConstants.REQUEST_HEADER.length());
                    }
                    setHeader(key, value);
                }
            }
        }
		setRequest(req.get(HTTPConstants.REQUEST_PROTOCOL), req.get(HTTPConstants.REQUEST_METHOD), 
			url, query);
	}

    private Object withoutQuery(String url) {
        int q = url.indexOf('?');
        if (q > -1) {
            url = url.substring(0, q);
        }
        return url;
    }
}
