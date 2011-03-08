package org.stringtree.mojasef.apps;

import java.util.Map;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.routing.URLMount;

public class ContextURLRouter extends URLRouter {

    private String key;

    public ContextURLRouter(String key) {
        this.key = key;
    }

	@SuppressWarnings("unchecked")
    public void init(StringKeeper context) {
        createMounts(context);
        for (Map.Entry<String, Object> entry : ((Map<String, Object>)context.getObject(key)).entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();
            mounts.add(new URLMount(path, value));
         }
    }
}
