package org.stringtree.mojasef.examples;

import org.stringtree.Fetcher;
import org.stringtree.fetcher.FetcherStringHelper;
import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.Token;
import org.stringtree.mojasef.apps.BasicApplication;

public class LoginExample extends BasicApplication {
    String contextKey = null;
    String identity = null;
    Fetcher users = null;

    // called to create the object, passed in the "tail" from the mount spec line
    // in this case the "tail" contans the name we used to store the map of users 
    //
    public LoginExample(String contextKey) {
        this.contextKey = contextKey;
    }
    
    // called by container after the object is created
    //
    public void init(StringFinder context) {
		users = (Fetcher)context.getObject(contextKey);
    }
    
    // called implicitly when the URL path ends with "logout"
    //
    public void logout() {
        identity = null;
        System.out.println("Goodbye ...");
    }

	// generating this using a template (or even loading from a file)
    // would probably be a better idea :)
    //
    static String form =
        "Welcome to Example Application, please log in:<p/>" +
        "<form method='POST'>" +
        " Username:<input name='username'/>" +
        " Password:<input type='password' name='password'/>" +
        " <input type='SUBMIT' name='LOGIN'/>" +
        "</form>";
    
    // catch-all handler, last choice if no more-specific names match
    //
	public Object request() {
	    // input parameters defined in login form
	    String name = FetcherStringHelper.getString(context, "username");
	    String pw = FetcherStringHelper.getString(context, "password");
	    
	    if (identity == null && pw.equals(users.getObject(name))) {
	        identity = name;
	    }

	    if (identity != null) {
		    StorerHelper.put(context, "user.name", identity);

			// continue processing other mounted applications
			return Token.CONTINUE;
	    }
	    
	    System.out.println(form);
	    
	    // any return other than HTTP.CONTINUE means the request is finished
		return null;
	}
}
