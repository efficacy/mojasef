package tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.stringtree.Repository;
import org.stringtree.fetcher.MapFetcher;
import org.stringtree.fetcher.hierarchy.FlatHierarchyHelper;
import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.StreamUtils;
import org.stringtree.xml.XMLLoader;

public class LocalApp {
    XMLLoader loader = new XMLLoader(new FlatHierarchyHelper());
    Repository parsed = new MapFetcher();
    
    public String ugh(StringFinder context) {
        InputStream in = (InputStream)context.getObject(MojasefConstants.REQUEST_STREAM); 
        try {
            loader.load(parsed, new InputStreamReader(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "hello " + parsed.getObject("//request[1]");
    }
    
    public String ptest(StringFinder context) {
        return context.get("TEXT") + " from " + context.get("twins");
    }
    
    public String pstest(StringFinder context) throws IOException {
        InputStream content = (InputStream)context.getObject(MojasefConstants.REQUEST_STREAM);
        String body = StreamUtils.readStream(content,false);
        return "hello from " + body + " (" + context.get(HTTPConstants.REQUEST_CONTENT_TYPE) + ")";
    }
    
    public void polluted(StringKeeper context) {
        context.put("list", new String[] { "aa", "bb" });
        System.getProperty("line.separator");
        System.setProperty("line.separator", "zzz");
        System.out.println("yahoo\nugh");
    }
}
