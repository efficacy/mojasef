package org.stringtree.mojasef.routing;

import java.util.Map;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.apps.URLRouter;

public class MapRouter extends URLRouter {
    private Map<String, Object> routes;

    public MapRouter(Map<String, Object> routes) {
        this.routes = routes;
    }
    
    public void init(StringKeeper context) {
        super.createMounts(context);
        for (Map.Entry<String, Object> entry : routes.entrySet()) {
             mounts.add(new URLMount(entry.getKey(), entry.getValue()));
        }
    }
    protected void notfound(StringKeeper context) {
        context.put(HTTPConstants.RESPONSE_CODE, "404");
    }
}
