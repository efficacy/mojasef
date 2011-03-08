package org.stringtree.mojasef.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.mojasef.model.RequestContext;
import org.stringtree.mojasef.model.Server;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.util.StreamUtils;
import org.stringtree.util.StringUtils;

class ServerConnection extends Thread {
	public static final String LOG_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	private Socket socket = null;
	private Server core;
	
	public ServerConnection(ThreadGroup parent, Socket socket, Server core) {
		super(parent, "connection " + System.currentTimeMillis());
	    this.socket = socket;
        this.core = core;
	}
	
	public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            RequestContext req = new RequestContext(core.getCommonContext(), core.getAliases());
            StandaloneOutputCollector res = new StandaloneOutputCollector(req);
            if (extractRequest(in, req)) {
                core.request(req, res);
                res.write(out);
                log(req, res);
            } else {
                res.write(out, null, null);
            }
            //out.close();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
//			try { socket.close(); }
//			catch(IOException e) {
//                e.printStackTrace();
//            }
		}
	}

	private static Object sync = new Object();
	
    private void log(RequestContext req, StandaloneOutputCollector res) {
    	DateFormat df = new SimpleDateFormat(LOG_TIME_FORMAT);
        StringBuilder buf = new StringBuilder()
        	.append(df.format(System.currentTimeMillis()))
        	.append(" ")
        	.append(req.getObject(HTTPConstants.REMOTE_ADDRESS))
        	.append(" ")
        	.append(res.getResponseCode())
        	.append(" ")
        	.append(req.getObject(HTTPConstants.HTTP_REQUEST));
        
        synchronized (sync) {
        	System.out.println(buf);
		}
    }

    protected boolean extractRequest(InputStream in, RequestContext context) throws IOException {
        String request = StreamUtils.readLine(in);
        
        StringTokenizer tok = new StringTokenizer(request);
        if (tok.hasMoreTokens()) {
	        String method = tok.nextToken();
	        String uri = tok.nextToken();
	        String protocol = tok.nextToken();
	        String type = null;
	        
	        if ("/.".equals(uri)) { // a "secret" signal to remind the server to check its stop flag
	            return false;
	        }
	        
	        String remote = socket.getInetAddress().getHostAddress();
	        context.put(HTTPConstants.REMOTE_ADDRESS, remote);
	        context.put(MojasefConstants.REQUEST_STREAM, in);
	
	        int length = 0;
	        String line = StreamUtils.readLine(in);
	        while (!StringUtils.isBlank(line)) {
	            if (line.contains(":")) {
	                int sep = line.indexOf(':');
	                String name = line.substring(0, sep).trim();
	                String value = line.substring(sep + 1).trim();
	                context.setHeader(name, value);
	                if (HTTPConstants.CONTENT_TYPE.equals(name)) type = value;
	                if (HTTPConstants.CONTENT_LENGTH.equals(name)) length = IntegerNumberUtils.intValue(value);
	            }
	            line = StreamUtils.readLine(in);
	        }
	        
	        String query = context.readParametersFromBody(in, context.extractContentType(type), length);
	
	        context.setRequest(protocol, method, uri, query);
        } else {
        	System.err.println("invalid incoming request: [" + request + "]");
        }
        
        return true;
    }
}