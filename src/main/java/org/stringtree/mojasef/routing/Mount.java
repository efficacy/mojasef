package org.stringtree.mojasef.routing;

import org.stringtree.finder.StringKeeper;

public interface Mount {
    public boolean match(String path, StringKeeper tokens);
    public Object getApplication();
}
