package tests;

import java.io.IOException;
import java.io.InputStream;

import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.StreamUtils;
import org.stringtree.util.Utils;

public class StandaloneInitTerminateTestApplication {

    public StringBuffer out;
    
    public StandaloneInitTerminateTestApplication() {
        out = new StringBuffer();
    }
    
    public void mojasef_init(StringFinder context) {
        log("init(" + context.get("example") + "),");
        Utils.sleep(500);
    }
    
    protected void log(String string) {
        out.append(string);
    }

    public void mojasef_terminate() {
        log("terminate.");
    }
    
    public String index(StringFinder context) {
        log("index(" + context.get("example") + "),");
        return "hello";
    }
    
    public String multi(StringFinder context) throws IOException {
    	int len = IntegerNumberUtils.intValue(context.getObject(HTTPConstants.REQUEST_HEADER + "Content-Length"));
        log("multi(" + StreamUtils.readStream((InputStream)context.getObject(MojasefConstants.REQUEST_STREAM), len));
        return "hello";
    }
    
    public String slow() {
    	Utils.sleep(500);
    	return "done";
    }
    
    public String action(StringFinder context) {
        String text = "action(" + context.get("first") + "," + context.get("second") + ")";
        log(text + ",");
        return text;
    }
}
