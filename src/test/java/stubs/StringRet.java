package stubs;

import org.stringtree.finder.StringFinder;
import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.MojasefConstants;

public class StringRet {
    public String request() {
		return "StringRet worked";
    }
    
    public String index() {
		return "StringRet index worked";
    }
    
    public String ugh() {
		return "StringRet ugh worked";
    }
    
    public String GET() {
		return "StringRet GET worked";
    }
    
    public String pp(StringFinder finder) {
		return "StringRet finder worked a='" + finder.get("a") + "' b='" + finder.get("b") + "'";
    }
    
    public String pt(StringFinder finder) {
		return "StringRet finder worked a='" + finder.get("a") + "' b='" + finder.get("b") + "'";
    }
    
    public String px(StringKeeper keeper) {
    	keeper.put(MojasefConstants.PAGE_TEMPLATE, "pt");
		return "StringRet finder worked a='" + keeper.get("a") + "' b='" + keeper.get("b") + "'";
    }
    
    public String pc(StringKeeper keeper) {
    	keeper.put(MojasefConstants.PAGE_TEMPLATE, "pz");
    	keeper.put("something", "else");
		return "coool";
    }
    
    public String pq(StringKeeper keeper) {
    	keeper.put("template.pq", "that's ${a}, ${this}");
		return "coool";
    }
}
