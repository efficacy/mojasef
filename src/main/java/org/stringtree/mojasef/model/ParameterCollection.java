package org.stringtree.mojasef.model;

import java.util.Map;

import org.stringtree.Storer;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.util.URLUtils;

public class ParameterCollection {
    
    MapFetcher args = new MapFetcher();
    
    public void update(String withEquals) {
        ParameterParser.addParameter(withEquals, args, ",");
    }
    
    public String asQueryString() {
        if (isEmpty()) return "";
        StringBuffer ret = new StringBuffer("?");
        
        boolean had = false;
        for (Map.Entry<String, Object> entry : args.getMap().entrySet()) {
            if (had) {
                ret.append("&");
            }
            had = true;
            ret.append(escapeQuery(entry.getKey()));
            ret.append("=");
            ret.append(escapeQuery(entry.getValue()));
        }
        
        return ret.toString();
    }
    
    public String asHiddenFields() {
        if (isEmpty()) return "";
        StringBuffer ret = new StringBuffer();
        
         for (Map.Entry<String, Object> entry : args.getMap().entrySet()) {
            ret.append("<input type=\"hidden\" name=\"");
            ret.append(escapeField(entry.getKey()));
            ret.append("\" value=\"");
            ret.append(escapeField(entry.getValue()));
            ret.append("\"/>");
        }
        
        return ret.toString();
    }

    public boolean isEmpty() {
        return args.getMap().isEmpty();
    }
    
    private String escapeQuery(Object obj) {
        return URLUtils.escape((String)obj);
    }
    
    private String escapeField(Object obj) {
        return ((String)obj).replaceAll("\\&", "&amp;").replaceAll("\"", "&quot;");
    }

    public void put(String name, String value) {
        args.put(name, value);
    }

    public void remove(String name) {
        args.remove(name);
    }

    public Storer getStorer() {
        return args;
    }
}
