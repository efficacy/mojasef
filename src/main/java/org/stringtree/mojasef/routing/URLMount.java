package org.stringtree.mojasef.routing;

import org.stringtree.Repository;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.PathInfoParser;

public class URLMount implements Mount{
    private String mountpoint;
    private Object application;
    private String[] dirs;

    public URLMount(String mountpoint, Object application) {
        this.mountpoint = mountpoint;
        this.application = application;
        this.dirs = mountpoint.split("/");
    }

    public boolean match(String path, StringKeeper context) {
        String[] dirs = path.split("/");
        if (this.dirs.length > dirs.length) return false;
        
        String mount = context.get(MojasefConstants.MOUNTCONTEXT) + context.get(MojasefConstants.MOUNTPOINT);
        StringBuffer realmount = new StringBuffer();
        Repository tokens = new MapFetcher();
        for (int i = 0; i < this.dirs.length; ++i) {
            if (isToken(this.dirs[i])) {
                tokens.put(tokenSymbol(this.dirs[i]), dirs[i]);
            } else if (!this.dirs[i].equals(dirs[i])){
                return false;
            }
            realmount.append(dirs[i]);
            realmount.append("/");
        }

        StorerHelper.putAll(tokens, context);
        context.put(MojasefConstants.MOUNTPOINT, realmount.toString());
        PathInfoParser.setContext(mount + path, context);
        return true;
    }

    public Object getApplication() {
        return application;
    }

    private String tokenSymbol(String string) {
        return string.substring(1, string.length()-1);
    }

    private boolean isToken(String string) {
        return string.startsWith("{") && string.endsWith("}");
    }
    
    public String toString() { return "URLMount(" + mountpoint + "=>" + application + ")"; }
}
