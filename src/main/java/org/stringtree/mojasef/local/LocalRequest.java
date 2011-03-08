package org.stringtree.mojasef.local;

import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.tract.MapByteTract;

public class LocalRequest extends MapByteTract {
    public LocalRequest(String path) {
        this("", "GET", path, "HTTP/1.0");
    }

    public LocalRequest(String mountpoint, String path) {
        this(mountpoint, "GET", path, "HTTP/1.0");
    }

    public LocalRequest(String mountpoint, String method, String path) {
        this(mountpoint, method, path, "HTTP/1.0");
    }

    public LocalRequest(String mountpoint, String method, String path, String protocol) {
        put(MojasefConstants.MOUNTCONTEXT, mountpoint);
        put(HTTPConstants.REQUEST_METHOD, method);
        put(MojasefConstants.REQUEST_URI, path);
        put(HTTPConstants.REQUEST_PROTOCOL, protocol);
    }
}
