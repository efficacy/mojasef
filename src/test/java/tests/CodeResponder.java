package tests;

import org.stringtree.finder.StringKeeper;

public class CodeResponder {
    String code;
    
    public CodeResponder(String code) {
        this.code = code;
    }

    public void request(StringKeeper context) {
        context.put("http.response.code", code);
    }
}
