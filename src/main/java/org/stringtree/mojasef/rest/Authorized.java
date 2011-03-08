package org.stringtree.mojasef.rest;

import java.util.Map;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.Mojasef;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.ContextClassUtils;
import org.stringtree.util.MethodCallUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.iterator.Spliterator;

public class Authorized {
    private Map<String, String> known;
    private String idkey = MojasefConstants.REQUEST_PATHOBJECT;
    private String mapkey = "known";
    private String appname;
    private String apptail;
    private Object application;
    private String realm;
    
    public Authorized(String tail) {
        Spliterator it = new Spliterator(tail);
        this.realm = it.hasNext() ? it.nextString() : null;
        this.idkey = it.hasNext() ? convertIdKey(it.nextString()) : null;
        this.mapkey = it.hasNext() ? it.nextString() : null;
        this.appname = it.hasNext() ? it.nextString() : null;
        this.apptail = it.hasNext() ? it.tail().trim() : null;
    }
    
    private String convertIdKey(String key) {
        key = key.trim();
        if ("OBJECT".equals(key)) {
            key = MojasefConstants.REQUEST_PATHOBJECT;
        }
        return key;
    }

	@SuppressWarnings("unchecked")
    public void init(StringKeeper context) {
        known = (Map<String, String>)context.getObject(mapkey);
        application = ContextClassUtils.ensureObject(appname, apptail, context); 
        MethodCallUtils.call(application, "init", context);
    }
    
    public void request(StringKeeper context) {
        String id = context.get(idkey);

        String secret = known.get(id);
        if (StringUtils.isBlank(secret)) {
            context.put(HTTPConstants.RESPONSE_CODE, "404");
            return;
        }
        
        String credentials = context.get(HTTPConstants.REQUEST_HEADER_AUTHORIZATION);
        if (credentials.startsWith("Basic ")) {
            credentials = credentials.substring("Basic ".length());
        }
        Spliterator it = new Spliterator(credentials, ":");
        String credId = it.hasNext() ? it.nextString() : "";
        String credPin = it.hasNext() ? it.nextString() : "";
        
        if (!(id.equals(credId) && secret.equals(credPin))) {
            context.put(HTTPConstants.RESPONSE_CODE, "401");
            context.put(HTTPConstants.RESPONSE_HEADER_AUTHENTICATE, "Basic realm=\"" + realm + ":" + id + "\"");
            return;
        }

        Mojasef.delegate(context, application);
    }
}
