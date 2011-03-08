package org.stringtree.mojasef.standalone;

import java.util.HashMap;
import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.fetcher.FetcherHelper;
import org.stringtree.mojasef.MojasefConstants;

@SuppressWarnings("serial")
class Args extends HashMap<String, Object> {
    public Args(Object app, String port) {
        put(MojasefConstants.HTTP_APPLICATION, app);
        put(WebServer.SERVER_PORT, port);
    }
}

public class InlineServer extends WebServer {
    public static final String DFL_PORT = "24761";

    public InlineServer(Object app, String port) {
        super(new Args(app, port), false);
    }
    
    public InlineServer(Fetcher settings) {
        super(FetcherHelper.asMap(settings), false);
    }
    
    public InlineServer(Map<String, Object> settings) {
        super(settings, false);
    }

    public InlineServer(Object app) {
        this(app, DFL_PORT);
    }
    
    public String getPort() {
        return (String)getObject(WebServer.SERVER_PORT);
    }
}
