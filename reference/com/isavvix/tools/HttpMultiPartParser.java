/**
 * Copyright(c) 2001 iSavvix Corporation (http://www.isavvix.com/)
 *
 *                        All rights reserved
 *
 * Permission to use, copy, modify and distribute this material for
 * any purpose and without fee is hereby granted, provided that the
 * above copyright notice and this permission notice appear in all
 * copies, and that the name of iSavvix Corporation not be used in
 * advertising or publicity pertaining to this material without the
 * specific, prior written permission of an authorized representative of
 * iSavvix Corporation.
 *
 * ISAVVIX CORPORATION MAKES NO REPRESENTATIONS AND EXTENDS NO WARRANTIES,
 * EXPRESS OR IMPLIED, WITH RESPECT TO THE SOFTWARE, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR ANY PARTICULAR PURPOSE, AND THE WARRANTY AGAINST
 * INFRINGEMENT OF PATENTS OR OTHER INTELLECTUAL PROPERTY RIGHTS.  THE
 * SOFTWARE IS PROVIDED "AS IS", AND IN NO EVENT SHALL ISAVVIX CORPORATION OR
 * ANY OF ITS AFFILIATES BE LIABLE FOR ANY DAMAGES, INCLUDING ANY
 * LOST PROFITS OR OTHER INCIDENTAL OR CONSEQUENTIAL DAMAGES RELATING
 * TO THE SOFTWARE.
 *
 */

package com.isavvix.tools;

import java.io.*;
import java.util.*;

import javax.servlet.*;

/**
  * This class provides methods for parsing a HTML multi-part form.  Each
  * method returns a Hashtable which contains keys for all parameters sent
  * from the web browser.  The corresponding values are either type "String"
  * or "FileInfo" depending on the type of data in the corresponding part.
  * <P>
  * The following is a sample InputStream expected by the methods in this
  * class:<PRE>

    -----------------------------7ce23a18680
    Content-Disposition: form-data; name="SomeTextField1"

    on
    -----------------------------7ce23a18680
    Content-Disposition: form-data; name="LocalFile1"; filename="C:\temp\testit.c"
    Content-Type: text/plain

    #include <stdlib.h>


    int main(int argc, char **argv)
    {
       printf("Testing\n");
       return 0;
    }

    -----------------------------7ce23a18680--
    </PRE>
 * @see com.isavvix.tools.FileInfo
 * @author  Anil Hemrajani
*/
public class HttpMultiPartParser
{
    private final int ONE_MB=1024*1024*1;
    

    /** 
     * Parses the InputStream, separates the various parts and returns
     * them as key=value pairs in a Hashtable.  Any incoming files are
     * saved in directory "saveInDir" using the client's file name; the
     * file information is stored as java.io.File object in the Hashtable
     * ("value" part).
     */
    public Hashtable parseData(ServletInputStream data,
                               String boundary,
                               String saveInDir)
                     throws IllegalArgumentException, IOException
    {
        return processData(data, boundary, saveInDir);
    }
    

