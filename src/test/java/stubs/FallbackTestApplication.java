package stubs;

import org.stringtree.Fetcher;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.template.Templater;

public class FallbackTestApplication {
    public void home(StringKeeper context) {
        context.put("value", "thing");
    }
    
    public void select(StringKeeper context) {
        String type = context.get("type");
        Fetcher source = (Fetcher) context.getObject(MojasefConstants.TEMPLATE_SOURCE + "." + type);
        context.put(Templater.TEMPLATE_SOURCE, source);
        context.put("value", "wotsit");
        context.put(MojasefConstants.PAGE_TEMPLATE, "home");
    }
}
