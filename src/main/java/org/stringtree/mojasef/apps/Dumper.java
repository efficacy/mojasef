package org.stringtree.mojasef.apps;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.stringtree.Fetcher;
import org.stringtree.Listable;
import org.stringtree.finder.StringFinder;
import org.stringtree.mojasef.MojasefConstants;
import org.stringtree.util.StreamUtils;
import org.stringtree.util.sort.UniqueSortedIteratorIterator;

public class Dumper {
	private StringFinder initContext;
	
	public void init(StringFinder context) 	{
		this.initContext = context;
	}

	public void request(StringFinder context) {
		PrintStream out = StreamUtils.ensurePrint((OutputStream)context.getObject(MojasefConstants.RESPONSE_STREAM));

		out.println("<h2>init context:</h2>");
		show(initContext, out);

		out.println("<h2>request context:</h2>");
		show(context, out);
	}

	public static void show(StringFinder context, PrintStream out) 	{
		@SuppressWarnings("unchecked")
		Listable<String> list = (Listable<String>)context.getObject(Listable.LIST); 

		if (list != null) 		{
			out.println("<p><table>");
			out.println("<tr><th align='left'>Name</th><th align='left'>Value</th></tr>");
		
			Iterator<String> it = new UniqueSortedIteratorIterator<String>(list.list());
			while (it.hasNext()) {
				String key = it.next(); 
				out.print("<tr><td>");
				out.print(key);
				out.print("</td><td>");
				out.print(context.get(key));
				out.println("</td></tr>");
			}

			out.println("</table>");
		} else {
			out.println("sorry, can't list context :(<br/>");
		}
	}

    public static void dump(String title, Fetcher context) {
		@SuppressWarnings("unchecked")
        Listable<String> list = (Listable<String>)context.getObject(Listable.LIST);
        PrintStream out = System.out;

        if (list != null) {
            out.println(title);
        
            Iterator<String> it = new UniqueSortedIteratorIterator<String>(list.list());
            while (it.hasNext()) {
                String key = it.next(); 
                out.print(key);
                out.print(" => ");
                out.println(context.getObject(key));
            }
        } else {
            out.println("sorry, can't list context :(<br/>");
        }
    }
}
