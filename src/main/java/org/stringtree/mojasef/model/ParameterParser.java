package org.stringtree.mojasef.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.stringtree.Storer;
import org.stringtree.fetcher.RepositoryHelper;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.StringUtils;
import org.stringtree.util.URLUtils;

public class ParameterParser {
	public static void parse(String query, Storer storer) {
        ParameterCollection args = new ParameterCollection(); 
		addParameters(query, storer, args);
        storer.put(MojasefConstants.COMMAND_ARGS_OBJECT, args);
	}
			
	public static void parse(String query, String headerPrefix, String cookiePrefix, 
	    BufferedReader in, Storer storer) {
        ParameterCollection args = new ParameterCollection(); 

        if (in != null) {
			try {
				parseInput(headerPrefix, cookiePrefix, in, storer, args);
			}
			catch(IOException e) {
                e.printStackTrace();
            }
		}
		
        // the query string overrides the post data, so do it last
		//
		addParameters(query, storer, args);
        storer.put(MojasefConstants.COMMAND_ARGS_OBJECT, args);
	}

	private static void parseInput(String headerPrefix, String cookiePrefix, 
	    BufferedReader in, Storer storer, ParameterCollection args) throws IOException {
        
		// read any header lines into the header and cookie tables
		//
        String line = null;
        
        for (line = in.readLine(); line != null && line.length() > 0; line = in.readLine()) {
            int colon = line.indexOf(":"); 
            if (colon > 0) {
				String name = line.substring(0, colon).trim();
				String body = line.substring(colon+1).trim();
                if(name.equals("Cookie")) {
					StringTokenizer cooks = new StringTokenizer(body,";");
					while (cooks.hasMoreTokens()) {
					    String cookie = cooks.nextToken().trim();
					    StringTokenizer cook = new StringTokenizer(cookie,"=");
						if (cook.hasMoreTokens()) {
							String cookieName = cook.nextToken();
							if (cook.hasMoreTokens()) {
								storer.put(cookiePrefix+cookieName, cook.nextToken());
							}
						}
					}
				} else {
					storer.put(headerPrefix+name, body);
				}
			}
        }
        
		int length = IntegerNumberUtils.intValue(RepositoryHelper.getObject(storer, HTTPConstants.RESPONSE_CONTENT_LENGTH));
		String type = RepositoryHelper.getString(storer, HTTPConstants.RESPONSE_CONTENT_TYPE);

		if (length > 0) {
			if (HTTPConstants.APPLICATION_X_WWW_FORM_URLENCODED.equals(type)) {
	            char[] buf = new char[length];
	            in.read(buf);
	            line = new String(buf);
	            addParameters(line, storer, args);
			} else if (type.startsWith(HTTPConstants.MULTIPART_FORM_DATA)) {
				// TODO decode multipart into parameters
			}
		}
	}
	
	private static void addParameters(String params, Storer storer, ParameterCollection args) {
	    if (StringUtils.isBlank(params)) return;
	    
		StringTokenizer tok = new StringTokenizer(params, "&");
        Storer[] storers = new Storer[] { storer, args.getStorer() };
		while (tok.hasMoreTokens()) {
            String param = tok.nextToken(); 
            addParameter(param, storers, "=");
		}
	}

	protected static void addParameter(String param, Storer storers[], String sep) {
		String name = null;
		String value = null;
		
		StringTokenizer tok = new StringTokenizer(param, sep);
		if (tok.hasMoreTokens()) {
			name = URLUtils.unescape(tok.nextToken());
		}
		
		if (tok.hasMoreTokens()) {
			value = URLUtils.unescape(tok.nextToken());
		}
		
		if (name != null && value != null) {
            for (int i = 0; i < storers.length; ++i) {
                storers[i].put(name, value);
            }
		}
	}

    protected static void addParameter(String param, Storer storers[]) {
        addParameter(param, storers, "=");
    }
    
    protected static void addParameter(String param, Storer storer, String sep) {
        addParameter(param, new Storer[] { storer }, sep);
    }
    
    protected static void addParameter(String param, Storer storer) {
        addParameter(param, storer, "=");
    }
}
