package org.stringtree.mojasef.apps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.Mojasef;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.Token;
import org.stringtree.mojasef.routing.Mount;
import org.stringtree.template.StringCollector;

public abstract class URLRouter {
	protected StringFinder initContext;
	protected String mountpoint;
	protected List<Mount> mounts;

    protected void createMounts(StringKeeper context) {
        this.initContext = context;
		mountpoint = context.get(MojasefConstants.MOUNTCONTEXT) + context.get(MojasefConstants.MOUNTPOINT);
		mounts = new ArrayList<Mount>();
    }

    public void request(StringKeeper context) {
		StringCollector collector = (StringCollector)context.getObject(MojasefConstants.OUTPUT_COLLECTOR);
		Iterator<Mount> it = mounts.iterator();
        String path = context.get(MojasefConstants.REQUEST_LOCALPATH);
        Token status = Token.CONTINUE;
        
		while (status == Token.CONTINUE && it.hasNext()) {
			Mount mount = it.next();
			if (mount.match(path, context)) {
				status = Mojasef.delegate(collector, context, mount.getApplication());
			}
		}
		
		if (status == Token.CONTINUE) {
		    notfound(context);
		}
	}

    protected void notfound(StringKeeper context) {}
}
