package stubs;

import java.util.Map;

import org.stringtree.finder.StringKeeper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.StringUtils;
import org.stringtree.util.iterator.Spliterator;

public class AuthExample {
    Map<String, String> known;
    String mapkey = "known";
    
    public AuthExample(String mapkey) {
        this.mapkey = mapkey;
    }
    
	@SuppressWarnings("unchecked")
    public void init(StringKeeper context) {
        known = (Map<String, String>)context.getObject(mapkey);
    }
    
    public void request(StringKeeper context) {
        String id = context.get(MojasefConstants.REQUEST_PATHOBJECT);

        String secret = known.get(id);
        if (StringUtils.isBlank(secret)) {
            context.put(HTTPConstants.RESPONSE_CODE, "404");
            return;
        }
        
        String credentials = context.get(HTTPConstants.REQUEST_HEADER_AUTHORIZATION);
        if (credentials.startsWith("Basic ")) {
            credentials = credentials.substring("Basic ".length());
        }

        Spliterator it = new Spliterator(credentials, ":");
        String credId = it.hasNext() ? it.nextString() : "";
        String credPin = it.hasNext() ? it.nextString() : "";
        
        if (!(id.equals(credId) && secret.equals(credPin))) {
            context.put(HTTPConstants.RESPONSE_CODE, "401");
            context.put(HTTPConstants.RESPONSE_HEADER_AUTHENTICATE, "Basic realm=\"private:" + id + "\"");
            return;
        }

        context.put(HTTPConstants.RESPONSE_CODE, "200");
        System.out.print("hello");
    }
}
