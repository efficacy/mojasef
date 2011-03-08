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


import java.io.File;


/**
 * This class is used as a data structure by HttpMultiPartParser.
 *
 * @see com.isavvix.tools.HttpMultiPartParser
 * @author  Anil Hemrajani
 */
public class FileInfo
{
    private String name            = null,
                   clientFileName  = null,
                   fileContentType = null;
    private byte[] fileContents    = null;
    private File   file            = null;
    private StringBuffer sb = new StringBuffer(100);
    

    // Param name
    public void   setName(String aName) { name = aName; }
    public String getName() { return name; }
    
    // File name
    public String getClientFileName() { return clientFileName; }
    public void   setClientFileName(String aClientFileName)
    {
       clientFileName = aClientFileName;
    }
    

    // File
    public void setLocalFile(File aFile) { file = aFile; }
    public File getLocalFile() { return file; }

    
    // File contents
    public byte[] getFileContents() { return fileContents; }
    public void   setFileContents(byte[] aByteArray)
    { 
        fileContents = new byte[aByteArray.length];
        System.arraycopy(aByteArray, 0, fileContents, 0, aByteArray.length);
    }
    

    // Content-type
    public String getFileContentType() { return fileContentType; }
    public void   setFileContentType(String aContentType)
    {
       fileContentType = aContentType;
    }
    
    
    public String toString()
    {
        sb.setLength(0);
        sb.append("               name = " + name     + "\n");
        sb.append("     clientFileName = " + clientFileName + "\n");
        
        if (file != null)
            sb.append("      File.toString = " + file
                     + " (size=" + file.length() + ")\n");
        else
            sb.append("fileContents.length = " + fileContents.length + "\n");
        
        return sb.toString();
    }
}

