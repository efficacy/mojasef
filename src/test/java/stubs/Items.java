package stubs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.stringtree.fetcher.MapFetcher;
import org.stringtree.finder.FetcherStringKeeper;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.rest.CollectionPersonality;
import org.stringtree.util.ReaderUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.spec.SpecReader;
import org.stringtree.xml.XMLReader;

public class Items extends MapFetcher implements CollectionPersonality {
    
    private int seq = 9900;

    public Items(String tail) throws IOException {
        if (!StringUtils.isBlank(tail)) {
            SpecReader.load(new FetcherStringKeeper(this), tail);
        }
    }

    public Items() {
        // don't load anything
    }

    public Object parse(StringKeeper context, String id, InputStream in) throws IOException {
        XMLReader reader = new XMLReader();
        reader.setIgnoreRoot(true);
        String xml = ReaderUtils.readInputStream(in);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) reader.read(xml);
        return new Item(body.get("id"), body.get("name"));
    }

    public Object empty(StringKeeper context, String id) {
        return new Item(id, "");
    }
    
    public synchronized String createId(StringKeeper context) {
        return Integer.toString(++seq);
    }

    public boolean validate(StringKeeper context, String id) {
        int ret = -1;
        try {
            ret = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            ret = -1;
        }
        return ret > 100;
    }

    public void list(StringKeeper context) {
        context.put("list", list());
        context.put(MojasefConstants.PAGE_TEMPLATE, "itemlist");
    }
}
