package org.stringtree.mojasef.standalone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.stringtree.Repository;
import org.stringtree.mojasef.Alias;
import org.stringtree.mojasef.model.NonTerminalAlias;
import org.stringtree.mojasef.model.Server;
import org.stringtree.mojasef.model.TerminalAlias;
import org.stringtree.util.Diagnostics;
import org.stringtree.util.IntegerNumberUtils;
import org.stringtree.waiter.SelfWaiter;

enum ServerStatus {
    FRESH, INITIALIZING, INITIALIZED, STARTING, RUNNING, STOPPING, STOPPED;
}

public class WebServer extends Thread implements Repository {
    
    public static final String SERVER_PORT = "server.port";
    
    private Server core;
    private ServerStatus status;
    
    public WebServer(Server server) {
        core = server;
        status = ServerStatus.FRESH;
    }

    public WebServer(String URL, boolean lock) throws IOException {
        this(new Server(URL, lock));
    }

    public WebServer(String URL) throws IOException {
        this(URL, true);
    }

    public WebServer(Map<String, Object> settings, boolean lock) {
        this(new Server(settings, lock));
    }

    public WebServer(Map<String, Object> settings) {
        this(settings, true);
    }
    
    public WebServer(URL spec, URL templates, URL pub, Map<String, String> mimetypes, boolean lock) {
    	this(new Server(spec, templates, pub, mimetypes, lock));
    }

    public WebServer(URL spec, URL templates, URL pub, Map<String, String> mimetypes) {
        this(spec, templates, pub, mimetypes, true);
    }

    public WebServer(String specLocation, String templatesLocation, String publicLocation, Map<String, String> mimetypes, boolean lock) throws IOException {
    	this(new Server(specLocation, templatesLocation, publicLocation, mimetypes, lock));
    }
    
    public WebServer(Object app, String port, String templatesLocation, String publicLocation) throws IOException {
    	this(new Server(app, port, templatesLocation, publicLocation));
    }
    
    public WebServer(Object app, String port, List<String> templatesLocation, List<String> publicLocation) throws IOException {
    	this(new Server(app, port, templatesLocation, publicLocation));
    }

	public void addAlias(Alias alias) {
		core.addAlias(alias);
	}

	public void addAlias(String pattern, String rewrite) {
		core.addAlias(pattern, rewrite);
	}
    
    public void addTerminalAlias(String pattern, String rewrite) {
    	addAlias(new TerminalAlias(pattern, rewrite));
    }
    
    public void addNonterminalAlias(String pattern, String rewrite) {
    	addAlias(new NonTerminalAlias(pattern, rewrite));
    }

    private ServerSocket init() throws IOException {
	    advance(ServerStatus.INITIALIZING);
        int port = IntegerNumberUtils.intValue(getObject(SERVER_PORT, "8080"));
        ServerSocket ret = new ServerSocket(port);
        ret.setSoTimeout(100);
        System.out.println("listening on port " + ret.getInetAddress() + ":" + port);
        advance(ServerStatus.INITIALIZED);
	    
	    return ret;
    }

    public Object getObject(String name, String dfl) {
        Object ret = getObject(name);
        if (ret == null) {
            ret = dfl;
        }
        
        return ret;
    }

    public Object getObject(String name) {
        return core.getCommonContext().getObject(name);
    }
    
    public void put(String name, Object value) {
    	core.getCommonContext().put(name, value);
    }

	public void run() {
		ServerSocket listen = null;
        final ThreadGroup threads = new ThreadGroup("webserver");
	    
		try {
			listen = init();
			advance(ServerStatus.RUNNING);

		    while (isRunning() && !interrupted()) {
            	Socket socket = null;
				try {
					socket = listen.accept();
				} catch (SocketTimeoutException e) {
					continue;
				}
				
				ServerConnection connection = new ServerConnection(threads, socket, core);
				connection.start();
			}
		} catch (IOException e) {
			System.out.print("while listening: ");
            e.printStackTrace();
		} finally {
            threads.interrupt();
			new SelfWaiter() {
				public boolean done() {
					return 0 == threads.activeCount();
				}
			}.waitOrTimeout(1000);
            if (listen != null) {
				try {
					listen.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
			advance(ServerStatus.STOPPED);
			core.terminate();
            System.out.println("Server halted");
		}
	}

	public void halt() {
		advance(ServerStatus.STOPPING);
        super.interrupt();
    }

	public boolean isRunning() {
		return ServerStatus.STARTING == status || ServerStatus.RUNNING == status || ServerStatus.STOPPING == status;
	}
    
    public static void main(String args[]) throws IOException {
        String url = args.length > 0 ? args[0] : "http.spec";
        WebServer server = new WebServer(url, false);
        if (!"STOP".equals(server.getObject("MOJASEF_ABORT"))) {
            server.start();
        }
    }

    public void clear() {
    	core.getCommonContext().clear();
    }

    public boolean isLocked() {
        return core.getCommonContext().isLocked();
    }

    public void lock() {
    	core.getCommonContext().lock();
    }

    public void remove(String name) {
    	core.getCommonContext().remove(name);
    }
    
    public static void ensureStartup(final WebServer server, long timeout) {
    	if (null != server) {
     		server.start();
     		new SelfWaiter() {
    			public boolean done() {
    				return server.isRunning();
    			}
    		}.waitOrTimeout(timeout);
    	}
    }
    
    public static void ensureShutdown(final WebServer server, long timeout) {
    	if (null != server && server.isRunning()) {
    		server.halt();
    		try {
				server.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		if (!new SelfWaiter() {
    			public boolean done() {
    				return !server.isRunning();
    			}
    		}.waitOrTimeout(timeout)) {
    			server.log("Unable to shutdown server within " + timeout + "ms");
    		}
    	}
    }
    
    public static void ensureShutdown(final WebServer server) {
    	ensureShutdown(server, 1000L);
    }
    
    public ServerStatus advance(ServerStatus to) {
    	ServerStatus current = status;
    	if (status.ordinal() < to.ordinal()) {
    		status = to;
    	} else {
    		//Diagnostics.whereAmI();
    	}
		log("status change from " + current + " to " + status);
    	return status;
    }
   
    private void log(String message) {
    	//System.err.println(this.getName() + ": " + message);
    }
}