package org.stringtree.mojasef.model;

import java.util.Map;

import org.stringtree.Fetcher;
import org.stringtree.util.spec.SpecProcessor;

public class LiteralCommonContext extends CommonContext {
    
    private Map<String, Object> map;

    public LiteralCommonContext(Map<String, Object> map, Fetcher templates, Fetcher pub, boolean lock) {
        super(templates, pub, lock);
        this.map = map;
        load();
    }

    public LiteralCommonContext(Map<String, Object> map, boolean lock) {
        this(map, null, null, lock);
    }

    public LiteralCommonContext(Map<String, Object> map) {
        this(map, true);
    }

    protected void readExternal(SpecProcessor spec) {
        putAll(map);
    }
}
