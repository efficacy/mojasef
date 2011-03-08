package org.stringtree.mojasef.apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.stringtree.fetcher.StorerHelper;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.HTTPConstants;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.StreamUtils;
import org.stringtree.util.StringUtils;

public class FileTreeServer {
	protected File root = null;
	
	public FileTreeServer(String root) {
	    this.root = new File(root);
	}
	
	public FileTreeServer() {
	    this.root = null;
	}
		
	public void mojasef_init(StringFinder context) {
		String dir = context.get("server.dir");
		if (root == null) this.root = new File(dir, "public_html");
	}

	public void request(StringFinder context) {
		OutputStream out = (OutputStream)context.getObject("response.stream");
		showFile(context.get(MojasefConstants.REQUEST_PREFIX+"localpath"), out, context);
	}
	
	private static void writeDirEntry(PrintStream writer, String file) {
		writer.println("<li><a href=\"" + file + "\">" +  file + "</a>");
	}
	
	private void showDirectory(File file, OutputStream stream, String URL) {
		PrintStream out = StreamUtils.ensurePrint(stream);
		out.print("<html><head><title>Index of ");
		out.print(URL);
		out.println("</title></head>");
		out.print("<body><h1>Index of ");
		out.print(URL);
		out.println("</h1>");
		out.println("<ul>");
		
		writeDirEntry(out, "..");

		String[] names = file.list();
		for (int i = 0; i < names.length; ++i) {
			File entry = new File(file,names[i]);
			String name = entry.getName();
     		
			if (entry.isDirectory()) {
				name += "/";
			}
			writeDirEntry(out, name);
		}
		out.println("</ul></body></html>");
		out.flush();
	}
	
	private void showFile(Object URL, OutputStream out, StringFinder context) {
		String filename = StringUtils.nullToEmpty(URL);
		File file = new File(root, filename);
		try {
			if (file.isDirectory()) {
				showDirectory(file, out, filename);
			}
			
			else if (file.canRead()) { // ordinary file 
			    String extension = filename.substring(filename.lastIndexOf("."));
			    Object type = context.get("filetype" + extension.toLowerCase());
			    if (type != null) {
			        StorerHelper.put(context, HTTPConstants.RESPONSE_CONTENT_TYPE, type);
			    }
			    StreamUtils.copyStream(new FileInputStream(file), out, true);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
