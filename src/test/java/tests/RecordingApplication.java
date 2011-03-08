package tests;

import java.util.ArrayList;
import java.util.List;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.StringUtils;

public class RecordingApplication {
    public List<String> events;
    private String name;
    
    public RecordingApplication(String name) {
        this.name = StringUtils.isBlank(name) ? "unknown" : name;
        events = new ArrayList<String>();
    }
    
    public void a(StringKeeper context) {
        record("a[" + context.get("id") + "]", context);
    }
    
    public void request(StringKeeper context) {
        record("request", context);
    }
    
    public void GET(StringKeeper context) {
        record("GET", context);
    }

    private void record(String method, StringKeeper context) {
        String text = name + ":" + method + "(" + context.get(MojasefConstants.REQUEST_PATHTAIL) + ").";
        events.add(text);
        System.out.print(text);
    }
}
