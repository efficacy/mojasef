package tests;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;

public class PluginApplication {
    
    public void simple(StringKeeper context) {
       // nothing 
    }
    
    public void complex(StringKeeper context) {
        context.put(MojasefConstants.PAGE_TEMPLATE, "simple");
    }

}
