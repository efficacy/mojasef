package org.stringtree.mojasef.examples;

import org.stringtree.Fetcher;
import org.stringtree.mojasef.Mojasef;

public class MethodExample
{
    int n = 0;
    
    public void index()
    {
Mojasef.log("got a call to 'index', System.out=" + System.out);
        System.out.println("this is the index<br>");
		System.out.println("<a href='blob'>click here</a><br>");
		System.out.println("<form method=POST action='wibble'>Size: <input name='size'> <input type=SUBMIT></form><br>");
		System.out.println("called " + n++ + " times");
    }

    public void blob()
    {
		System.out.println("Yahoo!, you found the blob!<br>");
    }

    public void POST(Fetcher context)
    {
		System.out.println("Generic post handler, size='" + context.getObject("size") + "'");
    }
}
