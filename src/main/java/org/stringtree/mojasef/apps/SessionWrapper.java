package org.stringtree.mojasef.apps;

import org.stringtree.fetcher.MapFetcher;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.KeyFinder;
import org.stringtree.util.MethodCallUtils;

public class SessionWrapper extends ApplicationWrapper {
    protected MapFetcher sessions = null;
    
    public SessionWrapper(String classname) {
        super(classname);
        sessions = new MapFetcher();
    }
    
    public Object warmup(StringKeeper requestContext) {
        KeyFinder finder = (KeyFinder)requestContext.getObject(HTTPConstants.SESSION_KEYFINDER);
        String key = finder.findKey(requestContext, classname);
        Object application = sessions.getObject(key);
        if (null == application) {
            application = createApplication(requestContext);
            sessions.put(key, application);
        }
        
        Object redirect = MethodCallUtils.call(application, "warmup", requestContext);
        if (null != redirect) application = redirect;
        return application;
    }
}
