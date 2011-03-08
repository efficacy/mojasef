package org.stringtree.mojasef.model;

import java.util.HashMap;

@SuppressWarnings("serial")
public class StandardMimeMap extends HashMap<String, String> {
    public StandardMimeMap() {
        put("png", "image/png");
        put("gif", "image/gif");
        put("jpeg", "image/jpeg");
        put("jpg", "image/jpeg");
        put("jpe", "image/jpeg");
        put("bmp", "image/bmp");
        put("ico", "image/x-icon");
        put("html", "text/html");
        put("htm", "text/html");
        put("xml", "text/xml");
        put("js", "text/javascript");
        put("json", "text/plain");
        put("css", "text/css");
        put("ps", "application/postscript");
        put("ai", "application/postscript");
        put("eps", "application/postscript");
        put("au", "audio/basic");
        put("snd", "audio/basic");
        put("wav", "audio/x-wav");             
        put("mp3", "audio/mp3");             
        put("mpeg", "video/mpeg");             
        put("mpe", "video/mpeg");             
        put("mpg", "video/mpeg");             
        put("qt", "video/quicktime");             
        put("mov", "video/quicktime");
        put("avi", "video/x-msvideo");
        put("asf", "video/x-msvideo");
        put("wmv", "video/x-msvideo");
    }
    
    public String get(Object key) {
        String ret = super.get(key);
        if (null == ret) {
            ret = "text/plain";
        }
        return ret;
    }
}