    /** 
     * Parses the InputStream, separates the various parts and returns
     * them as key=value pairs in a Hashtable.  Any incoming files are
     * saved as byte arrays; the file information is stored as java.io.File
     * object in the Hashtable ("value" part).
     */
    public Hashtable parseData(ServletInputStream data,
                               String boundary)
                     throws IllegalArgumentException, IOException
    {
        return processData(data, boundary, null);
    }
    
    
    private Hashtable processData(ServletInputStream is,
                                  String boundary,
                                  String saveInDir)
                             throws IllegalArgumentException, IOException
    {
        if (is == null)
            throw new IllegalArgumentException("InputStream");

        if (boundary == null || boundary.trim().length() < 1)
            throw new IllegalArgumentException("boundary");


        // Each content will begin with two dashes "--" plus the actual boundary string
        boundary = "--" + boundary; 


        StringTokenizer stLine = null, stFields = null;
        FileInfo fileInfo = null;
        Hashtable dataTable = new Hashtable(5);
        String line = null, field = null, paramName = null;
        boolean saveFiles=(saveInDir != null && saveInDir.trim().length() > 0),
                isFile = false;


        // Create output directory in case it doesn't exist
        if (saveFiles)
        {
            File f = new File(saveInDir);
            f.mkdirs();
        }


        line = getLine(is);
        if (line == null || !line.startsWith(boundary))
            throw new IOException("Boundary not found;"
                                 +" boundary = " + boundary
                                 +", line = "    + line);
        
            
        while (line != null)
        {
            // Process boundary line  ----------------------------------------
            if (line == null || !line.startsWith(boundary))
                return dataTable;


            // Process "Content-Disposition: " line --------------------------
            line = getLine(is);
            if (line == null)
                return dataTable;
                

            // Split line into the following 3 tokens (or 2 if not a file):
            // 1. Content-Disposition: form-data
            // 2. name="LocalFile1"
            // 3. filename="C:\autoexec.bat"  (only present if this is part of a HTML file INPUT tag) */
            stLine = new StringTokenizer(line, ";\r\n");
            if (stLine.countTokens() < 2)
                throw new IllegalArgumentException("Bad data in second line");


            // Confirm that this is "form-data"
            line = stLine.nextToken().toLowerCase();
            if (line.indexOf("form-data") < 0)
                throw new IllegalArgumentException("Bad data in second line");


            // Now split token 2 from above into field "name" and it's "value"
            // e.g. name="LocalFile1"
            stFields = new StringTokenizer(stLine.nextToken(), "=\"");
            if (stFields.countTokens() < 2)
                throw new IllegalArgumentException("Bad data in second line");
                

            // Get field name
            fileInfo = new FileInfo();
            stFields.nextToken();
            paramName = stFields.nextToken();


            // Now split token 3 from above into file "name" and it's "value"
            // e.g. filename="C:\autoexec.bat"
            isFile = false;
            if (stLine.hasMoreTokens())
            {
                field    = stLine.nextToken();
                stFields = new StringTokenizer(field, "=\"");
                if (stFields.countTokens() > 1)
                {
                    if (stFields.nextToken().trim().equalsIgnoreCase("filename"))
                    {
                        fileInfo.setName(paramName);
                        String value = stFields.nextToken();
                        if (value != null && value.trim().length() > 0)
                        {
                            fileInfo.setClientFileName(value);
                            isFile = true;
                        }
                        else
                        {
                            // An error condition occurred, skip to next boundary
                            line = getLine(is); // Skip "Content-Type:" line
                            line = getLine(is); // Skip blank line
                            line = getLine(is); // Skip blank line
                            line = getLine(is); // Position to boundary line
                            continue;
                        }
                    }
                }
                else
                if (field.toLowerCase().indexOf("filename") >= 0)
                {
                    // An error condition occurred, skip to next boundary
                    line = getLine(is); // Skip "Content-Type:" line
                    line = getLine(is); // Skip blank line
                    line = getLine(is); // Skip blank line
                    line = getLine(is); // Position to boundary line
                    continue;
                }
            }


            // Process "Content-Type: " line ----------------------------------
            // e.g. Content-Type: text/plain
            boolean skipBlankLine = true;
            if (isFile)
            {
                line = getLine(is);
                if (line == null)
                    return dataTable;

                // "Content-type" line not guaranteed to be sent by the browser
                if (line.trim().length() < 1)
                    skipBlankLine = false; // Prevent re-skipping below
                else
                {
                    stLine = new StringTokenizer(line, ": ");
                    if (stLine.countTokens() < 2)
                        throw new IllegalArgumentException("Bad data in third line");

                    stLine.nextToken(); // Content-Type
                    fileInfo.setFileContentType(stLine.nextToken());
                }
            }


            // Skip blank line  -----------------------------------------------
            if (skipBlankLine)  // Blank line already skipped above?
            {
                line = getLine(is);
                if (line == null)
                    return dataTable;
            }
                

            // Process data: If not a file, add to hashtable and continue
            if (!isFile)
            {
                line = getLine(is);
                if (line == null)
                    return dataTable;
                
                dataTable.put(paramName, line);
                line = getLine(is);
                
                continue;
            }


            // Either save contents in memory or to a local file
            try
            {
                OutputStream os = null;
                String path     = null;
                if (saveFiles)
                    os = new FileOutputStream(path = getFileName(saveInDir,
                                               fileInfo.getClientFileName()));
                else
                    os = new ByteArrayOutputStream(ONE_MB);


                // Read till next boundary and write contents to OutputStream
                boolean readingContent = true;
                byte b[] = new byte[2 * ONE_MB], b2[] = null;
                int read;

                while (readingContent)
                {
                    if ((read = is.readLine(b, 0, b.length)) == -1)
                    {
                        line = null;
                        break;
                    }

                    // If it's a blank line, hang on to it for next few lines
                    if (read < 3) // < 3 means CR and LF or just LF
                    {
                        b2 = new byte[read];
                        System.arraycopy(b, 0, b2, 0, b2.length);
                        if ((read = is.readLine(b, 0, b.length)) == -1)
                        {
                            line = null;
                            break;
                        }
                    }
                   
                    if (compareBoundary(boundary, b))
                    {
                        line = new String(b, 0, read);
                        break;
                    }
                    else
                    if (b2 != null) // Prev line was not a boundary line
                    {
                        os.write(b2);
                        b2 = null;
                    }

                    os.write(b, 0, read);
                    os.flush();
                }

                os.close();
                b  = null;

                if (!saveFiles)
                {
                    ByteArrayOutputStream baos = (ByteArrayOutputStream)os;
                    fileInfo.setFileContents(baos.toByteArray());
                }
                else
                {
                    fileInfo.setLocalFile(new File(path));
                    os = null;
                }

                dataTable.put(paramName, fileInfo);
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        return dataTable;
    }


    // Compares boundary string to byte array
    private boolean compareBoundary(String boundary, byte ba[])
    {
        if (boundary == null || ba == null)
            return false;

        for (int i=0; i < boundary.length(); i++)
           if ((byte)boundary.charAt(i) != ba[i])
               return false;

        return true;
    }


    /** Convenience method to read HTTP header lines */
    private synchronized String getLine(ServletInputStream sis)
                                throws IOException
    {
        byte   b[]  = new byte[1024];
        int    read = sis.readLine(b, 0, b.length), index;
        String line = null;

        if (read != -1)
        {
           line = new String(b, 0, read);

           if ((index = line.indexOf('\n')) >= 0)
               line   = line.substring(0, index-1);
        }

        b = null;
        return line;
    }


    /**
     * Concats the directory and file names.
     */
    private String getFileName(String dir, String fileName)
                   throws IllegalArgumentException
    {
        String path = null;

        if (dir == null || fileName == null)
            throw new IllegalArgumentException("dir or fileName is null");

        int   index = fileName.lastIndexOf('/');
        String name = null;
        if (index >= 0)
            name = fileName.substring(index + 1);
        else
            name = fileName;

        index = name.lastIndexOf('\\');
        if (index >= 0)
            fileName = name.substring(index + 1);

        path = dir + File.separator + fileName;
        if (File.separatorChar == '/')
            return path.replace('\\', File.separatorChar);
        else
            return path.replace('/',  File.separatorChar);
    }
}
